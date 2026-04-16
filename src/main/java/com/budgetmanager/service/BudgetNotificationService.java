package com.budgetmanager.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * GRASP Indirection: Separate notification system
 * Handles all budget notifications and manages observers
 * DIP: Depends on BudgetNotificationListener interface
 */
@Service
public class BudgetNotificationService {
    
    private final List<BudgetNotificationListener> listeners = new ArrayList<>();

    public void subscribe(BudgetNotificationListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(BudgetNotificationListener listener) {
        listeners.remove(listener);
    }

    public void notifyBudgetExceeded(String userId, double exceeded) {
        String message = String.format("Budget exceeded by: $%.2f", exceeded);
        listeners.forEach(listener -> listener.onBudgetExceeded(userId, message));
    }

    public void notifyBudgetWarning(String userId, double percentageUsed) {
        String message = String.format("Budget usage at %.1f%%", percentageUsed);
        listeners.forEach(listener -> listener.onBudgetWarning(userId, message));
    }
}
