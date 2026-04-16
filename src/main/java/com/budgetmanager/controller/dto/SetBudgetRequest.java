package com.budgetmanager.controller.dto;

public class SetBudgetRequest {
    private Double amount;
    private String period;

    public SetBudgetRequest() {
    }

    public SetBudgetRequest(Double amount, String period) {
        this.amount = amount;
        this.period = period;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
