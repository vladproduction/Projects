package com.vladproduction.bankTransactions.caseD_usingInterface;

public class MainApp {
    public static void main(String[] args) {
        BaseBankAnalyzer bankAnalyzer = new BaseBankAnalyzer();
        bankAnalyzer.analyze();
    }
}
