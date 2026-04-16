package com.budgetmanager.service;

/**
 * Observer Pattern Interface
 * Allows decoupled notification system for budget alerts
 */
public interface BudgetNotificationListener {
    void onBudgetExceeded(String userId, String message);
    void onBudgetWarning(String userId, String message);
}
