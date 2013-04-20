package decision_learning;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * The priority queue used by the decision learning. It is intended to make its instantiation
 * simpler and more abstract.
 */
@SuppressWarnings("serial")
public class FutureTreeQueue extends PriorityQueue<FutureTree> {

    private static class TreeComparator implements Comparator<FutureTree> {
        @Override
        public int compare(FutureTree o1, FutureTree o2) {
            return o2.compareTo(o1); // reversed to have the largest element at the head of the list
        }
    }

    public FutureTreeQueue() {
        super(10, new TreeComparator());
    }

    public String toString() {
        PriorityQueue<FutureTree> copy = new PriorityQueue<FutureTree>(this);
        String res = "[";
        while (!copy.isEmpty()) {
            res += "" + copy.poll();
            if (!copy.isEmpty())
                res += ", ";
        }
        return res + "]";
    }
}
