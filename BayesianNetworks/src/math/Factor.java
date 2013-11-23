package math;

import java.util.ArrayList;
import java.util.LinkedList;

public class Factor {

    private ArrayList<Variable> vars; // variables
    private Double[] values;

    public Factor(Variable... variables) {
        if (variables.length == 0) {
            throw new RuntimeException("the new factor must have variables");
        }
        this.vars = new ArrayList<>();
        for (Variable v : variables) {
            this.vars.add(v);
        }
        initValues();
    }

    public Factor(ArrayList<Variable> variables) {
        if (variables.size() == 0) {
            throw new RuntimeException("the new factor must have variables");
        }
        this.vars = new ArrayList<>();
        this.vars.addAll(variables);
        initValues();
    }

    /**
     * Create a new factor that is a copy of f.
     * 
     * @param f
     *            The factor to copy
     */
    public Factor(Factor f) {
        this.vars = new ArrayList<>();
        this.vars.addAll(f.vars);
        this.values = new Double[f.values.length];
        for (int i = 0; i < f.values.length; i++) {
            this.values[i] = f.values[i];
        }
    }

    /**
     * Initialize {@link Double#NaN} values.
     */
    private void initValues() {
        values = new Double[1 << vars.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = Double.NaN;
        }
    }

    /**
     * Raises an exception if the variable is not one of this factor.
     */
    private void checkVariablePresence(Variable v) {
        if (!vars.contains(v)) {
            throw new IllegalArgumentException("This factor does not contain the variable " + v);
        }
    }

    /**
     * Raises an exception if the variables in the assignment are not the same as in
     * this factor.
     */
    private void checkAssignmentVariables(Assignment a) {
        ArrayList<Variable> assignmentVars = a.getVariables();
        if (!assignmentVars.containsAll(vars)) {
            throw new IllegalArgumentException("Variables are missing in the assignment");
        }
        for (Variable v : assignmentVars) {
            checkVariablePresence(v);
        }
    }

    public void setValue(double value, boolean... assignment) {
        values[Assignment.toIndex(assignment)] = value;
    }

    public void setValue(double value, Assignment assignment) {
        checkAssignmentVariables(assignment);
        values[assignment.toIndex()] = value;
    }

    public double getValue(boolean... assignment) {
        return values[Assignment.toIndex(assignment)];
    }

    public double getValue(Assignment assignment) {
        return values[assignment.toIndex()];
    }

    public boolean contains(Variable v) {
        return vars.contains(v);
    }

    /**
     * Restrict the variable v to value in this factor.
     * 
     * @param v
     *            The variable to restrict in this factor
     * @param value
     *            The value for the restriction
     * @return This factor, which has been restricted.
     */
    public Factor restrict(Variable v, boolean value) {
        checkVariablePresence(v);
        Double newValues[] = new Double[1 << (vars.size() - 1)];
        // copy the values for the restricted factor
        for (Assignment a : Assignment.assignments(vars)) {
            // skip the assignments which are inconsistent with the given value for v
            if (a.getValue(v) != value) {
                continue;
            }
            // create the corresponding assignment without v
            Assignment shorterAssignment = new Assignment(a);
            shorterAssignment.removeVariable(v);
            // copy the probability for this assignment in the new factor
            newValues[shorterAssignment.toIndex()] = getValue(a);
        }
        vars.remove(v);
        values = newValues;
        return this;
    }

    /**
     * Sum out the variable v in this factor.
     * 
     * @param v
     *            The variable to sum out
     * @return This factor, where the variable v has been summed out (and therefore
     *         removed).
     */
    public Factor sumout(Variable v) {
        checkVariablePresence(v);
        Double newValues[] = new Double[1 << (vars.size() - 1)];
        for (int i = 0; i < newValues.length; i++) {
            newValues[i] = Double.NaN;
        }
        // fill the values of the new factor
        for (Assignment a : Assignment.assignments(vars)) {
            // create the corresponding assignment without v
            Assignment shorterAssignment = new Assignment(a);
            shorterAssignment.removeVariable(v);
            // add the probability for this assignment in the new factor
            int index = shorterAssignment.toIndex();
            Double value = newValues[index];
            if (value.equals(Double.NaN)) {
                newValues[index] = getValue(a);
            } else {
                newValues[index] = getValue(a) + value;
            }
        }
        vars.remove(v);
        values = newValues;
        return this;
    }

