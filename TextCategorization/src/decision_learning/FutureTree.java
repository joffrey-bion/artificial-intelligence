package decision_learning;

import java.util.LinkedList;

import data.ArticleSet;

/**
 * Represents a future node of the the tree, which is stored in the priority queue.
 */
public class FutureTree implements Comparable<FutureTree> {

    private double priority;

    // features of this potential node
    private Integer category;
    private String bestWord;
    private ArticleSet examples;
    private LinkedList<String> words;

    // link to parent in the actual Tree
    private Tree parent;
    private boolean branch;

    /**
     * Create a future internal node.
     * 
     * @param parent
     *            The parent node of the future node.
     * @param branch
     *            The branch of the parent this node will be attached to.
     * @param examples
     *            The subset of the training examples consistent with this future
     *            node.
     * @param words
     *            The subset of the words available for this future node.
     */
    public FutureTree(Tree parent, boolean branch, ArticleSet examples, LinkedList<String> words) {
        if (examples.isEmpty() || words.isEmpty()) {
            throw new NullPointerException();
        }
        this.parent = parent;
        this.branch = branch;
        this.examples = examples;
        this.words = words;
    }

    /**
     * Create a future leaf.
     * 
     * @param parent
     *            The parent node of the future leaf.
     * @param branch
     *            The branch of the parent this leaf will be attached to.
     * @param category
     *            The category represented by the future leaf.
     */
    public FutureTree(Tree parent, boolean branch, int category) {
        this.parent = parent;
        this.branch = branch;
        this.category = category;
    }

    public void setBestWord(String word) {
        bestWord = word;
    }

    public void setPriority(double value) {
        priority = value;
    }

    public ArticleSet getExamples() {
        return examples;
    }

    public LinkedList<String> getWords() {
        return words;
    }

    public Tree getParent() {
        return parent;
    }

    public boolean getBranch() {
        return branch;
    }

    /**
     * Force this future tree to become a leaf (category), using the mode of the
     * underlying set of examples.
     */
    public Tree toLeaf() {
        if (category != null) {
            return new Tree(category);
        } else {
            return new Tree(examples.mode());
        }
    }

    /**
     * Convert this future tree into an actual Tree object. It can be an internal
     * node as well as a leaf.
     */
    public Tree toTree() {
        if (category != null) {
            return new Tree(category);
        } else {
            return new Tree(bestWord);
        }
    }

    @Override
    public int compareTo(FutureTree o) {
        double diff = this.priority - o.priority;
        if (diff < 0) {
            return -1;
        } else if (diff == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        if (category != null) {
            return "<" + category + "> IG = " + priority;
        } else {
            return "[" + bestWord + "] IG = " + priority;
        }
    }
}
