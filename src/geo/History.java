package geo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/** Stores the history of how the last query was processed step by step to
 * enable replay mode in the GUI */
public class History {

    private final HashMap<Object, ArrayList<StatusUpdate>> history;
    private final ArrayList<Tuple<Object, StatusUpdate>> updateList;
    private int step;

    public History() {
        this.step = 0;
        this.history = new HashMap<>(); // stores all updates for given object
        this.updateList = new ArrayList<>(); // all status updates in FIFO order
    }

    /**
     * Record a status update for a given object.
     * @param object    The object, whose status is updated
     * @param status    The new status to be assigned to the object
     * @param sameStep  Whether to perform update simultaneously with the last
     */
    public void updateStatus(Object object, Status status, boolean sameStep) {
        if (!sameStep)
            step++;
        history.putIfAbsent(object, new ArrayList<>());
        StatusUpdate update = new StatusUpdate(status, step);
        history.get(object).add(update);
        updateList.add(new Tuple<>(object, update));
    }

    public void updateStatus(Object object, Status status) {
        updateStatus(object, status, false);
    }

    /** Get the status of the object at a certain step */
    public Status getStatus(Object object, int step) {
        Status result = Status.DEFAULT;
        for (StatusUpdate update: history
                .getOrDefault(object, new ArrayList<>()))
            if (update.step <= step)
                result = update.status;
        return result;
    }

    /** Get the last object whose status was updated prior to given step */
    public Object getLastUpdatedObjectOfClass(Class c, int step) {
        int index = Collections.binarySearch(updateList,
                new Tuple<>(null, new StatusUpdate(Status.DEFAULT, step)),
                Comparator.comparingInt(a -> a.snd.step));
        while ((index < updateList.size() - 1) &&
                (updateList.get(index + 1).snd.step == step)) index++;
        while (index >= 0) {
            if (c.isInstance(updateList.get(index).fst))
                return updateList.get(index).fst;
            index--;
        }
        return null;
    }

    public int getStep() {
        return step;
    }

    public enum Status {
        DEFAULT(Color.LIGHT_GRAY),
        VISITED(Color.YELLOW),
        MEDIAN_COMPARED(Color.PINK),
        ADDED(Color.BLUE);

        public final Color color;

        Status(Color color) {
           this.color = color;
        }
    }

    private static class StatusUpdate {
        public final Status status;
        public final int step;

        public StatusUpdate(Status status, int step) {
            this.status = status;
            this.step = step;
        }
    }

    private static class Tuple<T,K> {
        public final T fst;
        public final K snd;

        public Tuple(T fst, K snd) {
            this.fst = fst;
            this.snd = snd;
        }
    }
}
