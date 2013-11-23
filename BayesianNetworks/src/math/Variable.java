package math;

public class Variable {
    private String name;
    private boolean set;
    private boolean value;

    public Variable(String name) {
        this.name = name;
        this.set = false;
        this.value = false;
    }

    public boolean isSet() {
        return set;
    }

    public String getName() {
        return name;
    }

    public boolean getValue() {
        if (!set) {
            throw new RuntimeException("accessing value of unset variable");
        }
        return value;
    }

    public void set(boolean value) {
        this.value = value;
        this.set = true;
    }

    public void reset() {
        this.set = false;
    }

    public boolean equals(Variable v) {
        return name.equals(v.name);
    }

    /*
     * Methods for fancy printing
     */

    public String toStringWithValue(boolean givenValue) {
        String res = "";
        if (!givenValue) {
            res += "~";
        }
        res += name.toLowerCase();
        return res;
    }

    public String toStringWithValue() {
        if (!set) {
            return name + "?";
        } else {
            return toStringWithValue(value);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
