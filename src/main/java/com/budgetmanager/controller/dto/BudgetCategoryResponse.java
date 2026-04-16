package com.budgetmanager.controller.dto;

import com.budgetmanager.model.BudgetCategory;

public class BudgetCategoryResponse {
    private Long id;
    private String name;
    private Double budgetLimit;
    private Double spent;
    private Double remainingBudget;
    private Double percentageUsed;
    private Boolean isExceeded;

    public BudgetCategoryResponse() {
    }

    public BudgetCategoryResponse(BudgetCategory category) {
        this.id = category.getId();
        this.name = category.getName();
        this.budgetLimit = category.getBudgetLimit();
        this.spent = category.getSpent();
        this.remainingBudget = category.getRemainingBudget();
        this.percentageUsed = category.getPercentageUsed();
        this.isExceeded = category.isExceeded();
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

    public Double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(Double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public Double getPercentageUsed() {
        return percentageUsed;
    }

    public void setPercentageUsed(Double percentageUsed) {
        this.percentageUsed = percentageUsed;
    }

    public Boolean getIsExceeded() {
        return isExceeded;
    }

    public void setIsExceeded(Boolean isExceeded) {
        this.isExceeded = isExceeded;
    }
}
