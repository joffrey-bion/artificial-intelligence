package text_categorization;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import bayesian_learning.BayesianNetwork;

import data.ArticleSet;
import data.Data;
import decision_learning.DecisionLearning;
import decision_learning.RecursiveDecisionLearning;
import decision_learning.Tree;

public class Main {

    private static ArticleSet trainArticles;
    private static ArticleSet testArticles;
    private static LinkedList<String> words;

    public static void main(String args[]) {
        System.out.print("Parsing the files...");
        Data data = new Data("data/words.txt", "data/trainData.txt", "data/trainLabel.txt",
                "data/testData.txt", "data/testLabel.txt");
        System.out.println("done");
        // System.out.println(data);

        System.out.print("Creating sets...");
        trainArticles = new ArticleSet(data.trainArticles);
        testArticles = new ArticleSet(data.testArticles);
        words = new LinkedList<>(data.words.values());
        System.out.println("done");

        System.out.println();
        compare4trees();

        System.out.print("Building bayesian network...");
        BayesianNetwork bn = new BayesianNetwork(trainArticles, words);
        System.out.println("done");
        printTest(bn);
        LinkedList<String> bestWords = bn.getMostDiscriminativeWords(10);
        System.out.println(bestWords);

        int[] nbNodesTab = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                42, 44, 46, 48, 50, 55, 60, 65, 70, 75, 78, 79, 80, 81, 82, 83, 90, 100 };
        System.out.println();
        testAndWriteFile(true, nbNodesTab, "resultsTrue.txt");
        System.out.println();
        testAndWriteFile(false, nbNodesTab, "resultsFalse.txt");

    }

    private static Tree buildTree(String treeName, boolean prioType, int nbNodes, boolean print) {
        System.out.println("Building partial tree " + treeName + "...");
        System.out.print("    priority: best IG");
        if (prioType) {
            System.out.print(" times number of articles");
        }
        System.out.println("\n    max # nodes: " + nbNodes);
        Tree dtl = DecisionLearning.DTL(trainArticles, words, nbNodes, prioType);
        if (print)
            printTest(treeName, dtl);
        return dtl;
    }

    private static void printTest(BayesianNetwork bn) {
        System.out.println("Test of the bayesian network:");
        System.out.println("   test : " + testArticles.test(bn) + "% success");
        System.out.println("   train: " + trainArticles.test(bn) + "% success");
    }

    private static void printTest(String treeName, Tree dtl) {
        System.out.println(treeName + " - " + dtl.nbAttributes() + " nodes:");
        System.out.println("   test : " + testArticles.test(dtl) + "% success");
        System.out.println("   train: " + trainArticles.test(dtl) + "% success");
    }

    private static void testAndWriteFile(boolean prioType, int nbNodesTab[], String filename) {
        FileWriter outFile;
        try {
            outFile = new FileWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        PrintWriter out = new PrintWriter(outFile);
        System.out.println("Writing results to file '" + filename + "'...");
        for (int nbNodes : nbNodesTab) {
            Tree tree = DecisionLearning.DTL(trainArticles, words, nbNodes, prioType);
            String line = nbNodes + "\t";
            line += testArticles.test(tree).toString().replace('.', ',') + "\t";
            line += trainArticles.test(tree).toString().replace('.', ',');
            out.println(line);
            System.out.println(line);
        }
        out.close();
    }

    public static void compare4trees() {
        System.out.println("Building full tree recursivly...");
        Tree fullDTL = RecursiveDecisionLearning.DTL(trainArticles, words);
        // System.out.println(fullDTL + "\n");

        Tree partDTL1 = buildTree("Part DTL1", false, 100, false);
        System.out.println();
        System.out.println(DecisionLearning.getFirstPickedNodes());
        System.out.println();
        System.out.println(partDTL1 + "\n");

        Tree partDTL2 = buildTree("Part DTL2", true, 100, false);
        System.out.println();
        System.out.println(DecisionLearning.getFirstPickedNodes());
        System.out.println();
        System.out.println(partDTL2 + "\n");

        Tree partDTL3 = buildTree("Part DTL3", true, 30, false);
        System.out.println();
        System.out.println(DecisionLearning.getFirstPickedNodes());
        System.out.println();
        System.out.println(partDTL3 + "\n");

        System.out.println("Trees sizes:");
        System.out.println("Full DTL : " + fullDTL.nbAttributes() + " internal nodes, "
                + fullDTL.size() + " total size");
        System.out.println("Part DTL1: " + partDTL1.nbAttributes() + " internal nodes, "
                + partDTL1.size() + " total size");
        System.out.println("Part DTL2: " + partDTL2.nbAttributes() + " internal nodes, "
                + partDTL2.size() + " total size");
        System.out.println("Part DTL3: " + partDTL3.nbAttributes() + " internal nodes, "
                + partDTL3.size() + " total size");
        System.out.println();

        System.out.println("Remainders buffer used " + ArticleSet.remaindersBufferUse + " times");
        System.out.println();

        System.out.println("Testing on test set...");
        printTest("Full DTL", fullDTL);
        printTest("Part DTL1", partDTL1);
        printTest("Part DTL2", partDTL2);
        printTest("Part DTL3", partDTL3);
        System.out.println();
    }
}
