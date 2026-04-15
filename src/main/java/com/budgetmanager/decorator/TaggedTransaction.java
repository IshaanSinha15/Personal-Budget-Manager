package com.budgetmanager.decorator;

import com.budgetmanager.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorator for adding tags to transactions
 */
@Data
@AllArgsConstructor
public class TaggedTransaction implements TransactionFeature {
    private Transaction transaction;
    private List<String> tags;

    public TaggedTransaction(Transaction transaction) {
        this.transaction = transaction;
        this.tags = new ArrayList<>();
    }

    @Override
    public BigDecimal getNetAmount() {
        return transaction.getNetAmount();
    }

    @Override
    public String getDescription() {
        String baseDescription = transaction.getDescription() != null ? 
            transaction.getDescription() : "";
        return baseDescription + " [Tags: " + String.join(", ", tags) + "]";
    }

    @Override
    public Object getFeatureData() {
        return tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
    }
}
