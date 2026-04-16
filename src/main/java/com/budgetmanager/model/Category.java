package com.budgetmanager.model;

/**
 * Enumeration for transaction categories
 */
public enum Category {
    // Income categories
    SALARY("Salary"),
    FREELANCE("Freelance"),
    INVESTMENT("Investment"),
    GIFT("Gift"),
    BONUS("Bonus"),
    OTHER_INCOME("Other Income"),

    // Expense categories
    GROCERIES("Groceries"),
    UTILITIES("Utilities"),
    RENT("Rent"),
    TRANSPORTATION("Transportation"),
    ENTERTAINMENT("Entertainment"),
    DINING("Dining"),
    HEALTHCARE("Healthcare"),
    SHOPPING("Shopping"),
    EDUCATION("Education"),
    INSURANCE("Insurance"),
    OTHER_EXPENSE("Other Expense");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
