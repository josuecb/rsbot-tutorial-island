package osrs.tasks;

import org.powerbot.script.rt4.*;
import osrs.ChatOptions;
import osrs.Helper;
import osrs.Task;
import osrs.TutorialOSrs;
import osrs.assets.IslandLocation;

import java.util.Arrays;

public class TalkToEntity extends Task {
    int NpcID = -1;
    String taskString;
    BasicQuery<Npc> npcs = null;
    String randomOpt = null;

    public TalkToEntity(ClientContext ctx, int NPCid, String TaskString) {
        super(ctx);
        this.NpcID = NPCid;
        this.taskString = TaskString;
    }

    public TalkToEntity(ClientContext ctx, int NPCid, String TaskString, String randomOption) {
        super(ctx);
        this.NpcID = NPCid;
        this.taskString = TaskString;
        this.randomOpt = randomOption;
    }

    @Override
    public boolean activate() {
        npcs = ctx.npcs.select().id(this.NpcID).nearest();

        System.out.println(this.getIndexFrom(this.taskString));
        System.out.println(!TutorialOSrs.tutorSpoken[this.getIndexFrom(this.taskString)]);
        System.out.println(this.taskString);
        System.out.println(Arrays.toString(TutorialOSrs.tutorSpoken));
        return !TutorialOSrs.tutorSpoken[this.getIndexFrom(this.taskString)];
    }

    @Override
    public void execute() {
        System.out.println("NPC valid: " + npcs.peek().valid());
        if (npcs.peek().valid()) {
            if (!npcs.peek().inViewport()) {
                ctx.camera.turnTo(npcs.peek());
            }

            if (!ctx.chat.chatting()) {
                System.out.println("Found one!: " + npcs.peek().name());
                npcs.peek().interact("Talk-to");
                System.out.println("Is Chatting? " + ctx.chat.chatting());
                System.out.println("Can continue? " + ctx.chat.canContinue());

                Helper.sleepToClick();
                Helper.sleepToClick();
                Helper.sleepToClick();
            }

            if (!ctx.chat.chatting())
                return;

            while (ctx.chat.chatting() &&
                    (ctx.chat.canContinue() || new ChatOptions(ctx).getRandomOption() != null)) {
                ChatOptions opts = new ChatOptions(ctx);

                System.out.println(opts.getRandomOption());
                if (opts.getRandomOption() != null) {
                    if (randomOpt == null)
                        ctx.chat.continueChat(opts.getRandomOption());
                    else {
                        ctx.chat.continueChat(randomOpt);
                    }
                } else {
                    ctx.chat.clickContinue();
                }

                Helper.sleepToClick();
            }
        }

//        ctx.controller.stop();

        TutorialOSrs.tutorSpoken[this.getIndexFrom(this.taskString)] = true;
    }

    private int getIndexFrom(String taskLocation) {
        if (taskLocation.equals(IslandLocation.GETTING_STARTED)) {
            return 0;
        } else if (taskLocation.equals(IslandLocation.OPTION_MENU)) {
            return 1;
        } else if (taskLocation.equals(IslandLocation.OPTION_MENU_TALK)) {
            return 2;
        } else if (taskLocation.equals(IslandLocation.SKILLS_AND_EXPERIENCE)) {
            return 3;
        } else if (taskLocation.equals(IslandLocation.MOVING_TO_SURVIVAL_EXPERT)) {
            return 4;
        } else if (taskLocation.equals(IslandLocation.KITCHEN_COOKING)) {
            return 5;
        } else if (taskLocation.equals(IslandLocation.LEARN_QUEST)) {
            return 6;
        } else if (taskLocation.equals(IslandLocation.QUEST_JOURNAL_TALK)) {
            return 7;
        } else if (taskLocation.equals(IslandLocation.MINING_AND_SMITHING_WALK)) {
            return 8;
        } else if (taskLocation.equals(IslandLocation.SMITHING_WEAPON_TALK)) {
            return 9;
        } else if (taskLocation.equals(IslandLocation.COMBAT_TALK)) {
            return 10;
        } else if (taskLocation.equals(IslandLocation.HOLDING_DAGGER)) {
            return 11;
        } else if (taskLocation.equals(IslandLocation.WELL_DONE_FIRST_KILL)) {
            return 12;
        } else if (taskLocation.equals(IslandLocation.ACCOUNT_MANAGEMENT)) {
            return 13;
        } else if (taskLocation.equals(IslandLocation.ACCOUNT_MANAGEMENT_SECOND_TALK)) {
            return 14;
        } else if (taskLocation.equals(IslandLocation.PRAYER_ALTAR)) {
            return 15;
        } else if (taskLocation.equals(IslandLocation.PRAYER_DESCRIPTION)) {
            return 16;
        } else if (taskLocation.equals(IslandLocation.FRIENDS_AND_IGNORE_LIST_TALK)) {
            return 17;
        } else if (taskLocation.equals(IslandLocation.WALKING_TO_WIZARDS_HOUSE)) {
            return 18;
        } else if (taskLocation.equals(IslandLocation.MAGIC_SPELLS_TALK)) {
            return 19;
        } else if (taskLocation.equals(IslandLocation.TO_MAINLAND)) {
            return 20;
        } else {
            return -1;
        }
    }
}
