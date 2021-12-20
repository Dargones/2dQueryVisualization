package gui;

import geo.History;
import geo.Point;
import geo.Tree;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

/** A GUI class for displaying Trees (Treap or QueryTree2D)*/
public class TreeDisplayer extends JPanel
        implements MouseMotionListener, MouseListener {

    private Tree tree;
    private History history; // history of the latest query
    private int step;

    // height of a tree node as a fraction of the panel height
    private static final double BOX_HEIGHT_AS_FRACTION = 0.012;
    // minimum height of a tree node
    private static final int BOX_HEIGHT_MIN = 20;

    // width/height of a tree node. Has to be larger for treaps because a node
    // in a treap stores more info (2 points) when a BST does
    private final int box_width_multiplier;

    private Dimension dim; // dimension of the panel
    private geo.Point prevMousePos; // previous recorded position of the moust
    private int shiftX, shiftY; // by how much the tree should be shifted
    private int boxHeight;
    private int boxWidth;
    private BasicStroke stroke;
    private Font baseFont;


    public TreeDisplayer(Dimension dim, int box_width_multiplier) {
        setPreferredSize(dim);
        setFocusable(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        setBorder(new LineBorder(Color.BLACK, 5));
        this.dim = dim;
        this.box_width_multiplier = box_width_multiplier;
        prevMousePos = new geo.Point(0, 0);
        updateSizes();
    }

    private void updateSizes() {
        shiftX = 0;
        shiftY = 0;
        boxHeight = Integer.max(BOX_HEIGHT_MIN,
                (int)(dim.getWidth() * BOX_HEIGHT_AS_FRACTION));
        boxWidth = boxHeight * box_width_multiplier;
        stroke = new BasicStroke(boxHeight /10);
        baseFont = new Font(Font.SANS_SERIF, Font.BOLD, boxHeight / 3 * 2);
        repaint();
    }

    public void setTree(Tree tree) {
        this.tree = tree;
        updateSizes();
    }

    public void setHistory(History history) {
        this.history = history;
        repaint();
    }

    public void nextStep() {
        if ((history == null) || (history.getStep() == step))
            return;
        step++;
        repaint();
    }

    public void previousStep() {
        if (step == 0)
            return;
        step--;
        repaint();
    }

    public void resetStep() {
        step=0;
        repaint();
    }

    public void lastStep() {
        if (history != null)
            step = history.getStep();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tree == null)
            return;
        drawTree((Graphics2D) g, tree, shiftX + dim.width / 2,
                shiftY + boxHeight / 2 + (int) stroke.getLineWidth(),
                tree.getDepth());
    }

    public void drawTree(Graphics2D g, Tree tree, int xPos, int yPos,
                         int depth) {
        g.setStroke(stroke);
        g.setFont(baseFont);
        g.setColor(Color.BLACK);
        int distance;
        distance = (int) (boxWidth * Math.pow(2, depth - 2));
        if (tree.getLeft() != null) {
            g.drawLine(xPos, yPos + boxHeight / 2,
                    xPos - distance, yPos + 3 * boxHeight / 2);
            drawTree(g, tree.getLeft(), xPos - distance,
                    yPos + 2 * boxHeight, depth - 1);
        }
        if (tree.getRight() != null) {
            g.drawLine(xPos, yPos + boxHeight / 2, xPos + distance,
                    yPos + 3 * boxHeight / 2);
            drawTree(g, tree.getRight(), xPos + distance,
                    yPos + 2 * boxHeight, depth - 1);
        }
        g.setColor(history == null ?
                History.Status.DEFAULT.color :
                history.getStatus(tree, step).color);
        g.fillRect(xPos - boxWidth / 2, yPos - boxHeight / 2,
                boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(xPos - boxWidth / 2, yPos - boxHeight / 2,
                boxWidth, boxHeight);
        Rectangle2D r = baseFont.getStringBounds(tree.getName(),
                g.getFontRenderContext());
        g.drawString(tree.getName(), xPos - (int) r.getWidth() / 2,
                yPos + (int) r.getHeight() / 3);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        geo.Point mousePos = new Point(mouseEvent.getX(), mouseEvent.getY());
        shiftX += mousePos.x - prevMousePos.x;
        shiftY += mousePos.y - prevMousePos.y;
        prevMousePos = mousePos;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        prevMousePos = new geo.Point(mouseEvent.getX(), mouseEvent.getY());
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) { }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) { }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) { }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) { }

    @Override
    public void mouseExited(MouseEvent mouseEvent) { }
}
