package geo;

import java.util.Comparator;
import java.util.Random;

/** A planar point */
public class Point {

    public final int x,y;

    private static final Random random = new Random();
    public static final Comparator<Point> COMPARE_X =
            Comparator.comparingInt(one -> one.x);
    public static final Comparator<Point> COMPARE_Y =
            Comparator.comparingInt(one -> one.y);

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point getRandom(int xMin, int xMax, int yMin, int yMax) {
        return new Point(random.nextInt(xMax - xMin) + xMin,
                random.nextInt(yMax - yMin) + yMin);
    }

    public String toString() {
        return x + "," + y;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Point))
            return false;
        return this.x == ((Point) other).x && this.y == ((Point) other).y;
    }

    public int hashCode() {
        return x * 31 + y;
    }

}
