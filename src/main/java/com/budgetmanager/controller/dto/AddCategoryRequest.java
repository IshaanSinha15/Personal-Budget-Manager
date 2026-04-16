package com.budgetmanager.controller.dto;

public class AddCategoryRequest {
    private String name;
    private Double limit;

    public AddCategoryRequest() {
    }

    public AddCategoryRequest(String name, Double limit) {
        this.name = name;
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }
}
