package com.budgetmanager.controller.dto;

import com.budgetmanager.model.Budget;
import java.time.LocalDateTime;

public class BudgetResponse {
    private Long id;
    private Long userId;
    private Double totalBudget;
    private Double totalSpent;
    private Double remainingBudget;
    private Double percentageUsed;
    private Boolean isExceeded;
    private String period;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BudgetResponse() {
    }

    public BudgetResponse(Budget budget) {
        this.id = budget.getId();
        this.userId = budget.getUserId();
        this.totalBudget = budget.getTotalBudget();
        this.totalSpent = budget.getTotalSpent();
        this.remainingBudget = budget.getRemainingBudget();
        this.percentageUsed = budget.getPercentageUsed();
        this.isExceeded = budget.isBudgetExceeded();
        this.period = budget.getPeriod();
        this.createdAt = budget.getCreatedAt();
        this.updatedAt = budget.getUpdatedAt();
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
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
