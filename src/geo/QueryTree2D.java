package geo;

import java.util.*;

import static java.lang.Integer.max;

/** Represents the primary BST on which a 2D query is performed
 * While it is coded so it could be used with a generic argument type,
 * my comments refer to the planar point specifically */
public class QueryTree2D<T> implements Tree {

    private final T node; // the point with median x coordinate
    private final QueryTree2D<T> left, right;
    private final Comparator<T> mainComparator, treapComparator;
    private final Treap<T> treap; // treap hanging from the node

    /**
     * Constructs a new BST for a given set of points
     * @param elements        Tree is constructed from elements[start...end)
     * @param mainComparator  To compare by x (used to construct the tree)
     * @param treapComparator To compare by y (passed to treap constructor)
     * @param start           Tree is constructed from elements[start...end)
     * @param end             Tree is constructed from elements[start...end)
     * @param presorted       Whether elements are already sorted by x
     * @param isLeftSubtree   Whether the tree is a left subtree is relevant
     *                        for constructing the corresponding treap
     */
    private QueryTree2D(T[] elements, Comparator<T> mainComparator,
                        Comparator<T> treapComparator, int start, int end,
                        boolean presorted, boolean isLeftSubtree) {
        this.mainComparator = mainComparator;
        this.treapComparator = treapComparator;
        if (isLeftSubtree)
            this.treap = new Treap<>(Arrays.copyOfRange(elements, start, end),
                    treapComparator, mainComparator, this);
        else
            this.treap = new Treap<>(Arrays.copyOfRange(elements, start, end),
                    treapComparator, (x, y) -> -mainComparator.compare(x, y),
                    this);
        if (!presorted)
            Arrays.sort(elements, mainComparator);
        int midId = (start + end) / 2;
        this.node = elements[midId];
        if (midId == start)
            this.left = null;
        else
            this.left = new QueryTree2D<>(elements, mainComparator,
                    treapComparator, start, midId, true, true);
        if (midId == end - 1)
            this.right = null;
        else
            this.right = new QueryTree2D<>(elements, mainComparator,
                    treapComparator, midId+1, end, true, false);
    }

    public QueryTree2D(T[] elements, Comparator<T> mainComparator,
                       Comparator<T> treapComparator) {
        this(elements, mainComparator, treapComparator, 0, elements.length,
                false, false);
    }


    public Set<T> search(T min, T max, History history) {
        history.updateStatus(node, History.Status.VISITED);
        history.updateStatus(this, History.Status.VISITED, true);
        Set<T> result = new HashSet<>();
        if (mainComparator.compare(node, max) > 0) { // results are to the left
            if (left != null)
                return left.search(min, max, history);
            return result;
        }
        if (mainComparator.compare(min, node) > 0) { // results are to the right
            if (right != null)
                return right.search(min, max, history);
            return result;
        }
        // the node associated with the tree falls in the x-range of the query,
        // so the treaps are used to calculate the result
        if (treapComparator.compare(max, node) >= 0 &&
                treapComparator.compare(node, min) >= 0) {
            history.updateStatus(node, History.Status.ADDED);
            history.updateStatus(this, History.Status.ADDED, true);
            result.add(node);
        }
        if (right != null)
            result.addAll(right.treap.search(min, max, max, history));
        if (left != null)
            result.addAll(left.treap.search(min, max, min, history));
        return result;
    }

    public Treap<T> getTreap() {
        return treap;
    }

    /** Populate the argument map with all treaps in the tree and children */
    public void buildAllTreaps(HashMap<String, Treap<T>> result) {
        result.put(node.toString(), treap);
        if (left != null)
            left.buildAllTreaps(result);
        if (right != null)
            right.buildAllTreaps(result);
    }

    @Override
    public String getName() {
        return node.toString();
    }

    @Override
    public QueryTree2D<T> getLeft() {
        return left;
    }

    @Override
    public QueryTree2D<T> getRight() {
        return right;
    }

    @Override
    public int getDepth() {
        int depth = 0;
        if (left != null)
            depth = left.getDepth() + 1;
        if (right != null)
            depth = max(depth, right.getDepth() + 1);
        return depth;
    }

    @Override
    public boolean equals(Object o) {
        return this == o; // works for one plane
    }

    @Override
    public int hashCode() {
        return this.node.hashCode();
    }
}
