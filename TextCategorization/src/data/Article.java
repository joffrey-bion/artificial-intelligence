package data;

import java.util.HashSet;

public class Article {

    private int id;
    private int category;
    private HashSet<String> words;

    public Article(int id) {
        this.id = id;
        this.words = new HashSet<>();
        this.category = 0;
    }

    public void addWord(String word) {
        words.add(word);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public void setCategory(int catId) {
        category = catId;
    }

    public int getCategory() {
        if (category == 0) {
            throw new RuntimeException("The category of article " + id + " has not been set");
        }
        return category;
    }

    @Override
    public String toString() {
        return "art." + id + " (" + Category.getName(category) + ")";
    }
}
