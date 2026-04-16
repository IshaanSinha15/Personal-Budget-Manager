package com.budgetmanager.service;

/**
 * Strategy Pattern Interface
 * Defines contract for different budget calculation strategies
 * DIP: Depend on this interface, not concrete implementations
 */
public interface BudgetStrategy {
    double calculateAllowedAmount(double baseAmount, Object context);
    boolean isExceeded(double spent, double allowed);
}
