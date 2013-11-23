package math;

import java.util.ArrayList;
import java.util.LinkedList;

public class Assignment {

    private ArrayList<Variable> vars;
    private boolean values[];

    public Assignment(Assignment a) {
        this(a.vars, a.values);
    }

    public Assignment(ArrayList<Variable> vars, boolean... values) {
        if (vars.size() != values.length) {
            throw new RuntimeException("number of variables and values must be equal");
        }
        this.vars = new ArrayList<>();
        this.vars.addAll(vars);
        this.values = values;
    }

    public Assignment(ArrayList<Variable> vars, int index) {
        if (index > (1 << vars.size())) {
            throw new RuntimeException("index out of bounds for this number of variables");
        }
        this.vars = new ArrayList<>();
        this.vars.addAll(vars);
        values = new boolean[vars.size()];
        if (vars.size() == 0) {
            return; // empty assignment for a constant factor in this case
        }
        String binary = Integer.toBinaryString(index);
        for (int i = 0; i < binary.length(); i++) {
            values[i] = binary.charAt(binary.length() - i - 1) == '1';
        }
        for (int i = binary.length(); i < values.length; i++) {
            values[i] = false;
        }
    }

    private void checkVariablePresence(Variable v) {
        if (!vars.contains(v)) {
            throw new RuntimeException("the variable " + v + " is not in this assignment");
        }
    }

    public ArrayList<Variable> getVariables() {
        return vars;
    }

    public boolean getValue(Variable v) {
        checkVariablePresence(v);
        return values[vars.indexOf(v)];
    }

    /**
     * Converts this assignment into a decimal index for a factor's table.
     * 
     * @return a decimal index corresponding to this assignment
     */
    public int toIndex() {
        return toIndex(values);
    }

    public void removeVariable(Variable v) {
        checkVariablePresence(v);
        boolean[] newValues = new boolean[vars.size() - 1];
        int varIndex = vars.indexOf(v);
        for (int i = 0; i < newValues.length; i++) {
            if (i < varIndex) {
                newValues[i] = values[i];
            } else {
                newValues[i] = values[i + 1];
            }
        }
        values = newValues;
        vars.remove(v);
    }

    /**
     * Converts a boolean table (binary representation) into a decimal index.
     * 
     * @param assignment
     *            The assignment to convert into an index
     * @return the index corresponding to the specified statement
     */
    public static int toIndex(boolean assignment[]) {
        int index = 0;
        for (int i = 0; i < assignment.length; i++) {
            if (assignment[i]) {
                index += (1 << i);
            }
        }
        return index;
    }

    /**
     * Merge the two assignments into one, without repetition of the variables. Does
     * not check whether the assignments are consistent together or not (i.e. same
     * value for the same variable).
     * 
     * @param a1
     *            The source assignment 1
     * @param a2
     *            The source assignment 2
     * @return The resulting merged assignment
     */
    public static Assignment merge(Assignment a1, Assignment a2) {
        ArrayList<Variable> mergedVars = new ArrayList<>();
        mergedVars.addAll(a1.vars);
        for (Variable v : a2.vars) {
            if (!a1.vars.contains(v)) {
                mergedVars.add(v);
            }
        }
        boolean values[] = new boolean[mergedVars.size()];
        int i = 0;
        for (Variable v : mergedVars) {
            if (a1.vars.contains(v)) {
                values[i] = a1.getValue(v);
            } else {
                values[i] = a2.getValue(v);
            }
            i++;
        }
        return new Assignment(mergedVars, values);
    }

    /**
     * Returns a list of all possible assignments for a given list of variables.
     * 
     * @param vars
     *            The list of variables to get an assignment for
     * @return The list of all possible assignments for the specified variables
     */
    public static LinkedList<Assignment> assignments(ArrayList<Variable> vars) {
        LinkedList<Assignment> set = new LinkedList<>();
        for (int i = 0; i < (1 << vars.size()); i++) {
            set.add(new Assignment(vars, i));
        }
        return set;
    }

    /*
     * Methods for fancy printing
     */

    public String toString(boolean align) {
        String res = "";
        for (int i = 0; i < values.length; i++) {
            if (align && values[i]) {
                res += " ";
            }
            res += vars.get(i).toStringWithValue(values[i]);
            if (i < values.length - 1) {
                res += ",";
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return toString(false);
    }
}
