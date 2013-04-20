package data;

import java.util.HashMap;
import java.util.LinkedList;

import bayesian_learning.BayesianNetwork;

import decision_learning.Tree;

/**
 * Represents a set of articles which cannot be modified. Some methods results are buffered to
 * improve the efficiency.
 */
public class ArticleSet {

    public static int remaindersBufferUse = 0; 
    
    private LinkedList<Article> articles;

    // buffers
    private Double entropy;
    private int categoryCount[];
    private ArticleSet categoryPartition[];
    private HashMap<String, Double> remaindersBuffer;

    private ArticleSet() {
        this.articles = new LinkedList<>();
        // initialize buffers
        this.entropy = null;
        this.categoryCount = null;
        this.categoryPartition = null;
        this.remaindersBuffer = new HashMap<>();
    }

    /**
     * Create an article set from a map of articles.
     * 
     * @param articles
     *            The map of articles to convert into a set (the keys are not used)
     */
    public ArticleSet(HashMap<Integer, Article> articles) {
        this();
        this.articles.addAll(articles.values());
    }

    public boolean isEmpty() {
        return articles.isEmpty();
    }

    public int size() {
        return articles.size();
    }

    /**
     * Partition this set of articles into 2 subsets according to whether the articles are from
     * category 1 or 2.
     * 
     * @return An array t[], t[0] being the articles of category 1 and t[1] the articles of category
     *         2.
     */
    public ArticleSet[] categoryPartition() {
        if (categoryPartition != null)
            return categoryPartition;
        categoryPartition = new ArticleSet[2];
        categoryPartition[0] = new ArticleSet();
        categoryPartition[1] = new ArticleSet();
        for (Article a : articles) {
            categoryPartition[a.getCategory() - 1].articles.add(a);
        }
        return categoryPartition;
    }

    /**
     * Returns the number of articles for each category.
     * 
     * @return An array t[] of these numbers, t[0] being the number of articles of category 1 and
     *         t[1] the number of articles of category 2.
     */
    public int[] categoryCount() {
        if (categoryCount != null)
            return categoryCount;
        categoryCount = new int[] { 0, 0 };
        categoryCount[0] = categoryPartition()[0].size();
        ;
        categoryCount[1] = categoryPartition()[1].size();
        ;
        return categoryCount;
    }

    /**
     * Returns the id of the common category if all articles in this set are from the same category,
     * {@code null} otherwise.
     */
    public Integer commonCategory() {
        if (articles.isEmpty())
            throw new RuntimeException("Cannot check the category of an empty list of articles");
        int categoryCount[] = categoryCount();
        if (categoryCount[0] == 0) {
            return 2;
        } else if (categoryCount[1] == 0) {
            return 1;
        } else {
            return null;
        }
    }

    /**
     * Returns the id of the most popular category among this set of articles.
     */
    public int mode() {
        if (articles.isEmpty())
            throw new RuntimeException("Cannot get the mode of an empty list of articles");
        int categoryCount[] = categoryCount();
        if (categoryCount[0] > categoryCount[1]) {
            return 1;
        } else if (categoryCount[0] < categoryCount[1]) {
            return 2;
        } else {
            return Category.random();
        }
    }

    private static final double log2 = Math.log(2);
    /**
     * Returns the entropy of a distribution of probabilities.
     * 
     * @param ps
     *            The probabilities in the distribution.
     * @return The entropy of the distribution {@code ps}.
     */
    private static double I(double... ps) {
        double sum = 0;
        for (double p : ps) {
            if (p != 0) {
                sum += -p * Math.log(p) / log2;
            }
        }
        return sum;
    }

    /**
     * Return the entropy of this set of articles.
     */
    public double entropy() {
        if (entropy != null)
            return entropy;
        if (articles.size() == 0)
            return 0;
        int counts[] = categoryCount();
        double p1 = (double) counts[0] / (double) articles.size();
        double p2 = (double) counts[1] / (double) articles.size();
        entropy = I(p1, p2);
        return entropy;
    }

    /**
     * Return the remainder of this set of articles.
     */
    public double remainder(String word) {
        if (word == null)
            throw new IllegalArgumentException("word cannot be null");
        if (remaindersBuffer.containsKey(word)) {
            remaindersBufferUse++;
            return remaindersBuffer.get(word);
        }
        ArticleSet[] subset = this.partition(word);
        double a = (double) subset[0].articles.size() / (double) articles.size()
                * subset[0].entropy();
        double b = (double) subset[1].articles.size() / (double) articles.size()
                * subset[1].entropy();
        double remainder = a + b;
        remaindersBuffer.put(word, remainder);
        return remainder;
    }

    /**
     * Partition this set of articles into 2 subsets according to whether they contain the word
     * wordId or not.
     * 
     * @param word
     *            The word used to divide the set.
     * @return An array of 2 ArticleSet, index 0 for the articles containing the word, 1 for those
     *         which does not.
     */
    public ArticleSet[] partition(String word) {
        if (word == null)
            throw new IllegalArgumentException("word cannot be null");
        ArticleSet subsets[] = { new ArticleSet(), new ArticleSet() };
        for (Article a : articles) {
            if (a.contains(word)) {
                subsets[0].articles.add(a);
            } else {
                subsets[1].articles.add(a);
            }
        }
        return subsets;
    }

    /**
     * Use the given decision tree to determine the category of each article in this set.
     * 
     * @param decisionTree
     *            The decision tree used to decide which category each article belongs to
     * @return The percentage of success.
     */
    public Double test(Tree decisionTree) {
        int good = 0;
        for (Article a : articles) {
            if (decisionTree.decideCategory(a) == a.getCategory()) {
                good++;
            }
        }
        return (double) good / articles.size() * 100;
    }

    /**
     * Use the given Bayesian network to determine the category of each article in this set.
     * 
     * @param bn
     *            The Bayesian network used to decide which category each article belongs to
     * @return The percentage of success.
     */
    public Double test(BayesianNetwork bn) {
        int good = 0;
        for (Article a : articles) {
            if (bn.decideCategory(a) == a.getCategory()) {
                good++;
            }
        }
        return (double) good / articles.size() * 100;
    }
}
