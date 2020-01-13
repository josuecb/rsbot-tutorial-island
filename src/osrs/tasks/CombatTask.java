package osrs.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.*;
import osrs.Helper;
import osrs.TabChanger;
import osrs.Task;
import osrs.assets.ITEM;
import osrs.assets.NPC;

import java.util.Comparator;
import java.util.concurrent.Callable;

public class CombatTask extends Task {
    Type type;
    int NPCid;

    public CombatTask(ClientContext ctx) {
        super(ctx);
        this.type = Type.MELEE;
        this.NPCid = NPC.GIANT_RAT;
    }

    public CombatTask(ClientContext ctx, Type type) {
        super(ctx);
        this.type = type;
        this.NPCid = NPC.GIANT_RAT;
    }

    public CombatTask(ClientContext ctx, Type type, int NPCid) {
        super(ctx);
        this.type = type;
        this.NPCid = NPCid;
    }

    @Override
    public boolean activate() {
        return !ctx.players.local().healthBarVisible() && !ctx.players.local().inMotion();
    }

    @Override
    public void execute() {
        attackNPC(this.NPCid);
    }


    private void attackNPC(int npcId) {
        if (this.underAttackAlready())
            this.underAttackWarning().click();

        if (!ctx.players.local().healthBarVisible() && !ctx.players.local().inMotion()) {
            this.switchGear();

            Npc monster = ctx.npcs.select().id(npcId).nearest()
                    .sort((n1, n2) -> Boolean.compare(n1.inViewport(), n2.inViewport()))
                    .limit(5)
                    .select(npc -> !npc.interacting().valid() && npc.tile().matrix(ctx).reachable())
                    .poll();

            if (this.type == Type.RANGE || this.type == Type.MAGIC) {
                monster = ctx.npcs.select().id(npcId).nearest()
                        .sort(Comparator.comparingDouble(n -> n.tile().distanceTo(ctx.players.local().tile())))
                        .select(npc -> !npc.interacting().valid())
                        .poll();
            }

            ctx.camera.turnTo(monster);

            System.out.println("Distance: " + monster.tile().distanceTo(ctx.players.local().tile()));
            System.out.println(monster.name());

            if (monster.valid()) {
                if (this.type == Type.MAGIC) {
                    TabChanger t = new TabChanger(ctx);
                    t.changeTo(Game.Tab.MAGIC);

                    if (ctx.magic.ready(Magic.Spell.WIND_STRIKE) && !ctx.magic.cast(Magic.Spell.WIND_STRIKE)) {
                        ctx.magic.cast(Magic.Spell.WIND_STRIKE);
                        Helper.smartSecondsGen();
                        System.out.println("Casting");
                        monster.interact("Cast");
                    }
                } else {
                    System.out.println("ATTACKING NOW");
                    System.out.println(ctx.players.local().animation());
                    System.out.println(ctx.players.local().animation());
                    monster.interact("Attack");
                }
            } else {
                System.out.println("monster is not valid");
            }

            Npc finalMonster = monster;
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    heal();
                    return finalMonster.interacting().healthPercent() > 0 || !finalMonster.valid();
                }
            }, Helper.smartSecondsGen(), 4);
        } else {
            System.out.println("Combating already");
        }
    }

    private void heal() {
        Item shrimp = ctx.inventory.select().id(ITEM.SHRIMP).poll();
        if (this.hasFood(shrimp) && this.lowHealth()) {
            shrimp.interact("Eat");
        } else {
            System.out.println("Didn't eat anything.");
        }
    }


    private boolean hasFood(Item food) {
        return food.valid();
    }

    private boolean lowHealth() {
        return ctx.combat.healthPercent() < 60;
    }








    private void switchToMelee() {
        int mainHandID = ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id();
        int shieldHandID = ctx.equipment.itemAt(Equipment.Slot.OFF_HAND).id();

        if (mainHandID != ITEM.BRONZE_SWORD)
            wieldITem(ITEM.BRONZE_SWORD);

        Condition.sleep(Helper.smartSecondsGen());

        if (shieldHandID != ITEM.WOODEN_SHIELD)
            wieldITem(ITEM.WOODEN_SHIELD);
        Condition.sleep(Helper.smartSecondsGen());
    }

    private void switchToRange() {
        int mainHandID = ctx.equipment.itemAt(Equipment.Slot.MAIN_HAND).id();
        int arrowWeaponID = ctx.equipment.itemAt(Equipment.Slot.QUIVER).id();

        if (mainHandID != ITEM.SHORT_BOW)
            wieldITem(ITEM.SHORT_BOW);

        Condition.sleep(Helper.smartSecondsGen());

        if (arrowWeaponID != ITEM.BRONZE_SWORD)
            wieldITem(ITEM.BRONZE_ARROW);
        Condition.sleep(Helper.smartSecondsGen());
    }

    private void switchToMage() {

    }

    private void wieldITem(int wieldItemId) {
        Item bronzeWord = ctx.inventory.select().id(wieldItemId).poll();
        if (bronzeWord.valid()) {
            bronzeWord.interact("Wield");
        } else {
            System.out.println("Item is not on inventory");
        }
    }

    public static enum Type {
        MELEE,
        RANGE,
        MAGIC
    }

    public boolean underAttackAlready() {
        return this.underAttackWarning().visible();
    }

    public Component underAttackWarning() {
        return ctx.widgets.component(162, 45);
    }

    public void switchGear() {
        TabChanger tabChanger = new TabChanger(ctx);
        tabChanger.changeTo(Game.Tab.INVENTORY);

        switch (this.type) {
            case MELEE:
                this.switchToMelee();
                break;
            case RANGE:
                this.switchToRange();
                break;
            case MAGIC:
                this.switchToMage();
                break;
        }
    }
}
