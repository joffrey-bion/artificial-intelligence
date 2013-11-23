package decision_learning;

import java.util.Collection;

import data.ArticleSet;

public class RecursiveDecisionLearning {

    private static String chooseWord(ArticleSet examples, Collection<String> words) {
        if (examples == null || examples.isEmpty()) {
            throw new RuntimeException("Cannot choose a word given an empty list of articles");
        } else if (words == null || words.isEmpty()) {
            throw new RuntimeException("Cannot choose a word given an empty list of words");
        }
        double entropy = examples.entropy();
        double maxIG = -1;
        String bestWord = null;
        for (String word : words) {
            double IG = entropy - examples.remainder(word);
            if (maxIG < IG) {
                maxIG = IG;
                bestWord = word;
            }
        }
        return bestWord;
    }

    public static Tree DTL(ArticleSet examples, Collection<String> words) {
        return DTL(examples, words, new Tree(data.Category.random()));
    }

    private static Tree DTL(ArticleSet examples, Collection<String> words, Tree defaultTree) {
        if (examples.isEmpty()) {
            return defaultTree;
        }
        // check if all examples have the same classification, and return it if so
        Integer category = examples.commonCategory();
        if (category != null) {
            return new Tree(category);
        }
        // return the most common category among examples if no attributes are given
        if (words.isEmpty()) {
            return new Tree(examples.mode());
        }
        // choose the word represented by the new node
        String bestWord = chooseWord(examples, words);
        Tree tree = new Tree(bestWord);
        // remove the best word from the words
        words.remove(bestWord);
        // create the children of the root with recursive calls to DTL
        ArticleSet examplesSub[] = examples.partition(bestWord);
        Tree subtreeTrue = DTL(examplesSub[0], words, new Tree(examples.mode()));
        Tree subtreeFalse = DTL(examplesSub[1], words, new Tree(examples.mode()));
        tree.setChild(true, subtreeTrue);
        tree.setChild(false, subtreeFalse);
        // put the best word back to keep the words set unchanged
        words.add(bestWord);
        return tree;
    }
}
