package caster.tasks;

import caster.assets.AREA;
import lib.PathUtil;
import lib.Task;
import lib.Walker;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;

public class PathWalker extends Task {
    Tile[] path;
    Walker walker;

    public PathWalker(ClientContext ctx, Tile[] path) {
        super(ctx);
        PathUtil pathUtil = new PathUtil(path);

        this.path = pathUtil.randomizeCoordinateTiles();
        this.walker = new Walker(ctx);

    }

    @Override
    public boolean activate() {
        return !ctx.players.local().inMotion();
    }

    @Override
    public void execute() {
        if (ctx.players.local().inMotion() ||
                ctx.movement.destination().equals(Tile.NIL) ||
                ctx.movement.destination().distanceTo(ctx.players.local()) < 3
        ) {
            walker.walkPathReverse(this.path);
        }
    }
}
