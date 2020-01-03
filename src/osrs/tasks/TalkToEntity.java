package osrs.tasks;

import org.powerbot.script.rt4.*;
import osrs.ChatOptions;
import osrs.Helper;
import osrs.Task;
import osrs.TutorialOSrs;
import osrs.assets.IslandLocation;

public class TalkToEntity extends Task {
    int NpcID = -1;
    String taskString;
    BasicQuery<Npc> npcs = null;

    public TalkToEntity(ClientContext ctx, int NPCid, String TaskString) {
        super(ctx);
        this.NpcID = NPCid;
        this.taskString = TaskString;
    }

    @Override
    public boolean activate() {
        npcs = ctx.npcs.select().id(this.NpcID).nearest();

        System.out.println(!TutorialOSrs.tutorSpoken[this.getIndexFrom(this.taskString)]);
        return !TutorialOSrs.tutorSpoken[this.getIndexFrom(this.taskString)];
    }

    @Override
    public void execute() {
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
                    ctx.chat.continueChat(opts.getRandomOption());
                } else {
                    ctx.chat.clickContinue();
                }

                Helper.sleepToClick();
            }
        }

//        ctx.controller.stop();

        TutorialOSrs.tutorSpoken[getIndexFrom(this.taskString)] = true;
    }

    private int getIndexFrom(String taskLocation) {
        if (taskLocation.equals(IslandLocation.GETTING_STARTED)) {
            return 0;
        } else if (taskLocation.equals(IslandLocation.OPTION_MENU)) {
            return 1;
        } else if (taskLocation.equals(IslandLocation.SKILLS_AND_EXPERIENCE)) {
            return 2;
        } else if (taskLocation.equals(IslandLocation.MOVING_TO_SURVIVAL_EXPERT)) {
            return 3;
        } else if (taskLocation.equals(IslandLocation.KITCHEN_COOKING)) {
            return 4;
        } else if (taskLocation.equals(IslandLocation.LEARN_QUEST)) {
            return 5;
        } else if (taskLocation.equals(IslandLocation.QUEST_JOURNAL_TALK)) {
            return 6;
        } else if (taskLocation.equals(IslandLocation.MINING_AND_SMITHING_WALK)) {
            return 7;
        } else if (taskLocation.equals(IslandLocation.SMITHING_WEAPON_TALK)) {
            return 8;
        } else if (taskLocation.equals(IslandLocation.COMBAT_TALK)) {
            return 9;
        } else if (taskLocation.equals(IslandLocation.HOLDING_DAGGER)) {
            return 9;
        } else {
            return -1;
        }
    }
}
