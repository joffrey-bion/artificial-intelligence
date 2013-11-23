package algorithms;

import java.util.LinkedList;

import math.Factor;
import math.Variable;

public class Main {

    private static Variable Trav = new Variable("Trav");
    private static Variable Fraud = new Variable("Fraud");
    private static Variable FP = new Variable("FP");
    private static Variable OC = new Variable("OC");
    private static Variable IP = new Variable("IP");
    private static Variable CRP = new Variable("CRP");

    private static LinkedList<Factor> factors;
    private static LinkedList<Variable> orderedVariables;
    private static LinkedList<Variable> queryVariables = new LinkedList<>();
    private static LinkedList<Variable> evidence = new LinkedList<>();

    private static void printFactor(String text, Factor f) {
        System.out.println(text + "\n" + f.toFullString() + "\n");
    }

    private static void answer(String text) {
        System.out.println("======= " + text + " =======");
    }

    public static Factor query() {
        return query(true);
    }

    public static Factor query(boolean normalize) {
        LinkedList<Factor> factorsCopy = new LinkedList<>();
        for (Factor f : factors) {
            factorsCopy.add(new Factor(f));
        }
        return Factor.inference(factorsCopy, queryVariables, orderedVariables, evidence, normalize);
    }

    public static void main(String args[]) {

        // the factors and variable elimination order will not change
        setOrderedVariables(Trav, FP, Fraud, IP, OC, CRP);
        generateCreditCardProblemFactors();
        // other initializations
        queryVariables = new LinkedList<>();
        evidence = new LinkedList<>();
        Factor result;

        answer("2.b. Prior proba");
        initInferenceListsAndVars();
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud):", result);

