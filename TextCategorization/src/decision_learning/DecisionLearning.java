package decision_learning;

import java.util.LinkedList;

import data.ArticleSet;

public class DecisionLearning {

    private static final int LEAF_PRIORITY = -1;

    private static String pickedNodes;
        
    /**
     * Learn a decision tree from the set of examples and attributes (words).
     * 
     * @param examples
     *            The list of examples from which to learn the tree
     * @param words
     *            The list of words (attributes) to consider potentially
     * @param maxNbNodes
     *            The maximum number of internal nodes in the tree, corresponding to how many
     *            attributes will be considered
     * @param prioType
     *            The way to compute the priority of the nodes : {@code false} to use best IG only,
     *            {@code true} to use best IG times number of examples
     * @return The root of the decision tree.
     */
    public static Tree DTL(ArticleSet examples, LinkedList<String> words, int maxNbNodes,
            boolean prioType) {
        if (maxNbNodes < 0)
            throw new IllegalArgumentException("The number of nodes must be positive");
        if (examples.isEmpty())
            return new Tree(data.Category.random());
        if (maxNbNodes == 0)
            return new Tree(examples.mode());
        FutureTreeQueue queue = new FutureTreeQueue();
        Tree root = null;
        queue.offer(createFutureNode(null, true, examples, words, prioType));
        int nbNodes = 0;
        resetPickedNodes();
        while (!queue.isEmpty() && nbNodes < maxNbNodes) {
            // get the most promising node in the queue and attach it to the tree
            FutureTree ft = queue.poll();
            Tree newNode = ft.toTree();
            root = attachToTree(root, ft, newNode);
            nbNodes++;
            rememberPickedNode(ft, newNode, nbNodes);
            // expand the new node
            if (!newNode.isLeaf()) {
                // remove word from the list of available words for the children of the new node
                LinkedList<String> newWords = new LinkedList<>(ft.getWords());
                newWords.remove(newNode.getWord());
                // add the children to the queue, with new sets of examples
                ArticleSet examplesSub[] = ft.getExamples().partition(newNode.getWord());
                queue.offer(createFutureNode(newNode, true, examplesSub[0], newWords, prioType));
                queue.offer(createFutureNode(newNode, false, examplesSub[1], newWords, prioType));
            }
        }
        // attach leaves to the last branches, according to the mode of the underlying examples
        while (!queue.isEmpty()) {
            FutureTree ft = queue.poll();
            Tree newNode = ft.toLeaf();
            root = attachToTree(root, ft, newNode);
        }
        return root;
    }

    private static FutureTree createFutureNode(Tree parent, boolean branch, ArticleSet examples,
            LinkedList<String> words, boolean exampleSizeInPriority) {
        FutureTree ft;
        Integer category = examples.commonCategory();
        if (category != null) {
            // create a leaf with the common classification
            ft = new FutureTree(parent, branch, category);
            ft.setPriority(LEAF_PRIORITY);
        } else if (words.isEmpty()) {
            // create a leaf with the most common category among examples
            ft = new FutureTree(parent, branch, examples.mode());
            ft.setPriority(LEAF_PRIORITY);
        } else {
            // general case, create internal node and compute its priority
            ft = new FutureTree(parent, branch, examples, words);
            double entropy = examples.entropy();
            double maxIG = -1;
            for (String word : words) {
                double IG = entropy - examples.remainder(word);
                if (maxIG < IG) {
                    maxIG = IG;
                    ft.setBestWord(word);
                }
            }
            ft.setPriority(exampleSizeInPriority ? maxIG * examples.size() : maxIG);
        }
        return ft;
    }

    /**
     * Attach the new node to the tree 'root', or create the tree from the new node.
     * 
     * @param root
     *            The root of the current tree (may be {@code null} if the new node is the first)
     * @param ft
     *            The FutureTree object used to create the new node.
     * @param newNode
     *            The new node to add to the tree.
     * @return The root of the tree.
     */
    private static Tree attachToTree(Tree root, FutureTree ft, Tree newNode) {
        if (ft.getParent() == null) {
            root = newNode; // for the first node
        } else {
            ft.getParent().setChild(ft.getBranch(), newNode);
        }
        return root;
    }

    
    private static void resetPickedNodes() {
        pickedNodes = "";
    }

    private static void rememberPickedNode(FutureTree ft, Tree t, int nbNodes) {
        if (nbNodes > 10)
            return;
        pickedNodes += nbNodes + ". picked node: " + ft;
        if (ft.getParent() == null) {
            pickedNodes += "  (root)";
        } else {
            pickedNodes += "  (parent word: [" + ft.getParent().getWord() + "]";
            pickedNodes += "  child side: " + (ft.getBranch() ? "left-true" : "right-false") + ")";
        }
        pickedNodes += "\n";
    }

    /**
     * Return a String representing the 10 first picked nodes.
     */
    public static String getFirstPickedNodes() {
        return pickedNodes;
    }
}
