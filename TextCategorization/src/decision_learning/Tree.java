package decision_learning;

import data.Article;
import data.Category;

public class Tree {

    // attributes for a leaf
    private Integer category;
    // attributes for internal nodes
    private String word;
    private Tree left;
    private Tree right;

    /**
     * Creates an internal node representing the given word.
     */
    public Tree(String word) {
        if (word == null) {
            throw new RuntimeException("Cannot create a tree (node) from a null word");
        }
        this.word = word;
        this.category = null;
    }

    /**
     * Creates a leaf representing the given category.
     */
    public Tree(Integer category) {
        if (category == null) {
            throw new RuntimeException("Cannot create a tree (leaf) from a null category");
        }
        this.word = null;
        this.category = category;
    }

    public int size() {
        if (isLeaf()) {
            return 1;
        } else {
            return 1 + left.size() + right.size();
        }
    }

    public int nbAttributes() {
        if (isLeaf()) {
            return 0;
        } else {
            return 1 + left.nbAttributes() + right.nbAttributes();
        }
    }

    /**
     * Returns whether this node is a leaf (as opposed to an internal node).
     */
    public boolean isLeaf() {
        return category != null;
    }

    /**
     * Attach the subtree {@code subtree} to the the branch labeled by {@code value}.
     * 
     * @param value
     *            The value of this attribute which gives the branch to follow.
     * @param subtree
     *            The subtree we want to attach.
     */
    public void setChild(boolean value, Tree subtree) {
        if (value) {
            left = subtree;
        } else {
            right = subtree;
        }
    }

    /**
     * Returns the subtree pointed by the branch labeled by {@code value}.
     * 
     * @param value
     *            The value of this attribute which gives the branch to follow.
     * @return The subtree pointed by the branch labeled by {@code value}.
     */
    public Tree getChild(boolean value) {
        if (value) {
            return left;
        } else {
            return right;
        }
    }

    /**
     * Returns the word represented by this node. Works only on internal nodes.
     */
    public String getWord() {
        if (isLeaf()) {
            throw new RuntimeException("A leaf is not a word");
        }
        return word;
    }

    /**
     * Returns the category represented by this node. Works only on leaves.
     */
    public Integer getCategory() {
        if (!isLeaf()) {
            throw new RuntimeException("Only leaves are categories");
        }
        return category;
    }

    /**
     * Use this tree to determine the category of the article a.
     * 
     * @param a
     *            The Article of which we want the category.
     * @return The Integer representing the category.
     */
    public Integer decideCategory(Article a) {
        if (isLeaf()) {
            return category;
        } else {
            return getChild(a.contains(word)).decideCategory(a);
        }
    }

    /*
     * Fancy printing methods (Actually not fancy anymore because unicode characters
     * were removed)
     */

    private final char branch = '├';
    private final char line = '│';
    private final char lastBranch = '└';

    public String toString(String indent) {
        String newIndent = indent.replace(lastBranch, ' ').replace(branch, line);
        String indL = newIndent + "   " + branch;
        String indR = newIndent + "   " + lastBranch;
        String res = indent;
        if (isLeaf()) {
            res += "<" + Category.getName(category) + ">";
        } else {
            res += "[" + word + "]\n";
            if (left == null) {
                res += indL + "null";
            } else {
                res += left.toString(indL);
            }
            res += "\n";
            if (right == null) {
                res += indR + "null";
            } else {
                res += right.toString(indR);
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return toString("");
    }
}
