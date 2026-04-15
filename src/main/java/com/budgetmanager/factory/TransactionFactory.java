package com.budgetmanager.factory;

import com.budgetmanager.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Factory Pattern implementation for creating Transaction objects
 * GRASP Creator principle - Factory creates transaction objects
 * Handles creation logic and maintains separation of concerns
 */
public class TransactionFactory {

    private TransactionFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Create an Income transaction
     * @param amount Income amount
     * @param category Income category
     * @param date Transaction date
     * @param userId User ID
     * @param description Optional description
     * @return Income transaction object
     */
    public static Income createIncome(BigDecimal amount, Category category, 
                                      LocalDateTime date, Long userId, String description) {
        validateAmount(amount);
        validateCategory(category, TransactionType.INCOME);
        
        return new Income(amount, category, date, userId, description);
    }

    /**
     * Create an Expense transaction
     * @param amount Expense amount
     * @param category Expense category
     * @param date Transaction date
     * @param userId User ID
     * @param description Optional description
     * @return Expense transaction object
     */
    public static Expense createExpense(BigDecimal amount, Category category, 
                                        LocalDateTime date, Long userId, String description) {
        validateAmount(amount);
        validateCategory(category, TransactionType.EXPENSE);
        
        return new Expense(amount, category, date, userId, description);
    }

    /**
     * Create transaction based on type (Prototype Pattern preparation)
     * @param type Transaction type
     * @param amount Amount
     * @param category Category
     * @param date Date
     * @param userId User ID
     * @param description Description
     * @return Transaction object
     */
    public static Transaction createTransaction(TransactionType type, BigDecimal amount, 
                                               Category category, LocalDateTime date, 
                                               Long userId, String description) {
        if (type == TransactionType.INCOME) {
            return createIncome(amount, category, date, userId, description);
        } else {
            return createExpense(amount, category, date, userId, description);
        }
    }

    /**
     * Validate transaction amount
     * @param amount Amount to validate
     * @throws IllegalArgumentException if amount is invalid
     */
    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    /**
     * Validate category matches transaction type
     * @param category Category to validate
     * @param type Transaction type
     * @throws IllegalArgumentException if category doesn't match type
     */
    private static void validateCategory(Category category, TransactionType type) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        // Optional: Validate category matches transaction type
        // This can be extended based on business rules
    }
}
