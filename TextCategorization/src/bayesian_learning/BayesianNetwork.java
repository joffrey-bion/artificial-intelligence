package bayesian_learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import data.Article;
import data.ArticleSet;

public class BayesianNetwork {

    // prior probability of being from category 1 (for a document)
    private double priorProbability1;
    // probability of containing a word knowing being in category 1
    private HashMap<String, Double> conditionalProbabilities1;
    // probability of containing a word knowing being in category 2
    private HashMap<String, Double> conditionalProbabilities2;

    private LinkedList<String> allWords;

    /**
     * Creates a Naive Bayes Model containing all the words, and computes the parameters according
     * to the statistics in the given set of examples.
     */
    public BayesianNetwork(ArticleSet examples, LinkedList<String> words) {
        allWords = words;
        // setting prior probability of category 1
        int categoryCount[] = examples.categoryCount();
        priorProbability1 = (double) categoryCount[0] / examples.size();
        // setting conditional probabilities
        conditionalProbabilities1 = new HashMap<String, Double>();
        conditionalProbabilities2 = new HashMap<String, Double>();
        ArticleSet catPartition[] = examples.categoryPartition();
        for (String word : words) {
            ArticleSet wordPartition[] = catPartition[0].partition(word);
            double p1 = (double) (wordPartition[0].size() + 1) / (catPartition[0].size() + 2);
            conditionalProbabilities1.put(word, p1);
            double p2 = (double) (wordPartition[0].size() + 1) / (catPartition[1].size() + 2);
            conditionalProbabilities2.put(word, p2);
        }
    }

    /**
     * Returns the most likely category for the given article.
     */
    public int decideCategory(Article a) {
        double product1 = priorProbability1;
        double product2 = 1 - priorProbability1;
        for (String word : allWords) {
            if (a.contains(word)) {
                product1 *= conditionalProbabilities1.get(word);
                product2 *= conditionalProbabilities2.get(word);
            } else {
                product1 *= 1 - conditionalProbabilities1.get(word);
                product2 *= 1 - conditionalProbabilities2.get(word);
            }
        }
        //System.out.println("cat1 = " + product1 + "   cat2 = " + product2);
        if (product1 > product2) {
            return 1;
        } else {
            return 2;
        }
    }

    private double discriminationPower(String word) {
        double p1 = conditionalProbabilities1.get(word);
        double p2 = conditionalProbabilities2.get(word);
        return Math.abs(Math.log(p1) - Math.log(p2));
    }

    /**
     * Returns the most discriminative words.
     * 
     * @param number
     *            The maximum number of discriminative words returned.
     * @return A LinkedList of the most discriminative words.
     */
    public LinkedList<String> getMostDiscriminativeWords(int number) {
        ArrayList<String> mostDiscriminant = new ArrayList<>();
        ArrayList<Double> powers = new ArrayList<>();
        for (String word : allWords) {
            double power = discriminationPower(word);
            for (int i = 0; i < number; i++) {
                if (powers.size() <= i || power > powers.get(i)) {
                    mostDiscriminant.add(i, word);
                    powers.add(i, power);
                    break;
                }
            }
        }
        if (mostDiscriminant.size() < number) {
            return new LinkedList<String>(mostDiscriminant);
        } else {
            return new LinkedList<String>(mostDiscriminant.subList(0, number));
        }
    }
}
