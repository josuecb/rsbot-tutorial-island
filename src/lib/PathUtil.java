package lib;

import org.powerbot.script.Tile;

import java.util.Arrays;
import java.util.Random;

public class PathUtil {
    Tile[] path;

    public PathUtil(Tile[] path) {
        this.path = path;
    }

    public Tile[] randomizeCoordinateTiles() {
        Tile[] newPath = new Tile[path.length];

        for (int index = 0; index < this.path.length; index++) {
            if (index != this.path.length - 1)
                newPath[index] = this.randomizeTile(this.path[index], this.path[index + 1]);
            else
                newPath[index] = this.path[index];
        }

        System.out.println(Arrays.toString(newPath));
        return newPath;
    }

    // If t1 is higher then it will return a positive number
    // otherwise it would return a negative
    // if both are equal return 0

    private int checkHigher(int tile1Coordinate, int tile2Coordinate) {
        if (tile1Coordinate == tile2Coordinate)
            return 0;

        return tile1Coordinate - tile2Coordinate;
    }

    private int randomCoordinate(int c1, int c2) {
        int c3;

        int cDifference = this.checkHigher(c1, c2);

        if (cDifference < 0) {
            c3 = new Random().nextInt(Math.abs(cDifference)) + c1;
        } else if (cDifference > 0) {
            c3 = new Random().nextInt(cDifference) + (c1 - cDifference);
        } else {
            c3 = new Random().nextInt(1) + c1;
        }
        return c3;
    }

    private Tile randomizeTile(Tile tile1, Tile tile2) {
        int x = this.randomCoordinate(tile1.x(), tile2.x());

        int y = this.randomCoordinate(tile1.y(), tile2.y());

        return new Tile(x, y, 0);
    }

}
