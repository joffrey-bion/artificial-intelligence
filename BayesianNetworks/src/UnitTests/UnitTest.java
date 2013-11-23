package UnitTests;

import java.util.ArrayList;
import java.util.LinkedList;

import math.Assignment;
import math.Factor;
import math.Variable;

public class UnitTest {

    public static void main(String args[]) {
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        Variable c = new Variable("C");
        Variable d = new Variable("D");
        Variable e = new Variable("E");
        Variable f = new Variable("F");
        Variable g = new Variable("G");

        ArrayList<Variable> list1 = new ArrayList<>();
        list1.add(a);
        list1.add(b);
        list1.add(c);
        list1.add(d);

        ArrayList<Variable> list2 = new ArrayList<>();
        list2.add(b);
        list2.add(e);
        list2.add(f);

        ArrayList<Variable> list3 = new ArrayList<>();
        list3.add(f);
        list3.add(g);

        System.out.println("list1 = " + list1);
        System.out.println("list2 = " + list2);
        System.out.println("list3 = " + list3);
        System.out.println();

        Assignment a1 = new Assignment(list1, true, false, false, true);
        System.out.println("a1 = [" + a1 + "]");
        a1.removeVariable(c);
        System.out.println("a1 = [" + a1 + "] (C removed)");

        Assignment a2 = new Assignment(list2, false, true, true);
        System.out.println("a2 = [" + a2 + "]");
        System.out.println("a1 U a2 = [" + Assignment.merge(a1, a2) + "]");
        System.out.println();

        Factor testBigFactor = testFactor4Vars(list1);
        printFactor("factor 1:", testBigFactor);

        Factor testFactor = testFactor3Vars(list2);
        printFactor("factor 2:", testFactor);
        printFactor("factor 2 summed over B:", Factor.sumout(testFactor, b));
        printFactor("factor 2 restricted to B=T:", Factor.restrict(testFactor, b, true));
        printFactor("factor 2 restricted to B=T (non static):", testFactor.restrict(b, true));
        printFactor("factor 2 normalized:", testFactor.normalize());

        Factor F = setTestValues2Vars1(new Factor(a, b));
        Factor G = setTestValues2Vars2(new Factor(b, c));
        printFactor("factor F:", F);
        printFactor("factor G:", G);
        printFactor("factor F x G:", Factor.multiply(F, G));

        Factor f1 = new Factor(a);
        f1.setValue(0.9, true);
        f1.setValue(0.1, false);
        printFactor("f1(A):", f1);
        printFactor("f1(A) restricted to A=a:", Factor.restrict(f1, a, true));
        printFactor("f1(A)|A=a * f1(A) :", Factor.multiply(f1, Factor.restrict(f1, a, true)));

        Factor f2 = new Factor(a, b);
        f2.setValue(0.9, true, true);
        f2.setValue(0.1, true, false);
        f2.setValue(0.4, false, true);
        f2.setValue(0.6, false, false);
        printFactor("f2(A,B):", f2);

        Factor f3 = new Factor(b, c);
        f3.setValue(0.7, true, true);
        f3.setValue(0.3, false, true);
        f3.setValue(0.2, true, false);
        f3.setValue(0.8, false, false);
        printFactor("f3(B,C):", f3);

        LinkedList<Factor> factors = new LinkedList<>();
        LinkedList<Variable> orderedVariables = new LinkedList<>();
        LinkedList<Variable> queryVariables = new LinkedList<>();
        LinkedList<Variable> evidence = new LinkedList<>();
        factors.add(f1);
        factors.add(f2);
        factors.add(f3);
        orderedVariables.add(a);
        orderedVariables.add(b);
        queryVariables.add(c);
        Factor res = Factor.inference(factors, queryVariables, orderedVariables, evidence, true);
        printFactor("\nresult:", res);
    }

    private static void printFactor(String text, Factor f) {
        System.out.println(text + "\n" + f.toFullString() + "\n");
    }

    private static Factor setTestValues2Vars1(Factor f) {
        f.setValue(0.9, true, true);
        f.setValue(0.1, true, false);
        f.setValue(0.4, false, true);
        f.setValue(0.6, false, false);
        return f;
    }

    private static Factor setTestValues2Vars2(Factor f) {
        f.setValue(0.7, true, true);
        f.setValue(0.3, true, false);
        f.setValue(0.8, false, true);
        f.setValue(0.2, false, false);
        return f;
    }

    private static Factor testFactor3Vars(ArrayList<Variable> vars) {
        Factor f = new Factor(vars);
        f.setValue(0.7, false, false, false);
        f.setValue(0.14, true, false, false);
        f.setValue(0.9, false, true, false);
        f.setValue(0.2, true, true, false);
        f.setValue(1.35, false, false, true);
        f.setValue(0.7, true, false, true);
        f.setValue(0.61, false, true, true);
        f.setValue(1.48, true, true, true);
        return f;
    }

    private static Factor testFactor4Vars(ArrayList<Variable> vars) {
        Factor f = new Factor(vars);
        f.setValue(0.7, false, false, false, false);
        f.setValue(0.1, true, false, false, false);
        f.setValue(0.9, false, true, false, false);
        f.setValue(0.2, true, true, false, false);
        f.setValue(0.35, false, false, true, false);
        f.setValue(1.7, true, false, true, false);
        f.setValue(0.61, false, true, true, false);
        f.setValue(0.48, true, true, true, false);
        f.setValue(1.15, false, false, false, true);
        f.setValue(0.2, true, false, false, true);
        f.setValue(0.4, false, true, false, true);
        f.setValue(1.12, true, true, false, true);
        f.setValue(0.35, false, false, true, true);
        f.setValue(0.42, true, false, true, true);
        f.setValue(0.0, false, true, true, true);
        f.setValue(1.2, true, true, true, true);
        return f;
    }

}
