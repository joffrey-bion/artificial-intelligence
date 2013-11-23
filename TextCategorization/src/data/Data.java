package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Data {

    public HashMap<Integer, String> words;
    public HashMap<Integer, Article> trainArticles;
    public HashMap<Integer, Article> testArticles;

    public Data(String wordsFile, String trainDataFile, String trainLabelFile, String testDataFile,
            String testLabelFile) {
        try {
            parseWords(wordsFile);
            trainArticles = parseArticles(trainDataFile);
            parseCategories(trainArticles, trainLabelFile);
            testArticles = parseArticles(testDataFile);
            parseCategories(testArticles, testLabelFile);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Incorrect file format: " + e.getMessage());
        }
    }

    /**
     * Parse the words given in the file located at {@code path}. <br>
     * The file must be a list of words, one on each line, the line number being the
     * ID of the word, starting at 1.
     * 
     * @param path
     *            The path where to find the file to parse.
     * @throws FileNotFoundException
     *             If no file is found at {@code path}.
     */
    private void parseWords(String path) throws FileNotFoundException {
        words = new HashMap<>();
        File file = new File(path);
        Scanner input = new Scanner(file);
        int lineNumber = 1;
        while (input.hasNext()) {
            String word = input.nextLine();
            words.put(lineNumber, word);
            lineNumber++;
        }
        input.close();
    }

    /**
     * Parse the articles given in the file located at {@code path}. <br>
     * The file must be a succession of integers, making sense by pairs, a document
     * ID followed by a word ID. These integers may be on different lines.
     * 
     * @param path
     *            The path where to find the file to parse.
     * @throws FileNotFoundException
     *             If no file is found at {@code path}.
     */
    private HashMap<Integer, Article> parseArticles(String path) throws FileNotFoundException {
        HashMap<Integer, Article> articles = new HashMap<>();
        File file = new File(path);
        Scanner input = new Scanner(file);
        while (input.hasNext()) {
            int docId = input.nextInt();
            int wordId = input.nextInt();
            if (!articles.containsKey(docId)) {
                articles.put(docId, new Article(docId));
            }
            articles.get(docId).addWord(words.get(wordId));
        }
        input.close();
        return articles;
    }

    /**
     * Parse the categories given in the file located at {@code path}, and update the
     * category of the given articles. <br>
     * The file must be a list of category IDs, one on each line, the line number
     * being the ID of the document it describes, starting at 1.
     * 
     * @param articles
     *            The articles
     * @param path
     *            The path where to find the file to parse.
     * @throws FileNotFoundException
     *             If no file is found at {@code path}.
     */
    private static void parseCategories(HashMap<Integer, Article> articles, String path)
            throws FileNotFoundException {
        File file = new File(path);
        Scanner input = new Scanner(file);
        int lineNumber = 1;
        while (input.hasNext()) {
            int catId = input.nextInt();
            if (!articles.containsKey(lineNumber)) {
                articles.put(lineNumber, new Article(lineNumber));
            }
            articles.get(lineNumber).setCategory(catId);
            lineNumber++;
        }
        input.close();
    }

    /**
     * Returns a String representing all the data in this object.
     */
    @Override
    public String toString() {
        String res = "Words:\n";
        for (Integer key : words.keySet()) {
            res += key + " " + words.get(key) + "\n";
        }
        res += "\nTrain Articles:\n";
        for (Integer key : trainArticles.keySet()) {
            res += trainArticles.get(key) + "\n";
        }
        res += "\nTest Articles:\n";
        for (Integer key : testArticles.keySet()) {
            res += testArticles.get(key) + "\n";
        }
        return res;
    }
}
