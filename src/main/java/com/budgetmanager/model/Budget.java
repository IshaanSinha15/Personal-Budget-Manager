package com.budgetmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Budget Entity
 * GRASP High Cohesion: All budget data consolidated
 * Composite Pattern: Contains BudgetCategory children
 */
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double totalBudget;

    @Column(nullable = false)
    private Double totalSpent;

    @Column(nullable = false)
    private String period; // MONTHLY, YEARLY, etc.

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BudgetCategory> categories = new ArrayList<>();

    public Budget() {
        this.totalSpent = 0.0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Budget(Long userId, Double totalBudget, String period) {
        this.userId = userId;
        this.totalBudget = totalBudget;
        this.totalSpent = 0.0;
        this.period = period;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
        this.updatedAt = LocalDateTime.now();
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<BudgetCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<BudgetCategory> categories) {
        this.categories = categories;
    }

    public void addCategory(BudgetCategory category) {
        categories.add(category);
        category.setBudget(this);
    }

    public void removeCategory(BudgetCategory category) {
        categories.remove(category);
        category.setBudget(null);
    }

    public double getRemainingBudget() {
        return totalBudget - totalSpent;
    }

    public boolean isBudgetExceeded() {
        return totalSpent > totalBudget;
    }

    public double getPercentageUsed() {
        return (totalSpent / totalBudget) * 100;
    }

    public double getCategoriesTotal() {
        return categories.stream()
                .mapToDouble(BudgetCategory::getBudgetLimit)
                .sum();
    }
}
