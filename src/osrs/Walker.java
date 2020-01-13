package osrs;

import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Chris on 05/01/2016.
 */
public class Walker {

    private ClientContext ctx;
    private String regex;

    public Walker(ClientContext ctx) {
        this.ctx = ctx;
        regex = "(staircase)|(ladder)|((.*)?door)|(gate(.*)?)";
    }

    /**
     * Returns the next tile to walk to, disregarding whether it is reachable.
     *
     * @param t The tile path being traversed.
     * @return The next tile to traverse to.
     */
    public Tile getNextTile(Tile[] t) {
        Tile nextTile = ctx.movement.newTilePath(t).next();                 //The next tile, as suggested by the RSBot api, this will be the next reachable tile.
        int index = 0;                                                      //The index at which the next tile (by our definition) is at. Default to 0 (start tile).
        final Player p = ctx.players.local();

        /*
         * Loop through the path, backwards.
         * Find the intended next tiles index
         * then check if there is a better option.
         */
        for (int i = t.length - 1; i >= 0; i--) {
            if (t[i].equals(nextTile)) {                                    //This is the index at which the suggested next tile resides
                if (i + 1 <= t.length - 1 && nextTile.distanceTo(p) < 3) {  //If we're not at the end of the path and we're very close to the suggested next tile
                    index = i + 1;                                          //then it's too close to bother with. We will try to go to the tile after instead.
                    break;
                }
                index = i;                                                  //Suggested next tile was the best option as it is not very close, and reachable.
                break;
            } else if (t[i].distanceTo(p) < 8) {
                index = i;                                                  //if next closest tile is <8 and it's not the next tile then we can assume the next tile is probably not correct, perhaps no reachable tile available...
                break;
            }
        }
        return t[index];
    }

