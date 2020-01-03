package osrs;

import org.powerbot.script.Tile;

import java.util.*;

public class RandomTraceTile {
    ArrayList<Tile> additionalStops;

    public RandomTraceTile() {

    }

    public RandomTraceTile(ArrayList<Tile> stopInBetweenTiles) {
        additionalStops = stopInBetweenTiles;
//        ArrayList<Tile> newPath = this.generateLinearShortPath(new Tile(3093, 3106, 0), new Tile(3097, 3107, 0));


    }

    private ArrayList<Tile> generateLinearShortPath(Tile currentPointTile, Tile endPointTile) {
        int randomSteps = new Random().nextInt(2) + 3;
        System.out.println("Size: " + randomSteps);

        int currentXPoint = currentPointTile.x();
        int destinationXEndPoint = endPointTile.x();

        int currentYPoint = currentPointTile.y();
        int destinationYEndPoint = endPointTile.y();

        ArrayList<Integer> xPoints = this.generateRandomList(randomSteps, currentXPoint, destinationXEndPoint);
        ArrayList<Integer> yPoints = this.generateRandomList(randomSteps, currentYPoint, destinationYEndPoint);
//        System.out.println("Unsorted x: " + Arrays.toString(xPoints.toArray()));

        this.sortDependingOnDirection(currentXPoint, destinationXEndPoint, xPoints);
//        System.out.println("Sorted x: " + Arrays.toString(xPoints.toArray()));

//        System.out.println("Unsorted y: " + Arrays.toString(yPoints.toArray()));

        this.sortDependingOnDirection(currentYPoint, destinationYEndPoint, yPoints);
//        System.out.println("Sorted y: " + Arrays.toString(yPoints.toArray()));

        ArrayList<Tile> tiles = new ArrayList<>(Arrays.asList(this.createTileFrom(xPoints, yPoints)));
        if (!tiles.contains(endPointTile))
            tiles.add(endPointTile);

        System.out.print("Generated Tile array: ");
        System.out.println(Arrays.toString(tiles.toArray()));
        return tiles;
    }

    public Tile[] generateLinearPath(Tile currentPointTile, Tile endPointTile) {
        ArrayList<Tile> newPath = new ArrayList<>();

        for (Tile stop : additionalStops) {
            newPath.addAll(this.generateLinearShortPath(currentPointTile, stop));
            currentPointTile = stop;
        }

        newPath.addAll(this.generateLinearShortPath(currentPointTile, endPointTile));

        return newPath.toArray(new Tile[0]);
    }

    private void sortDependingOnDirection(int currentPoint, int endPoint, ArrayList<Integer> pointList) {
        if (endPoint > currentPoint) {
            Collections.sort(pointList);
        } else {
            pointList.sort(Collections.reverseOrder());
        }
    }

    private Tile[] createTileFrom(ArrayList<Integer> xPoints, ArrayList<Integer> yPoints) {
        Tile[] tiles = new Tile[xPoints.size()];

        for (int index = 0; index < xPoints.size(); index++) {
            Tile t = new Tile(xPoints.get(index), yPoints.get(index), 0);
            tiles[index] = t;
//            System.out.println("Adding Tile: " + t.toString());
        }

        return tiles;
    }

    private ArrayList<Integer> generateRandomList(int size, int from, int to) {
        ArrayList<Integer> randomList = new ArrayList<>();
        for (int i = size; i > 0; i--) {
            if (to > from) {
                randomList.add(new Random().nextInt((to - from)) + from);
            } else if (to < from) {
                randomList.add(new Random().nextInt((from - to)) + to);
            } else {
                randomList.add(from);
            }
        }
        return randomList;
    }
}
