package osrs.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Npc;
import osrs.Helper;
import osrs.Task;
import osrs.assets.ITEM;

import java.util.concurrent.Callable;

public class CombatTask extends Task {
    public CombatTask(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public void execute() {

    }


    private void attackNPC(int npcId) {
        Npc monster = ctx.npcs.select().id(npcId).nearest()
                .sort((n1, n2) -> Boolean.compare(n1.inViewport(), n2.inViewport()))
                .limit(5)
                .select(npc -> !npc.interacting().valid() && npc.tile().matrix(ctx).reachable())
                .poll();

        if (monster.valid()) {
            monster.interact("Attack");
        }

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                heal();
                return monster.interacting().healthPercent() > 0;
            }
        }, Helper.smartSecondsGen(), 4);
    }

    private void heal() {
        Item shrimp = ctx.inventory.select().id(ITEM.SHRIMP).poll();
        if (shrimp.valid() && ctx.combat.healthPercent() < 60) {
            shrimp.interact("Eat");
        }
    }
}
