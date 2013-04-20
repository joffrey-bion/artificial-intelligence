package data;

import java.util.Random;

public class Category {
    
    private static Random gen = new Random();

    /**
     * Return the name of the category given its ID.
     * 
     * @param categoryId
     *            The ID of the category
     * @return The name of the category.
     */
    public static String getName(int categoryId) {
        if (categoryId == 1) {
            return "alt.atheism";
        } else if (categoryId == 2) {
            return "comp.graphics";
        } else {
            return "unknown";
        }
    }

    /**
     * Returns a random category id.
     */
    public static int random() {
        return gen.nextInt(2) + 1;
    }
}
