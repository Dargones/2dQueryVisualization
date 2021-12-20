package gui;

import geo.QueryTree2D;
import geo.History;
import geo.Point;
import geo.Treap;
import geo.Tree;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

/** Gui for displaying a set of points and associated commands that allow
 * building 2d-search trees based on these points and run queries on them */
public class PointDisplayer extends JPanel implements MouseListener {

    HashSet<geo.Point> points; // the current set of points
    geo.Point[] query; // the two points that form a query (null if no query)
    public Mode mode; // add points, remove points, set query

    private History history; // history of the latest query
    private int step; // which step within the history to display

    private final TreeDisplayer treeDisplayer; // associated tree dispalyer
    private final TreeDisplayer treapDisplayer; // associated treap displayer
    private QueryTree2D<Point> tree; // the tree built from points
    private HashMap<String, Treap<geo.Point>> allTreaps; // all treaps in tree
    // stores list of treaps available for display
    private final JComboBox treapSelector;

    public Dimension panelDim; // dimensions of the window to display points
    public Dimension planeDim; // dimensions of the square in which points lie
    private static final Color QUERY_COLOR = Color.RED;

    public PointDisplayer(TreeDisplayer treeDisplayer,
                          TreeDisplayer treapDisplayer,
                          JComboBox treapSelector,
                          Dimension panelDim, Dimension planeDim) {
        this.treeDisplayer = treeDisplayer;
        this.treapDisplayer = treapDisplayer;
        this.treapSelector = treapSelector;
        this.tree = null;
        this.allTreaps = null;
        setBorder(new LineBorder(Color.BLACK, 5));
        setPreferredSize(panelDim);
        setFocusable(true);
        addMouseListener(this);
        this.panelDim = panelDim;
        this.planeDim = planeDim;
        query = new geo.Point[2];
        query[0] = null;
        query[1] = null;
        mode = Mode.ADD;
        points = new HashSet<>();
    }

    public void setHistory(History history) {
        this.history = history;
        repaint();
    }

    private void selectTreap() {
        if (history == null)
            return;
        Treap<geo.Point> heapToDisplay = (Treap<geo.Point>) history
                .getLastUpdatedObjectOfClass(Treap.class, step);
        if (heapToDisplay == null) {
            if (tree == null)
                return;
            heapToDisplay = tree.getTreap();
        }
        treapDisplayer.setTree(heapToDisplay);
        treapSelector.setSelectedItem(heapToDisplay.parent.getName());
        treapDisplayer.setHistory(history);
    }

    public void nextStep() {
        if ((history == null) || (history.getStep() == step))
            return;
        step++;
        selectTreap();
        repaint();
    }

    public void previousStep() {
        if (step == 0)
            return;
        step--;
        selectTreap();
        repaint();
    }

    public void resetStep() {
        step=0;
        selectTreap();
        repaint();
    }

    public void lastStep() {
        if (history != null)
            step = history.getStep();
        selectTreap();
        repaint();
    }

    public void clear() {
        treapSelector.removeAllItems();
        treeDisplayer.setTree(null);
        treeDisplayer.setHistory(null);
        treapDisplayer.setHistory(null);
        treapDisplayer.setTree(null);
        points = new HashSet<>();
        query = new geo.Point[2];
        setHistory(null);
        tree = null;
    }

    public void computeTree() {
        treeDisplayer.setHistory(null);
        treapDisplayer.setHistory(null);
        treeDisplayer.setTree(null);
        setHistory(null);
        tree = new QueryTree2D<>(points.toArray(new geo.Point[0]),
                Point.COMPARE_X, Point.COMPARE_Y);
        treeDisplayer.setTree(tree);
        treapDisplayer.setTree(tree.getTreap());
        allTreaps = new HashMap<>();
        tree.buildAllTreaps(allTreaps);
        treapSelector.removeAllItems();
        for (String key: allTreaps.keySet()) {
            treapSelector.addItem(key);
        }
        treapSelector.setSelectedItem(tree.getName());
    }

    public Tree getTreap(String name) {
        if ((allTreaps == null) || (!allTreaps.containsKey(name)))
            return null;
        return allTreaps.get(name);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        double blockW = (double) panelDim.width / planeDim.width;
        double blockH = (double) panelDim.height / planeDim.height;
        for (geo.Point point: points) {
            g.setColor(history == null ? History.Status.DEFAULT.color :
                    history.getStatus(point, step).color);
            g.fillRect((int)(blockW * point.x), (int)(blockH * point.y),
                    (int) blockW, (int) blockH);
        }
        g.setColor(QUERY_COLOR);
        ((Graphics2D) g).setStroke(new BasicStroke((int) blockH / 4));
        for (Point point: query) {
            if (point == null)
                continue;
            g.drawRect((int)(blockW * (point.x)),
                    (int)(blockH * (point.y)),
                    (int) (blockW), (int) (blockH));
        }
    }

    enum Mode {
        QUERY("Set Query"),
        ADD("Add Point"),
        REMOVE("Remove Point");

        public final String name;

        Mode(String name) {
            this.name = name;
        }

        public static Mode getByName(String name) {
            for (Mode mode: Mode.values())
                if (mode.name.equals(name))
                    return mode;
            return null;
        }
    }

    public void editPoints(geo.Point pos) {
        boolean updateTrees = false;
        boolean updateHistory = false;
        switch (mode) {
            case ADD:
                if (!points.contains(pos))
                    updateTrees = true;
                points.add(pos);
                query = new geo.Point[2];
                break;
            case REMOVE:
                if (points.contains(pos))
                    updateTrees = true;
                points.remove(pos);
                query = new geo.Point[2];
                break;
            case QUERY:
                if ((query[0] != null) && (query[1] == null)) {
                    query[1] = pos;
                    if (tree == null)
                        computeTree();
                    geo.Point pMin = new geo.Point(min(query[0].x, query[1].x),
                            min(query[0].y, query[1].y));
                    geo.Point pMax = new geo.Point(max(query[0].x, query[1].x),
                            max(query[0].y, query[1].y));
                    history = new History();
                    tree.search(pMin, pMax, history);
                    treeDisplayer.setHistory(history);
                    lastStep();
                    selectTreap();
                    treapDisplayer.setHistory(history);
                    treeDisplayer.lastStep();
                    treapDisplayer.lastStep();
                } else if ((query[0] == null) || (!query[0].equals(pos))) {
                    updateHistory = true;
                    query = new geo.Point[2];
                    query[0] = pos;
                    query[1] = null;
                }
                repaint();
                break;
        }
        if (updateTrees) {
            computeTree();
        }
        if (updateHistory || updateTrees) {
            treeDisplayer.setHistory(null);
            treapDisplayer.setHistory(null);
            setHistory(null);
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        double blockW = (double) panelDim.width / planeDim.width;
        double blockH = (double) panelDim.height / planeDim.height;
        geo.Point pos = new geo.Point((int)((double) mouseEvent.getX() / blockW),
                (int)((double) mouseEvent.getY() / blockH));
        editPoints(pos);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) { }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) { }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) { }

    @Override
    public void mouseExited(MouseEvent mouseEvent) { }
}