    /**
     * Normalizes this factor.
     * 
     * @return This factor, which have been normalized.
     */
    public Factor normalize() {
        float sum = 0;
        for (Double value : values) {
            sum += value;
        }
        for (int i = 0; i < values.length; i++) {
            values[i] /= sum;
        }
        return this;
    }

    /**
     * The static version of restrict(), which does not modify the factor f.
     * 
     * @param f
     *            The factor to restrict a copy of
     * @param var
     *            The variable to fix the value of
     * @param value
     *            The value of
     * 
     * @return A new factor, the restricted version of f.
     */
    public static Factor restrict(Factor f, Variable var, boolean value) {
        return new Factor(f).restrict(var, value);
    }

    /**
     * The static version of {@link #sumout(Variable)}, which does not modify the
     * factor f.
     * 
     * @param f
     *            The factor to sumout a copy of
     * @param var
     *            The variable to sum out
     * 
     * @return A new factor, the summed out version of f.
     */
    public static Factor sumout(Factor f, Variable var) {
        return new Factor(f).sumout(var);
    }

    /**
     * The static version of normalize(), which does not modify the factor f.
     * 
     * @param f
     *            The factor to copy and normalize
     * 
     * @return A new factor, the normalized version of f.
     */
    public static Factor normalize(Factor f) {
        return new Factor(f).normalize();
    }

    /**
     * Multiplies the factors f1 and f2. Does not modify f1 nor f2.
     * 
     * @param f1
     *            The first term of the product
     * @param f2
     *            The second term of the product
     * @return The product factor of f1 and f2.
     */
    public static Factor multiply(Factor f1, Factor f2) {
        // create a new factor with the variables of f1 and f2
        ArrayList<Variable> commonVars = new ArrayList<>();
        ArrayList<Variable> mergedVars = new ArrayList<>();
        mergedVars.addAll(f1.vars);
        for (Variable v : f2.vars) {
            if (f1.vars.contains(v)) {
                commonVars.add(v);
            } else {
                mergedVars.add(v);
            }
        }
        Factor productFactor = new Factor(mergedVars);
        // fill the values of the new factor
        for (Assignment a1 : Assignment.assignments(f1.vars)) {
            f2Loop: for (Assignment a2 : Assignment.assignments(f2.vars)) {
                // skip the assignments which are inconsistent
                // (different values for the same variable)
                for (Variable v : commonVars) {
                    if (a1.getValue(v) != a2.getValue(v)) {
                        continue f2Loop;
                    }
                }
                // create the merged assignment
                Assignment a = Assignment.merge(a1, a2);
                // copy the probability for this assignment in the new factor
                productFactor.setValue(f1.getValue(a1) * f2.getValue(a2), a);
            }
        }
        return productFactor;
    }

    /**
     * Computes the product of all the factors. Does not modify them, neither the
     * list.
     * 
     * @param factors
     *            The terms of the product
     * @return The product of the factors.
     */
    public static Factor multiply(LinkedList<Factor> factors) {
        if (factors == null || factors.size() == 0) {
            throw new IllegalArgumentException("Cannot compute the product of an empty list!");
        }
        LinkedList<Factor> factorsCopy = new LinkedList<>();
        factorsCopy.addAll(factors);
        while (factorsCopy.size() > 1) {
            Factor f1 = factorsCopy.removeFirst();
            Factor f2 = factorsCopy.removeFirst();
            factorsCopy.add(multiply(f1, f2));
        }
        return factorsCopy.getFirst();
    }

