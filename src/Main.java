import geo.QueryTree2D;
import geo.History;
import geo.Point;

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

    /** Read the file into a list of lines */
    public static String[] readFile(String filename) {
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
}
