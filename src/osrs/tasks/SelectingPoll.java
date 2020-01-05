package osrs.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import osrs.Helper;
import osrs.Task;
import osrs.TutorialLocation;
import osrs.assets.IslandLocation;
import osrs.assets.OBJECTS;

import java.util.concurrent.Callable;

public class SelectingPoll extends Task {
    public SelectingPoll(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return !ctx.bank.opened() && !ctx.players.local().inMotion();
    }

    @Override
    public void execute() {
        if (!this.isOpened()) {
            System.out.println("is not opened Yet");
            pollBooth().interact("Use");

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    TutorialLocation location = new TutorialLocation(ctx);

                    flagNextMessage().click();
                    return true;
                }
            }, Helper.smartSecondsGen(), 5);
        } else {
            closeBooth();
        }
    }


    private GameObject pollBooth() {
        return ctx.objects.select().id(OBJECTS.POLL_BOOTH).nearest().poll();
    }

    private boolean isOpened() {
        return pollHistoryPanel().visible();
    }

    private void closeBooth() {
        this.closeButton().click();
    }

    private Component pollHistoryPanel() {
        return ctx.widgets.component(310, 2, 0);
    }

    private Component closeButton() {
        return ctx.widgets.component(310, 2, 11);
    }

    private Component flagNextMessage() {
        return ctx.widgets.component(193, 0, 2);
    }

}
