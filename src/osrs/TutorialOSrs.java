package osrs;

import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import osrs.assets.*;
import osrs.tasks.*;

import java.util.ArrayList;
import java.util.List;

@Script.Manifest(name = "Tutrial", description = "OSRS Tutorial", properties = "client=4; author=Josue; topic=999;")
public class TutorialOSrs extends PollingScript<ClientContext> {
    List<Task> taskList = new ArrayList<Task>();

    public static boolean[] tutorSpoken = {
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
    };
    int count = 0;

    @Override
    public void start() {
//        taskList.add(new CustomizeAvatar(ctx));
//        taskList.add(new ChooseName(ctx, "bewbow2"));
//        taskList.add(new CameraMotion(ctx));
//        taskList.add(new TalkToEntity(ctx, NPC.GIELINOR_GUIDE));
//        taskList.add(new CustomSettingTask(ctx));
//        taskList.add(new Walk(ctx));
//        taskList.add(new TalkToEntity(ctx, NPC.SURVIVAL_EXPERT));

    }

    public List<Task> detectLocation() {
        List<Task> taskList = new ArrayList<Task>();


        TutorialLocation location = new TutorialLocation(ctx);

        if (location.is(IslandLocation.CHOOSE_DISPLAY_NAME)) {
            taskList.add(new ChooseName(ctx, "redempl4"));
        } else if (location.is(IslandLocation.YOU_VE_GIVEN_AN_ITEM)) {
            TabChanger tabChanger = new TabChanger(ctx);
            tabChanger.changeTo(Game.Tab.INVENTORY);
            System.out.println(ctx.game.tab());
        } else if (location.is(IslandLocation.YOU_VE_GAINED_SOME_EXP)) {
            TabChanger tabChanger = new TabChanger(ctx);
            tabChanger.changeTo(Game.Tab.STATS);
        } else if (location.is(IslandLocation.SKILLS_AND_EXPERIENCE)) {
            taskList.add(new TalkToEntity(ctx, NPC.SURVIVAL_EXPERT, IslandLocation.SKILLS_AND_EXPERIENCE));
        } else if (location.is(IslandLocation.FISHING)) {
            TabChanger tabChanger = new TabChanger(ctx);
            tabChanger.changeTo(Game.Tab.INVENTORY);

            taskList.add(new FishingTutorial(ctx));
        } else if (location.is(IslandLocation.WOODCUTING)) {
//            System.out.println("Ahhh");
            taskList.add(new WoodcutTutorial(ctx));
        } else if (location.is(IslandLocation.FIREMAKING)) {
            taskList.add(new FiremakingTutorial(ctx));
        } else if (location.is(IslandLocation.MOVING_TO_FIRST)) {
            taskList.add(new Walk(ctx));
        } else if (location.is(IslandLocation.MOVING_TO_SURVIVAL_EXPERT)) {
            taskList.add(new Walk(ctx));
            taskList.add(new TalkToEntity(ctx, NPC.SURVIVAL_EXPERT, IslandLocation.MOVING_TO_SURVIVAL_EXPERT));
        } else if (location.is(IslandLocation.SURVIVAL_EXPERT_GIVES_YOU_NET)) {
            ctx.widgets.component(193, 0, 2).click();
            System.out.println("Can continue?: " + ctx.chat.canContinue());
        } else if (location.is(IslandLocation.MOVING_ON_AFTER_KITCHEN)) {
            if (!AREA.afterWardsOutsideKitchenArea.containsOrIntersects(ctx.players.local())) {
                Tile randomTile = AREA.afterWardsOutsideKitchenArea.getRandomTile();
                Tile[] tiles = {ctx.players.local().tile(), randomTile};

                Walker w = new Walker(ctx);
                w.walkPath(tiles);
            }
        } else if (location.is(IslandLocation.FANCY_A_RUN)) {
            System.out.println("Fancy a run");
            Component runComp = ctx.widgets.component(160, 24);

            runComp.click();
            Condition.sleep(Helper.smartSecondsGen());

            if (runComp.textureId() == 1064) {
                runComp.click();
            }

        } else if (location.is(IslandLocation.MOVING_AFTER_RUN_ACTIVATED)) {
            System.out.println("Going to quest area");
            if (!AREA.questTutorialArea.containsOrIntersects(ctx.players.local())) {
                Tile randomTile = AREA.questTutorialArea.getRandomTile();
                Tile[] tiles = {ctx.players.local().tile(), randomTile};

                Walker w = new Walker(ctx);
                w.walkPath(tiles);
            }

        } else if (location.is(IslandLocation.LEARN_QUEST)) {
            System.out.println("QUEST AREA");
            taskList.add(new TalkToEntity(ctx, NPC.QUEST_GUIDE, IslandLocation.LEARN_QUEST));
        } else if (location.is(IslandLocation.QUEST_JOURNAL_TALK)) {
            System.out.println("QUEST JOURNAL");
            taskList.add(new TalkToEntity(ctx, NPC.QUEST_GUIDE, IslandLocation.QUEST_JOURNAL_TALK));
        } else if (location.is(IslandLocation.MOVING_ON_AFTER_QUEST)) {
            System.out.println("MOVING TO CAVE");
            if (!AREA.miningStartingPointArea.containsOrIntersects(ctx.players.local())) {
                Tile randomTile = AREA.miningStartingPointArea.getRandomTile();
                Tile[] tiles = {ctx.players.local().tile(), randomTile};

                Walker w = new Walker(ctx);
                w.avoidChecking("staircase");
                w.walkPath(tiles);
            }
        } else if (location.is(IslandLocation.MINING_AND_SMITHING_WALK)) {
            System.out.println("MOVING TO MINING");
            taskList.add(new WalkTask(ctx, AREA.miningTutorialArea));
            taskList.add(new TalkToEntity(ctx, NPC.MINING_INSTRUCTOR, IslandLocation.MINING_AND_SMITHING_WALK));

//            if (!AREA.miningTutorialArea.containsOrIntersects(ctx.players.local())) {
//                Tile randomTile = AREA.miningTutorialArea.getRandomTile();
//                Tile[] tiles = {ctx.players.local().tile(), randomTile};
//
//                Walker w = new Walker(ctx);
//                w.walkPath(tiles);
//            }
        } else if (location.is(IslandLocation.MINING_TIN_ORE)) {
            System.out.println("Mining tin ore");
            taskList.add(new WalkTask(ctx, AREA.tinOreTutorialArea));
            taskList.add(new MiningTask(ctx, OBJECTS.TIN_ORE));

        } else if (location.is(IslandLocation.MINING_COPPER_ORE)) {
            System.out.println("Mining cooper ore");
            taskList.add(new WalkTask(ctx, AREA.copperOreTutorialArea));
            taskList.add(new MiningTask(ctx, OBJECTS.COOPER_ORE));

        } else if (location.is(IslandLocation.QUEST_JOURNAL)) {
            System.out.println("QUEST TAB");
            TabChanger tabChanger = new TabChanger(ctx);
            tabChanger.changeTo(Game.Tab.QUESTS);
        } else if (location.is(IslandLocation.MOVING_ON_AFTER_COOKING)) {
            if (!AREA.kitchenTutorialArea.containsOrIntersects(ctx.players.local())) {
                Tile randomTile = AREA.kitchenTutorialArea.getRandomTile();
                Tile[] tiles = {ctx.players.local().tile(), randomTile};

                Walker w = new Walker(ctx);
                w.walkPath(tiles);
            }
        } else if (location.is(IslandLocation.SMITHING_SMELT_BRONZE_BAR)) {
            System.out.println("smelting ore");
            taskList.add(new WalkTask(ctx, AREA.furnaceTutorialArea));
            taskList.add(new SmeltBarTask(ctx));
        } else if (location.is(IslandLocation.SMITHING_A_DAGGER)) {
            System.out.println("smithing a dagger");
            taskList.add(new WalkTask(ctx, AREA.anvilTutorialArea));
            taskList.add(new SmithDaggerTask(ctx));
        } else if (location.is(IslandLocation.MOVING_AFTER_SMITHING)) {
            System.out.println("moving on after smithing");
            taskList.add(new WalkTask(ctx, AREA.firstHalfToCombatArea));
        } else if (location.is(IslandLocation.COMBAT_TALK)) {
            System.out.println("going to instructor");
            taskList.add(new WalkTask(ctx, AREA.combatInstructorArea));
            taskList.add(new TalkToEntity(ctx, NPC.COMBAT_INSTRUCTOR, IslandLocation.COMBAT_TALK));
        } else if (location.is(IslandLocation.EQUIPMENT_ITEM)) {
            System.out.println("EQUIPMENT ITEM");
            TabChanger tabChanger = new TabChanger(ctx);
            tabChanger.changeTo(Game.Tab.EQUIPMENT);
        } else if (location.is(IslandLocation.WORN_INVENTORY)) {
            System.out.println("WORN INVENTORY");
            Component wornEquipmentComp = ctx.widgets.component(387, 18);

            if (wornEquipmentComp.visible()) {
                wornEquipmentComp.click();
            } else {
                TabChanger tabChanger = new TabChanger(ctx);
                tabChanger.changeTo(Game.Tab.EQUIPMENT);
            }
        } else if (location.is(IslandLocation.EQUIP_DAGGER)) {
            System.out.println("EQUIP DAGGER");
            taskList.add(new EquipmentTask(ctx, new int[]{ITEM.BRONZE_DAGGER}));
        } else if (location.is(IslandLocation.HOLDING_DAGGER)) {
            System.out.println("HOLDING DAGGER");
            taskList.add(new TalkToEntity(ctx, NPC.COMBAT_INSTRUCTOR, IslandLocation.HOLDING_DAGGER));
        } else if (location.is(IslandLocation.UNEQUIPPING_ITEM)) {
            System.out.println("Unnequiping items");
            taskList.add(new EquipmentTask(ctx, new int[]{ITEM.WOODEN_SHIELD, ITEM.BRONZE_SWORD}));
        } else if (location.is(IslandLocation.COMBAT_INTERFACE)) {
            System.out.println("combat interface items");
            TabChanger tabChanger = new TabChanger(ctx);
            tabChanger.changeTo(Game.Tab.ATTACK);
        } else if (location.is(IslandLocation.COMBAT_GO_IN)) {
            System.out.println("combat go in");
            taskList.add(new WalkTask(ctx, AREA.combatRatArea));
        }  else if (location.is(IslandLocation.ATTACKING_RATS)) {
            System.out.println("Attacking rats");
//            taskList.add();
        } else if (location.is(IslandLocation.KITCHEN_COOKING)) {
            System.out.println("KITCHEN COOKING");
            taskList.add(new TalkToEntity(ctx, NPC.MASTER_CHEF, IslandLocation.KITCHEN_COOKING));
        } else if (location.is(IslandLocation.SMITHING_WEAPON_TALK)) {
            System.out.println("talk to make a weapon");
            taskList.add(new WalkTask(ctx, AREA.miningTutorialArea));
            taskList.add(new TalkToEntity(ctx, NPC.MINING_INSTRUCTOR, IslandLocation.SMITHING_WEAPON_TALK));
        } else if (location.is(IslandLocation.MAKING_DOUGH)) {
            System.out.println("MAKING DOUGH");
            Item potOfFlour = ctx.inventory.select().id(ITEM.POT_OF_FLOUR).poll();
            Item bucketOfWater = ctx.inventory.select().id(ITEM.BUCKET_OF_WATER).poll();

            System.out.println(potOfFlour.valid());
            System.out.println(bucketOfWater.valid());
            if (potOfFlour.valid() && bucketOfWater.valid()) {
                potOfFlour.interact("Use");
                Condition.sleep(Helper.smartSecondsGen());
                bucketOfWater.interact(true, "Use");
            }
            Condition.sleep(2000);
        } else if (location.is(IslandLocation.COOKING_DOUGH)) {
            System.out.println("COOKING DOUGH");

            Item bread = ctx.inventory.select().id(ITEM.BREAD).poll();
            Item breadDough = ctx.inventory.select().id(ITEM.BREAD_DOUGH).poll();
            GameObject range = ctx.objects.select().id(OBJECTS.RANGE).poll();

            System.out.println("bread: " + !bread.valid());
            System.out.println("breadDough: " + breadDough.valid());
            System.out.println("range: " + range.valid());
            if (breadDough.valid() && range.valid() && !bread.valid()) {
                Condition.sleep(Helper.smartSecondsGen());
                range.interact("Cook");
            }
            Condition.sleep(2000);
        } else if (location.is(IslandLocation.COOKING)) {
            taskList.add(new CookingTutorial(ctx));
        } else if (location.is(IslandLocation.SETTING_APPEARANCE)) {
            taskList.add(new CustomizeAvatar(ctx));
        } else if (location.is(IslandLocation.GETTING_STARTED)) {
            taskList.add(new TalkToEntity(ctx, NPC.GIELINOR_GUIDE, IslandLocation.GETTING_STARTED));
        } else if (location.is(IslandLocation.OPTION_MENU)) {
            System.out.println("OPTION MENU");
            taskList.add(new CustomSettingTask(ctx));
            taskList.add(new TalkToEntity(ctx, NPC.GIELINOR_GUIDE, IslandLocation.OPTION_MENU));
        } else {
            TutorialLocation tl = new TutorialLocation(ctx);
            tl.smartChecker();
            System.out.println("pitch " + ctx.camera.pitch(99));

            return taskList;
        }
        return taskList;
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void poll() {
//        ctx.controller.stop();


//        System.out.println("Checking: " + continued);

        for (Task t : detectLocation()) {
            if (t.activate()) {
                System.out.println("Task started: " + count++);
                t.execute();
                break;
            }
        }

//        System.out.println("pitch " + ctx.camera.pitch(99));
//        System.out.println("new angle " + ctx.camera.angle(0));
//        System.out.println("pitch " + ctx.camera.pitch());
//        System.out.println("yaw " + ctx.camera.yaw());
//        Condition.sleep(2000);
    }


    private Component inputBox() {
        return ((ClientContext) this.ctx).widgets.component(162, 45);
    }

    public boolean pendingInput() {
        return this.inputBox().visible();
    }


}
