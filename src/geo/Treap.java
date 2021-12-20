package geo;

import java.util.*;

import static java.lang.Integer.max;

/** Represents a treap used in 2D queries.
 * While it is coded so it could be used with a generic argument type,
 * my comments refer to the planar point specifically */
public class Treap<T> implements Tree {

    private final T heapMax; // point with largest/smallest x coord. Never null.
    private final T heapMedian; // point with median y coord. Can be null.
    private final Comparator<T> maxComparator; // compares by y coord.
    private final Comparator<T> medComparator; // compares by x coord.
    private final Treap<T> left, right;
    public final QueryTree2D<T> parent;

    /** Constructs a treap. A treap always has a non-null heapMax associated
     * with it. heapMax is not included in any of the sub-treaps. heapMedian
     * is passed to one of the subtrees. */
    private Treap(List<T> elements, Comparator<T> medComparator,
                  Comparator<T> maxComparator, QueryTree2D<T> parent,
                  boolean presorted) {
        this.parent = parent;
        this.medComparator = medComparator;
        this.maxComparator = maxComparator;
        this.heapMax = Collections.max(elements, maxComparator);
        elements.remove(heapMax);
        if (elements.size() == 0) {
            this.left = null;
            this.right = null;
            this.heapMedian = null;
            return;
        }
        if (!presorted)
            elements.sort(medComparator);
        int midId = elements.size() / 2;
        heapMedian = elements.get(midId);
        if (elements.size() == 1)
            left = null;
        else
            left = new Treap<>(new LinkedList<>(elements.subList(0, midId)),
                    medComparator, maxComparator, parent, true);
        right = new Treap<>(
                new LinkedList<>(elements.subList(midId, elements.size())),
                medComparator, maxComparator, parent, true);
    }

    public Treap(T[] elements, Comparator<T> medComparator,
                 Comparator<T> maxComparator, QueryTree2D<T> parent) {
        this(new LinkedList<T>(Arrays.asList(elements)),
                medComparator, maxComparator, parent, false);
    }


    public Set<T> search(T treeMin, T treeMax, T heapMin, History history) {
        history.updateStatus(this.heapMax, History.Status.VISITED);
        history.updateStatus(this, History.Status.VISITED, true);
        Set<T> result = new HashSet<>();
        if (maxComparator.compare(heapMin, this.heapMax) > 0)
            return result;
        if ((medComparator.compare(treeMax, this.heapMax) >= 0) &&
                (medComparator.compare(this.heapMax, treeMin) >= 0)) {
            history.updateStatus(this, History.Status.ADDED);
            history.updateStatus(this.heapMax, History.Status.ADDED, true);
            result.add(this.heapMax);
        }
        if (heapMedian == null)
            return result;
        history.updateStatus(heapMedian, History.Status.MEDIAN_COMPARED, true);
        if (medComparator.compare(heapMedian, treeMin) >= 0 && left != null)
            result.addAll(left.search(treeMin, treeMax, heapMin, history));
        if (medComparator.compare(treeMax, heapMedian) >= 0 && right != null)
            result.addAll(right.search(treeMin, treeMax, heapMin, history));
        return result;
    }

    @Override
    public String getName() {
        return heapMax.toString() + " | " +
                (heapMedian == null ? "null" : heapMedian.toString());
    }

    @Override
    public Tree getLeft() {
        return left;
    }

    @Override
    public Tree getRight() {
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
        return this == o;
    }

    @Override
    public int hashCode() {
        return heapMax.hashCode();
    }
}
