import geo.QueryTree2D;
import geo.History;
import geo.Point;
import gui.PointDisplayer;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.List;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class Main {

    public static void main(String[] args) {
        CLO clo = new CLO(args);
        if (clo.gui) {
            gui.MainPanel.setupFrame(clo.resolution);
            return;
        } else if (clo.performance != -1) {
            performanceTest(clo.performance, 10);
            return;
        }
        String[] lines = readFile(clo.file);
        if (lines.length < 1) {
            System.out.println("Input file is empty.");
            System.exit(0);
        }
        String[] pointStrings = lines[0].split(" ");
        if (pointStrings.length < 1) {
            System.out.println("No points specified.");
            System.exit(0);
        }

        // reading points to be queried
        Point[] points = new Point[pointStrings.length];
        for (int i = 0; i < pointStrings.length; i++) {
            points[i] = parsePoint(pointStrings[i], 0, i);
        }

        // reading all the queries
        Point[][] queries = new Point[lines.length - 1][2];
        for (int i = 1; i < lines.length; i++) {
            queries[i - 1] = new Point[2];
            String[] corners = lines[i].split(" ");
            if (corners.length != 2) {
                System.out.println("Query on line " + i + 1 + " has " +
                        corners.length + " points instead of 2.");
                System.exit(0);
            }
            Point pt1 = parsePoint(corners[0], i, 0);
            Point pt2 = parsePoint(corners[1], i, 1);
            queries[i - 1][0] = new Point(min(pt1.x, pt2.x), min(pt1.y, pt2.y));
            queries[i - 1][1] = new Point(max(pt1.x, pt2.x), max(pt1.y, pt2.y));
        }

        // building the tree
        QueryTree2D<Point> tree = new QueryTree2D<>(points, Point.COMPARE_X,
                Point.COMPARE_Y);
        // processing the queries
        for (Point[] query: queries) {
            Set<Point> result = tree.search(query[0], query[1], new History());
            System.out.println(String.join(" ", Arrays
                    .stream(result.toArray(new Point[0]))
                    .map(Point::toString).toArray(String[]::new)));
        }

    }

    /**
     * Generate nPts points. Report how long does it take to:
     * 1) build a 2d query tree for these points
     * 2) perform a query expected to return 0 points on this set
     * @param nPts        number of points
     * @param nQueries    number of queries to average the result over
     */
    private static void performanceTest(int nPts, int nQueries) {
        HashSet<Point> points = new HashSet<>();
        // points' coordinates are in range (0, sqrt(n)), so a 10 by 10 query is
        // expected to return 100 points
        while (points.size() != nPts) {
           points.add(Point.getRandom(0, nPts, 0, nPts));
        }
        Point[] pts = points.toArray(new Point[0]);
        long startTime = System.nanoTime();
        QueryTree2D<Point> tree = new QueryTree2D<>(pts,
                Point.COMPARE_X,
                Point.COMPARE_Y);
        long treeBuildingTime = System.nanoTime() - startTime;
        long[] queryTimes = new long[nQueries];
        for (int i = 0; i < nQueries; i++) {
            Point p = Point.getRandom(0, nPts, 0, nPts);
            while (points.contains(p))
                p = Point.getRandom(0, nPts, 0, nPts);
            startTime = System.nanoTime();
            tree.search(p, p, new History());
            queryTimes[i] = System.nanoTime() - startTime;
        }
        long meanQueryTime = Arrays.stream(queryTimes).sum() / nQueries;
        System.out.println("Preprocessing for " + nPts + " points took " +
                treeBuildingTime + " nanoseconds");
        System.out.println("Querying " + nPts + " points took " + meanQueryTime +
                " nanoseconds (average over " + nQueries + " runs)");
    }

    /** Read the file into a list of lines */
    private static String[] readFile(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));
            List<String> lines = new ArrayList<>();
            while (sc.hasNextLine()) {
                lines.add(sc.nextLine());
            }
            return lines.toArray(new String[0]);
        } catch (Exception e) {
            System.out.println("Failed to open file: " + filename);
            System.exit(0);
        }
        return null;
    }

    public static Point parsePoint(String s, int line, int pID) {
        int x = 0, y = 0;
        String message = "Failed to parse point " + pID + " on line " + line;
        String[] parts = s.split(",");
        if (parts.length != 2) {
            System.out.println(message);
            System.exit(0);
        }
        try {
            x = Integer.parseInt(parts[0]);
            y = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e2) {
            System.out.println(message);
            System.exit(0);
        }
        return new Point(x, y);

    }

    public static class Tuple<T,K> {
        public final T fst;
        public final K snd;

        public Tuple(T fst, K snd) {
            this.fst = fst;
            this.snd = snd;
        }
    }
}
