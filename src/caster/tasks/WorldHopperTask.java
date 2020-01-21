package caster.tasks;

import lib.Helper;
import lib.Task;
import lib.WorldUtil;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class WorldHopperTask extends Task {
    int everySeconds;
    long startTime;
    long currentTime;

    WorldUtil worldUtil;

    public WorldHopperTask(ClientContext ctx, int everySeconds, WorldUtil worldUtil) {
        super(ctx);
        this.startTime = System.currentTimeMillis();
        this.everySeconds = everySeconds;
        this.worldUtil = worldUtil;
    }

    @Override
    public boolean activate() {
        this.currentTime = System.currentTimeMillis();
        return ctx.client().getClientState() == 30;
    }

    @Override
    public void execute() {
        if (timesUP())
            this.startTime = System.currentTimeMillis();

        String worldId = worldUtil.peekWorldFrom(worldUtil.shuffleWorldsInChunks(10));
        System.out.println(worldId);

        ctx.worlds.select().id(Integer.parseInt(worldId)).poll().hop();

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                System.out.println("Waiting");

                return timesUP() || Helper.checkIfStoppedThenShutDown(ctx);
            }
        }, 1000, everySeconds);
    }

    private boolean timesUP() {
        long elapsedTime = Helper.round(Helper.toSeconds(this.currentTime - this.startTime));
        return elapsedTime >= everySeconds;
    }
}
