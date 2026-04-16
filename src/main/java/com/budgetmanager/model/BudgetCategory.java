package com.budgetmanager.model;

import jakarta.persistence.*;

/**
 * Composite Pattern: Part of Budget hierarchy
 * Represents a category within a budget
 */
@Entity
@Table(name = "budget_categories")
public class BudgetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double budgetLimit;

    @Column(nullable = false)
    private Double spent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    public BudgetCategory() {
    }

    public BudgetCategory(String name, Double budgetLimit) {
        this.name = name;
        this.budgetLimit = budgetLimit;
        this.spent = 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(Double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public Double getSpent() {
        return spent;
    }

    public void setSpent(Double spent) {
        this.spent = spent;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public double getRemainingBudget() {
        return budgetLimit - spent;
    }

    public boolean isExceeded() {
        return spent > budgetLimit;
    }

    public double getPercentageUsed() {
        return (spent / budgetLimit) * 100;
    }
}
