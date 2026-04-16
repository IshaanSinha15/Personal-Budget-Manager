package com.budgetmanager.service;

/**
 * Concrete Strategy: Monthly Budget
 * Implements standard monthly budget calculation
 */
public class MonthlyBudgetStrategy implements BudgetStrategy {

    @Override
    public double calculateAllowedAmount(double baseAmount, Object context) {
        // Simple monthly budget - no adjustment
        return baseAmount;
    }

    @Override
    public boolean isExceeded(double spent, double allowed) {
        return spent > allowed;
    }

    @Override
    public String toString() {
        return "Monthly Budget Strategy";
    }
}