    /**
     * Will detect which obstacle to tackle to give Walker the ability to
     * traverse to the next tile.     *
     *
     * @param t The tile path being traversed.
     * @return True if obstacle was clicked. False otherwise.
     */
    public boolean handleObstacle(Tile[] t) {
//        System.out.println("handling obstacles");
        Tile nextTile = getNextTile(t);                                       //The calculated next tile.
//        System.out.println("Next tile: " + nextTile.toString());
        /*
         * Return false as there is no obstacle to handle.
         * Perhaps this was called whilst we were still walking
         * and the tile became reachable?
         */
        if (nextTile.matrix(ctx).reachable()) {
//            System.out.println("Calculating if next tile is reachable");
            return false;
        }
//        System.out.println("next tile is not reachable");

        final Player p = ctx.players.local();
        double distance = Double.POSITIVE_INFINITY;

//        BasicQuery<GameObject> go1 = ctx.objects.select().id(9398);
//        System.out.println(go1.peek().toString());
//        GameObject gate = go1.peek();
//        System.out.println(gate.orientation());

        GameObject obstacle = null;

//        gate.bounds(new int[]{100, 143, -100, 0, -66, 90});
//        gate.interact("Open");
//        ctx.controller.stop();
        for (GameObject go : ctx.objects.select().name(
                Pattern.compile(this.regex, Pattern.CASE_INSENSITIVE)).nearest()) {                            //Changed to filter by name else all the filtering in the next part takes much too long to be considered effective..
//            System.out.println(go.toString());
            double calcDist = go.tile().distanceTo(new Tile(nextTile.x(), nextTile.y(), p.tile().floor()));
            /*
             * Check if the next tile is on a different floor. If it is then
             * we need to check whether it's up or down. We can use this
             * to filter objects by actions 'Climb-up' or 'Climb-down'
             */
            if (nextTile.floor() != p.tile().floor()) {
                if (go.type() == GameObject.Type.INTERACTIVE && go.actions().length > 0) {
                    if (nextTile.floor() > p.tile().floor()) {                      //Need to climb up.
                        if (calcDist < distance && reachable(go)) {
                            for (String s : go.actions()) {
                                if (s != null && !s.equals("null") && s.contains("Climb-up")) {
                                    obstacle = go;
                                    distance = calcDist;                                                            //Set the distance to the object so we can compare future objects against it to determine the best.
                                    break;
                                }
                            }
                        }
                    } else {                                                     //Need to climb down.
                        if (calcDist < distance && reachable(go)) {
                            for (String s : go.actions()) {
                                if (s != null && !s.equals("null") && s.contains("Climb-down")) {
                                    obstacle = go;
                                    distance = calcDist;                                                            //Set the distance to the object so we can compare future objects against it to determine the best.
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (nextTile.distanceTo(ctx.players.local()) > 50) {          //we can now assume that we need to go up or down .. not flawless whatsoever
                if (go.type() != GameObject.Type.BOUNDARY) {
                    for (String s : go.actions()) {
                        if (go.tile().distanceTo(nextTile) + go.tile().distanceTo(p) < distance && reachable(go)) {
                            if (s != null && !s.equals("null") && (s.contains("Climb-up") || s.contains("Climb-down") || s.contains("Enter"))) {
                                obstacle = go;
                                distance = go.tile().distanceTo(nextTile) + go.tile().distanceTo(p);              //Set the distance to the object so we can compare future objects against it to determine the best.
                                break;
                            }
                        }
                    }
                }
            } else {
                /*
                 * Floor was the same so we are blocked by a door, or gate.
                 * These are boundary objects, however we need to check the name
                 * as some random boundary objects appear with the name null.
                 */
                if (go.type() == GameObject.Type.BOUNDARY) {
                    if (calcDist < distance && reachable(go)) {
                        obstacle = go;
                        distance = calcDist;                                                            //Set the distance to the object so we can compare future objects against it to determine the best.
                    }
                }
            }
        }


        if (obstacle == null) {
//            System.out.println("Obstacle is null");
            ctx.camera.angle('n');
            return false;
        }

        if (obstacle.inViewport()) {
            obstacle.bounds(getBounds(obstacle));
            if (nextTile.floor() > p.tile().floor()) {                                                      //Going up.
                if (obstacle.interact("Climb-up")) {
                    return handlePostInteraction();
                }
            } else if (nextTile.floor() < p.tile().floor()) {                                               //Going down.
                if (obstacle.interact("Climb-down")) {
                    return handlePostInteraction();
                }
            } else if (nextTile.distanceTo(ctx.players.local()) > 50) {                                      //This is just guessing
                if (obstacle.interact("Climb-")) {
                    return handlePostInteraction();
                }
            } else {
//                System.out.println("opening door");
//                System.out.println(obstacle.toString());
                if (obstacle.interact("Open")) {                                                        //Going through.
                    return handlePostInteraction();
                }
            }
        } else {
//            System.out.println("rotating camera");
            if (ctx.movement.step(obstacle)) {                                                           //Can't see the obstacle, step towards it.
//                ctx.camera.turnTo(obstacle);                                                            //and turn the camera.
                SmartCamera sc = new SmartCamera(ctx, true);
                sc.rotate360Camera(1);
//                Condition.wait(new Callable<Boolean>() {
//                    @Override
//                    public Boolean call() throws Exception {
//                        return p.animation() == -1 && !p.inMotion();
//                    }
//                }, 1000, 3);
            }

        }
        return false;
    }

    /**
     * Determines if a game object is reachable by
     * checking the tiles around it.
     *
     * @param go The game object being tested.
     * @return True or false.
     */
    private boolean reachable(GameObject go) {
        int a = go.width();
        Tile t1 = new Tile(go.tile().x() + a, go.tile().y(), go.tile().floor());
        Tile t2 = new Tile(go.tile().x() - a, go.tile().y(), go.tile().floor());
        Tile t3 = new Tile(go.tile().x(), go.tile().y() + a, go.tile().floor());
        Tile t4 = new Tile(go.tile().x(), go.tile().y() - a, go.tile().floor());

        return (t1.matrix(ctx).reachable() || t2.matrix(ctx).reachable() || t3.matrix(ctx).reachable() || t4.matrix(ctx).reachable());
    }

    /**
     * Handles the period after interacting with the object.
     *
     * @return True if clicked successfully.
     */
    private boolean handlePostInteraction() {
        final Player p = ctx.players.local();
        if (ctx.game.crosshair() == Game.Crosshair.ACTION) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return p.animation() == -1 && !p.inMotion();
                }
            }, 1000, 3);
            return true;
        }
        return false;

    }

    /**
     * Moves a single step towards the destination of a given path.
     * Most obstacles such as gates, ladders, doors, and stairs are handled.
     *
     * @param t The tile path to be traversed.
     * @return True if a step was taken, or object handled.
     */
    public boolean walkPath(Tile[] t) {
        Tile ti = getNextTile(t);

        return ti.matrix(ctx).reachable() ? ctx.movement.step(ti) : handleObstacle(t);
    }

    /**
     * Gets the bounds for a given object
     *
     * @param go The object to get bounds for.
     * @return Bounds. If not found, default bounds returned.
     */
    private int[] getBounds(GameObject go) {
        SmartCamera sc = new SmartCamera(ctx, true);
        sc.rotate360Camera(4);

        switch (getType(go.name())) {
            case DOOR:
//                System.out.println("door orientation: " + go.orientation());
                switch (go.orientation()) {
                    case 0:
                    case 4:
                    case 5:
                        return new int[]{-12, 102, -208, 0, 138, 85};
                    case 6:
                        return new int[]{102, 142, -210, 0, 120, 7};
//                        return new int[]{-5, 15, 0, -220, 0, 90};
                    case 1:
                        return new int[]{0, 90, 0, -220, 110, 130};
                    case 2:
                        return new int[]{100, 120, 0, -220, 0, 90};
                    case 3:
                        return new int[]{0, 100, 0, -200, 10, 20};
                }
                break;
            case GATE:
//                System.out.println("Type name");
//                System.out.println(go.orientation());
                switch (go.orientation()) {
                    case 0:
//                        System.out.println("rientation:");
//                        System.out.println(go.orientation());
                    case 4:
                    case 6:
                        return new int[]{100, 143, -100, 0, -66, 90};
//                        return new int[]{5, 10, 0, -80, 20, 80};
                    case 1:
                        return new int[]{0, 80, 0, -80, 118, 123};
                    case 2:
                        return new int[]{118, 123, 0, -80, 0, 80};
                    case 3:
                        return new int[]{10, 80, 0, -80, 15, 0};
                }
                break;
            case LADDER:
                switch (go.orientation()) {
                    case 0:
                    case 4:
                    case 2:
                        return new int[]{-20, 40, 20, -40, 0, -60};
                    case 1:
                    case 5:
                        return new int[]{0, -40, -64, 0, -32, 32};
                    case 6:
                        return new int[]{-20, 40, 20, -40, 0, 60};
                    case 3:
                    case 7:
                        return new int[]{10, 40, -64, 0, -32, 32};
                }
                break;
        }

        return new int[]{-32, 32, -64, 0, -32, 32};
    }

    /**
     * Gets the enum associated with a given string, if applicable.
     *
     * @param s The string to check (name of object?)
     * @return The enum associated.
     */
    private Type getType(String s) {
        if (s.matches("(?i)((.*)?door)")) {
            return Type.DOOR;
        } else if (s.matches("(?i)(gate(.*)?)")) {
            return Type.GATE;
        } else if (s.equalsIgnoreCase("ladder")) {
            return Type.LADDER;
        }
        return Type.TEAPOT;
    }

    public void avoidChecking(String type) {
        String r = this.regex;
        ArrayList<String> rl;
//        (staircase)|(ladder)|((.*)?door)|(gate(.*)?)
        rl = (ArrayList<String>) Arrays.stream(r.split("\\|")).filter(entry -> !entry.contains(type)).collect(Collectors.toList());
        this.regex = String.join("|", rl);
    }

    /**
     * Reverses the given path, and then calls {@link #walkPath(Tile[] t)}.
     *
     * @param t
     * @return
     */
    public boolean walkPathReverse(Tile[] t) {
        t = ctx.movement.newTilePath(t).reverse().toArray();

        return walkPath(t);
    }

    public enum Type {
        DOOR, GATE, LADDER, TEAPOT
    }

}