        answer("2.b. Proba with evidence");
        initInferenceListsAndVars();
        FP.set(true);
        IP.set(false);
        CRP.set(true);
        evidence.add(FP);
        evidence.add(IP);
        evidence.add(CRP);
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud | fp, ~ip, crp):", result);

        answer("2.c. Proba with more evidence");
        initInferenceListsAndVars();
        FP.set(true);
        IP.set(false);
        CRP.set(true);
        Trav.set(true);
        evidence.add(FP);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(Trav);
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud | fp, ~ip, crp, trav):", result);

        answer("2.d. Comparison");

        initInferenceListsAndVars();
        IP.set(true);
        CRP.set(false);
        FP.set(false);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud | ip, ~crp, ~fp):", result);

        initInferenceListsAndVars();
        IP.set(true);
        CRP.set(true);
        FP.set(false);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud | ip, crp, ~fp):", result);

        initInferenceListsAndVars();
        IP.set(true);
        CRP.set(false);
        FP.set(true);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud | ip, ~crp, fp):", result);

        initInferenceListsAndVars();
        IP.set(true);
        CRP.set(true);
        FP.set(true);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud | ip, crp, fp):", result);

        initInferenceListsAndVars();
        IP.set(true);
        CRP.set(true);
        FP.set(true);
        Trav.set(true);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        evidence.add(Trav);
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud | ip, crp, fp, trav):", result);

        initInferenceListsAndVars();
        IP.set(false);
        CRP.set(false);
        FP.set(false);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud | ~ip, ~crp, ~fp):", result);

        initInferenceListsAndVars();
        IP.set(false);
        CRP.set(true);
        FP.set(false);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        queryVariables.add(Fraud);
        result = query();
        printFactor("\nP(Fraud | ~ip, crp, ~fp):", result);

        answer("3.b");
        setOrderedVariables(Trav, FP, Fraud, IP, OC, CRP);
        generateCreditCardProblemFactors();
        Variable Block = new Variable("Block");
        Factor U = new Factor(Fraud, Block);
        factors.add(U);
        U.setValue(0, true, true);
        U.setValue(-1000, true, false);
        U.setValue(-10, false, true);
        U.setValue(5, false, false);

        initInferenceListsAndVars();
        queryVariables.add(Block);
        result = query(false);
        printFactor("\nEU(Block):", result);

        initInferenceListsAndVars();
        IP.set(false);
        CRP.set(true);
        FP.set(true);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        queryVariables.add(Block);
        result = query(false);
        printFactor("\nEU(Block | ~ip, crp, fp):", result);

        initInferenceListsAndVars();
        IP.set(false);
        CRP.set(true);
        FP.set(true);
        Trav.set(true);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        evidence.add(Trav);
        queryVariables.add(Block);
        result = query(false);
        printFactor("\nEU(Block | ~ip, crp, fp, trav):", result);

        initInferenceListsAndVars();
        IP.set(false);
        CRP.set(true);
        FP.set(true);
        Trav.set(false);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        evidence.add(Trav);
        queryVariables.add(Block);
        result = query(false);
        printFactor("\nEU(Block | ~ip, crp, fp, ~trav):", result);

        answer("3.c");

        Variable Call = new Variable("Call");
        Factor newU = new Factor(Fraud, Trav, Call);
        factors.remove(U);
        factors.add(newU);
        newU.setValue(0, true, false, true);
        newU.setValue(-1000, true, true, true);
        newU.setValue(-10, false, false, true);
        newU.setValue(5, false, true, true);
        newU.setValue(0, true, false, false);
        newU.setValue(0, true, true, false);
        newU.setValue(-10, false, false, false);
        newU.setValue(-10, false, true, false);
        initInferenceListsAndVars();
        IP.set(false);
        CRP.set(true);
        FP.set(true);
        evidence.add(IP);
        evidence.add(CRP);
        evidence.add(FP);
        queryVariables.add(Call);
        result = query(false);
        printFactor("\nEU(Call | ~ip, crp, fp):", result);
        System.out.println("EVI = " + (result.getValue(true) - result.getValue(false)));
    }

    private static void initInferenceListsAndVars() {
        Trav.reset();
        Fraud.reset();
        FP.reset();
        OC.reset();
        IP.reset();
        CRP.reset();
        queryVariables.clear();
        evidence.clear();
        System.out.println("***** Inference initialized *****\n");
    }

    private static void setOrderedVariables(Variable... vars) {
        orderedVariables = new LinkedList<>();
        for (Variable var : vars) {
            orderedVariables.add(var);
        }
    }

    private static void generateCreditCardProblemFactors() {
        Factor factorTrav = new Factor(Trav);
        factorTrav.setValue(0.05, true);
        factorTrav.setValue(0.95, false);
        printFactor("P(Trav):", factorTrav);

        Factor factorFraud = new Factor(Fraud, Trav);
        factorFraud.setValue(0.01, true, true);
        factorFraud.setValue(0.99, false, true);
        factorFraud.setValue(0.004, true, false);
        factorFraud.setValue(0.996, false, false);
        printFactor("P(Fraud | Trav):", factorFraud);

        Factor factorFP = new Factor(FP, Fraud, Trav);
        factorFP.setValue(0.9, true, true, true);
        factorFP.setValue(0.1, false, true, true);
        factorFP.setValue(0.9, true, false, true);
        factorFP.setValue(0.1, false, false, true);
        factorFP.setValue(0.1, true, true, false);
        factorFP.setValue(0.9, false, true, false);
        factorFP.setValue(0.01, true, false, false);
        factorFP.setValue(0.99, false, false, false);
        printFactor("P(FP | Fraud, Trav):", factorFP);

        Factor factorOC = new Factor(OC);
        factorOC.setValue(0.65, true);
        factorOC.setValue(0.35, false);
        printFactor("P(OC):", factorOC);

        Factor factorIP = new Factor(IP, Fraud, OC);
        factorIP.setValue(0.02, true, true, true);
        factorIP.setValue(0.98, false, true, true);
        factorIP.setValue(0.01, true, false, true);
        factorIP.setValue(0.99, false, false, true);
        factorIP.setValue(0.011, true, true, false);
        factorIP.setValue(0.989, false, true, false);
        factorIP.setValue(0.001, true, false, false);
        factorIP.setValue(0.999, false, false, false);
        printFactor("P(IP | Fraud, OC):", factorOC);

        Factor factorCRP = new Factor(CRP, OC);
        factorCRP.setValue(0.1, true, true);
        factorCRP.setValue(0.9, false, true);
        factorCRP.setValue(0.001, true, false);
        factorCRP.setValue(0.999, false, false);
        printFactor("P(CRP | OC):", factorOC);

        factors.add(factorFraud);
        factors.add(factorTrav);
        factors.add(factorOC);
        factors.add(factorIP);
        factors.add(factorFP);
        factors.add(factorCRP);
    }
}
