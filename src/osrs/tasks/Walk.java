package osrs.tasks;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import osrs.Task;
import osrs.TutorialOSrs;
import osrs.Walker;

public class Walk extends Task {
    public static final Tile[] guilderToWoodcut = {
            new Tile(3093, 3106, 0),
            new Tile(3096, 3107, 0),
            new Tile(3099, 3107, 0),
            new Tile(3102, 3107, 0),
            new Tile(3103, 3104, 0),
            new Tile(3101, 3101, 0),
            new Tile(3098, 3099, 0),
            new Tile(3101, 3099, 0),
            new Tile(3101, 3096, 0)
    };

    private final Walker walker = new Walker(ctx);

    public Walk(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {

        System.out.println(guilderToWoodcut[0].distanceTo(ctx.players.local()));
        System.out.println(guilderToWoodcut[0].distanceTo(ctx.players.local()) < 12);
        System.out.println(guilderToWoodcut[0].distanceTo(ctx.players.local()) < 12 && !ctx.chat.chatting());
        return guilderToWoodcut[0].distanceTo(ctx.players.local()) < 12 && !ctx.chat.chatting();
    }

    @Override
    public void execute() {
        if (!ctx.players.local().inMotion() || ctx.movement.destination().equals(Tile.NIL)) {
            walker.walkPath(guilderToWoodcut);
            System.out.println("[Not Walking]");
        } else {
            System.out.println("[Walking]");
        }
    }
}
