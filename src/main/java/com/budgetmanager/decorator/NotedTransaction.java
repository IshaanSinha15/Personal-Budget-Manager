package com.budgetmanager.decorator;

import com.budgetmanager.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Decorator for adding notes to transactions
 */
@Data
@AllArgsConstructor
public class NotedTransaction implements TransactionFeature {
    private Transaction transaction;
    private String note;

    public NotedTransaction(Transaction transaction) {
        this.transaction = transaction;
        this.note = "";
    }

    @Override
    public BigDecimal getNetAmount() {
        return transaction.getNetAmount();
    }

    @Override
    public String getDescription() {
        String baseDescription = transaction.getDescription() != null ? 
            transaction.getDescription() : "";
        return baseDescription + " [Note: " + note + "]";
    }

    @Override
    public Object getFeatureData() {
        return note;
    }
}
