package osrs.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import osrs.Task;
import osrs.Walker;
import org.powerbot.script.Area;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class WalkTask extends Task {
    Walker w;
    Area area;
    String[] avoidTypes;

    public WalkTask(ClientContext ctx, Area area) {
        super(ctx);
        this.area = area;
        avoidTypes = new String[]{};
    }

    public WalkTask(ClientContext ctx, Area area, String[] avoidBoundaryTypes) {
        super(ctx);
        this.area = area;
        this.avoidTypes = avoidBoundaryTypes;
    }

    @Override
    public boolean activate() {
        return !ctx.players.local().inMotion() && !area.containsOrIntersects(ctx.players.local());
    }

    @Override
    public void execute() {
        System.out.println("Start Walking");
        Tile randomTile = this.area.getRandomTile();
        Tile[] tiles = {ctx.players.local().tile(), randomTile};

        System.out.println("Random Tile: " + randomTile.toString());
        System.out.println(Arrays.toString(tiles));
        w = new Walker(ctx);

        for (String avoidType : this.avoidTypes)
            w.avoidChecking(avoidType);

        w.walkPath(tiles);

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                System.out.println("Waiting");
                return ctx.players.local().inMotion();
            }
        }, 1000, 1);
    }
}
