package com.budgetmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.budgetmanager.controller.dto.*;
import com.budgetmanager.model.Budget;
import com.budgetmanager.model.BudgetCategory;
import com.budgetmanager.service.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BudgetController
 * REST API endpoints for budget management
 * DIP: Depends on BudgetService interface
 */
@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private BudgetNotificationService notificationService;

    /**
     * POST /api/budget/set
     * Set budget for a user
     * Strategy Pattern: Client can configure different strategies
     */
    @PostMapping("/set")
    public ResponseEntity<BudgetResponse> setBudget(
            @RequestParam Long userId,
            @RequestBody SetBudgetRequest request) {
        try {
            // Use Monthly Strategy by default
            budgetService.setStrategy(new MonthlyBudgetStrategy());

            Budget budget = budgetService.setBudget(userId, request.getAmount(), request.getPeriod());
            return ResponseEntity.ok(new BudgetResponse(budget));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/budget/{userId}
     * Get budget for a user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<BudgetResponse> getBudget(
            @PathVariable Long userId,
            @RequestParam String period) {
        Optional<Budget> budget = budgetService.getBudget(userId, period);
        return budget.map(b -> ResponseEntity.ok(new BudgetResponse(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /api/budget/user/{userId}/all
     * Get all budgets for a user
     */
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<BudgetResponse>> getUserBudgets(@PathVariable Long userId) {
        List<BudgetResponse> budgets = budgetService.getUserBudgets(userId)
                .stream()
                .map(BudgetResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(budgets);
    }

    /**
     * GET /api/budget/{budgetId}/remaining
     * Get remaining budget
     */
    @GetMapping("/{budgetId}/remaining")
    public ResponseEntity<Double> getRemainingBudget(@PathVariable Long budgetId) {
        try {
            double remaining = budgetService.getRemainingBudget(budgetId);
            return ResponseEntity.ok(remaining);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/budget/{budgetId}/check
     * Check if budget is exceeded
     */
    @PostMapping("/{budgetId}/check")
    public ResponseEntity<Void> checkBudget(@PathVariable Long budgetId) {
        try {
            budgetService.checkBudget(budgetId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/budget/{budgetId}/spend
     * Record spending against budget
     * Composite Pattern: Updates both parent and category
     */
    @PostMapping("/{budgetId}/spend")
    public ResponseEntity<BudgetResponse> recordSpending(
            @PathVariable Long budgetId,
            @RequestBody SpendingRequest request) {
        try {
            budgetService.recordSpending(budgetId, request.getAmount(), request.getCategory());
            Optional<Budget> budget = budgetService.getBudgetById(budgetId);
            return budget.map(b -> ResponseEntity.ok(new BudgetResponse(b)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/budget/{budgetId}/category
     * Add category to budget
     * Composite Pattern: Build budget hierarchy
     */
    @PostMapping("/{budgetId}/category")
    public ResponseEntity<BudgetCategoryResponse> addCategory(
            @PathVariable Long budgetId,
            @RequestBody AddCategoryRequest request) {
        try {
            BudgetCategory category = budgetService.addCategory(budgetId, request.getName(), request.getLimit());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new BudgetCategoryResponse(category));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/budget/{budgetId}/categories
     * Get all categories for a budget
     */
    @GetMapping("/{budgetId}/categories")
    public ResponseEntity<List<BudgetCategoryResponse>> getCategories(@PathVariable Long budgetId) {
        List<BudgetCategoryResponse> categories = budgetService.getBudgetCategories(budgetId)
                .stream()
                .map(BudgetCategoryResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    /**
     * GET /api/budget/category/{categoryId}
     * Get specific category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<BudgetCategoryResponse> getCategory(@PathVariable Long categoryId) {
        Optional<BudgetCategory> category = budgetService.getCategoryById(categoryId);
        return category.map(c -> ResponseEntity.ok(new BudgetCategoryResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/budget/{budgetId}
     * Delete budget
     */
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId) {
        try {
            budgetService.deleteBudget(budgetId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/budget/{budgetId}/strategy/monthly
     * Switch to monthly strategy
     * Strategy Pattern: Dynamic strategy switching
     */
    @PostMapping("/{budgetId}/strategy/monthly")
    public ResponseEntity<String> setMonthlyStrategy(@PathVariable Long budgetId) {
        try {
            budgetService.setStrategy(new MonthlyBudgetStrategy());
            budgetService.checkBudget(budgetId);
            return ResponseEntity.ok("Monthly budget strategy activated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/budget/{budgetId}/strategy/category/{categoryName}
     * Switch to category-based strategy
     * Strategy Pattern: Dynamic strategy switching
     */
    @PostMapping("/{budgetId}/strategy/category/{categoryName}")
    public ResponseEntity<String> setCategoryStrategy(
            @PathVariable Long budgetId,
            @PathVariable String categoryName) {
        try {
            budgetService.setStrategy(new CategoryBasedBudgetStrategy(categoryName));
            budgetService.checkBudget(budgetId);
            return ResponseEntity.ok("Category-based budget strategy activated for: " + categoryName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