    /**
     * Executes the variable elimination algorithm.
     * 
     * @param factors
     *            The list of all factors to consider
     * @param queryVariables
     *            The variables we want to see in the resulting factor
     * @param orderedHiddenVariables
     *            A list of the other variables, in the order of their elimination.
     *            This list may contain some variables contained in queryVariables
     *            and evidence, but these variables will be ignored.
     * @param evidence
     *            A list of variables which have been set to a value as evidence.
     * @param normalize
     *            If {@code true}, the result is normalized.
     * @return The resulting normalized factor computed by the variable elimination
     *         algorithm.
     */
    public static Factor inference(LinkedList<Factor> factors, LinkedList<Variable> queryVariables,
            LinkedList<Variable> orderedHiddenVariables, LinkedList<Variable> evidence,
            boolean normalize) {
        // restrict all factors according to the evidence list
        System.out.println("Restrictions:");
        for (Variable v : evidence) {
            for (Factor f : factors) {
                if (f.contains(v)) {
                    System.out.print(f + "|" + v + "=" + v.toStringWithValue() + " -> ");
                    f.restrict(v, v.getValue());
                    System.out.println(f);
                }
            }
        }
        // sum out the variables in the order given by hiddenVariables
        LinkedList<Factor> affectedFactors = new LinkedList<>();
        System.out.println("\nVariables summation:");
        for (Variable v : orderedHiddenVariables) {
            // skip query variables and evidence variables
            if (queryVariables.contains(v) || evidence.contains(v)) {
                continue;
            }
            // find the factors containing the variable v
            affectedFactors.clear();
            for (Factor f : factors) {
                if (f.contains(v)) {
                    affectedFactors.add(f);
                }
            }
            if (affectedFactors.isEmpty()) {
                continue;
            }
            factors.removeAll(affectedFactors);
            // compute the product of all the factors containing v
            Factor product = multiply(affectedFactors);
            // sum out the variable v in the product
            product = sumout(product, v);
            printNewComputedFactor(product, v, affectedFactors);
            factors.add(product);
        }
        // compute the product of all remaining factors
        Factor resultingFactor = multiply(factors);
        // normalization
        if (normalize) {
            resultingFactor.normalize();
        }
        return resultingFactor;
    }

    /**
     * Debug method which displays a list of factors with a title.
     * 
     * @param title
     *            A String printed before the list.
     * @param factors
     *            The list to display.
     * @param full
     *            Whether the full table of values should be displayed or just the
     *            variables.
     */
    public static void printFactorsList(String title, LinkedList<Factor> factors, boolean full) {
        System.out.println(title);
        int i = 1;
        for (Factor f : factors) {
            if (full) {
                System.out.println("member " + i + ":\n" + f.toFullString() + "\n");
            } else {
                System.out.println(f);
            }
            i++;
        }
        System.out.println();
    }

    /**
     * Fancy printing for a summation of a product of factors.
     */
    private static void printNewComputedFactor(Factor newFactor, Variable summedOutVar,
            LinkedList<Factor> termsOfProduct) {
        System.out.print(newFactor + " = Sum[" + summedOutVar + "] ");
        for (Factor f : termsOfProduct) {
            System.out.print(f);
        }
        System.out.println();
    }

    /**
     * Returns a string representing this factor with all its values.
     * 
     * @return a string representing this factor with all its values.
     */
    public String toFullString() {
        String res = "";
        for (int i = 0; i < values.length; i++) {
            res += "f(" + new Assignment(vars, i).toString(true) + ") = " + values[i];
            if (i < values.length - 1) {
                res += "\n";
            }
        }
        return res;
    }

    /**
     * Returns a string representing this factor, function of its variables.
     */
    @Override
    public String toString() {
        if (vars.isEmpty()) {
            return "f(-)";
        }
        String res = "f(";
        for (Variable v : vars) {
            res += v + ",";
        }
        return res.substring(0, res.length() - 1) + ")";
    }
}
