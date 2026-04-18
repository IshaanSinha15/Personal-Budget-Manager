package com.budgetmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.budgetmanager.model.Budget;
import com.budgetmanager.model.BudgetCategory;
import com.budgetmanager.model.TransactionType;
import com.budgetmanager.repository.BudgetCategoryRepository;
import com.budgetmanager.repository.BudgetRepository;

/**
 * BudgetService
 * SRP: Only handles budget logic
 * DIP: Depends on BudgetStrategy and BudgetNotificationListener interfaces
 * GRASP High Cohesion: All budget business logic in one place
 */
@Service
@Transactional
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;

    @Autowired
    private BudgetNotificationService notificationService;

    private BudgetStrategy strategy;

    /**
     * Set budget with strategy pattern
     * DIP: Accept BudgetStrategy interface
     */
    public void setStrategy(BudgetStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Create or update budget for a user
     * Responsibilities: Initialize budget, validate, and persist
     */
    public Budget setBudget(Long userId, Double amount, String period) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Budget amount must be greater than 0");
        }

        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndPeriod(userId, period);
        
        Budget budget = existingBudget.orElseGet(() -> new Budget(userId, amount, period));
        budget.setTotalBudget(amount);
        
        return budgetRepository.save(budget);
    }

    /**
     * Check if budget is exceeded and notify
     * Responsibilities: Validation, checking, and triggering notifications
     */
    public void checkBudget(Long budgetId) {
        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        if (budgetOpt.isEmpty()) {
            throw new IllegalArgumentException("Budget not found");
        }

        Budget budget = budgetOpt.get();
        
        if (strategy != null) {
            double allowed = strategy.calculateAllowedAmount(budget.getTotalBudget(), null);
            if (strategy.isExceeded(budget.getTotalSpent(), allowed)) {
                double exceeded = budget.getTotalSpent() - allowed;
                notificationService.notifyBudgetExceeded(budget.getUserId().toString(), exceeded);
            }
        } else {
            // Default behavior
            if (budget.isBudgetExceeded()) {
                double exceeded = budget.getTotalSpent() - budget.getTotalBudget();
                notificationService.notifyBudgetExceeded(budget.getUserId().toString(), exceeded);
            }
        }

        // Notify warning at 80% usage
        if (budget.getPercentageUsed() >= 80) {
            notificationService.notifyBudgetWarning(budget.getUserId().toString(), budget.getPercentageUsed());
        }
    }

    /**
     * Get remaining budget for user
     */
    public double getRemainingBudget(Long budgetId) {
        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        if (budgetOpt.isEmpty()) {
            throw new IllegalArgumentException("Budget not found");
        }
        return budgetOpt.get().getRemainingBudget();
    }

    /**
     * Get budget by user and period
     */
    public Optional<Budget> getBudget(Long userId, String period) {
        return budgetRepository.findByUserIdAndPeriod(userId, period);
    }

    /**
     * Get all budgets for user
     */
    public List<Budget> getUserBudgets(Long userId) {
        return budgetRepository.findAllByUserId(userId);
    }

    /**
     * Get budget by id
     */
    public Optional<Budget> getBudgetById(Long budgetId) {
        return budgetRepository.findById(budgetId);
    }

    /**
     * Record spending against budget
     * Composite Pattern: Updates both parent and category
     */
    public void recordSpending(Long budgetId, Double amount, String categoryName) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Spending amount must be greater than 0");
        }

        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        if (budgetOpt.isEmpty()) {
            throw new IllegalArgumentException("Budget not found");
        }

        Budget budget = budgetOpt.get();
        budget.setTotalSpent(budget.getTotalSpent() + amount);

        // Update category if exists
        if (categoryName != null && !categoryName.isEmpty()) {
            BudgetCategory category = budget.getCategories().stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .orElse(null);

            if (category != null) {
                category.setSpent(category.getSpent() + amount);
                budgetCategoryRepository.save(category);
            }
        }

        budgetRepository.save(budget);
        checkBudget(budgetId);
    }

    /**
     * Add category to budget
     * Composite Pattern: Build budget hierarchy
     */
    public BudgetCategory addCategory(Long budgetId, String categoryName, Double limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Category limit must be greater than 0");
        }

        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        if (budgetOpt.isEmpty()) {
            throw new IllegalArgumentException("Budget not found");
        }

        Budget budget = budgetOpt.get();
        BudgetCategory category = new BudgetCategory(categoryName, limit);
        budget.addCategory(category);
        budgetRepository.save(budget);

        return category;
    }

    /**
     * Get category by id
     */
    public Optional<BudgetCategory> getCategoryById(Long categoryId) {
        return budgetCategoryRepository.findById(categoryId);
    }

    /**
     * Get all categories for budget
     */
    public List<BudgetCategory> getBudgetCategories(Long budgetId) {
        return budgetCategoryRepository.findByBudgetId(budgetId);
    }

    /**
     * Delete budget
     */
    public void deleteBudget(Long budgetId) {
        budgetRepository.deleteById(budgetId);
    }

    /**
     * Apply transaction impact to the active monthly budget for a user.
     * Expense consumes budget; income restores available budget.
     *
     * @param userId User id
     * @param type Transaction type
     * @param amount Transaction amount
     * @param categoryName Optional category name (used for expense categories)
     * @param direction +1 to apply, -1 to reverse
     */
    public void adjustBudgetFromTransaction(Long userId,
                                            TransactionType type,
                                            Double amount,
                                            String categoryName,
                                            int direction) {
        if (userId == null || type == null || amount == null || amount <= 0 || (direction != 1 && direction != -1)) {
            return;
        }

        Optional<Budget> budgetOpt = findActiveBudgetForUser(userId);
        if (budgetOpt.isEmpty()) {
            return;
        }

        Budget budget = budgetOpt.get();

        // Expense increases spent amount, income decreases it (restores budget).
        double signedDelta = (type == TransactionType.EXPENSE ? amount : -amount) * direction;
        double updatedSpent = budget.getTotalSpent() + signedDelta;
        budget.setTotalSpent(Math.max(0.0, updatedSpent));

        if (type == TransactionType.EXPENSE && categoryName != null && !categoryName.isBlank()) {
            BudgetCategory category = budget.getCategories().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                    .findFirst()
                    .orElse(null);

            if (category != null) {
                double updatedCategorySpent = category.getSpent() + (amount * direction);
                category.setSpent(Math.max(0.0, updatedCategorySpent));
                budgetCategoryRepository.save(category);
            }
        }

        budgetRepository.save(budget);
        checkBudget(budget.getId());
    }

    private Optional<Budget> findActiveBudgetForUser(Long userId) {
        Optional<Budget> monthly = budgetRepository.findByUserIdAndPeriod(userId, "monthly");
        if (monthly.isPresent()) {
            return monthly;
        }

        Optional<Budget> monthlyUpper = budgetRepository.findByUserIdAndPeriod(userId, "MONTHLY");
        if (monthlyUpper.isPresent()) {
            return monthlyUpper;
        }

        List<Budget> budgets = budgetRepository.findAllByUserId(userId);
        if (budgets.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(budgets.get(0));
    }
}
