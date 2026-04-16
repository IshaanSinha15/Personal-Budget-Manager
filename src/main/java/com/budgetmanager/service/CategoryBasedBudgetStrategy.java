package com.budgetmanager.service;

/**
 * Concrete Strategy: Category-Based Budget
 * Implements budget calculations per category
 */
public class CategoryBasedBudgetStrategy implements BudgetStrategy {

    private String categoryName;

    public CategoryBasedBudgetStrategy(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public double calculateAllowedAmount(double baseAmount, Object context) {
        // For category-based budgets, calculate proportional amount
        // Context could contain category weights
        return baseAmount;
    }

    @Override
    public boolean isExceeded(double spent, double allowed) {
        return spent > allowed;
    }

    @Override
    public String toString() {
        return "Category-Based Budget Strategy (" + categoryName + ")";
    }
}
