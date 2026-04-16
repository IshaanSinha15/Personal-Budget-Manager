package com.budgetmanager.decorator;

import com.budgetmanager.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Decorator for marking transactions as recurring
 */
@Data
@AllArgsConstructor
public class RecurringTransaction implements TransactionFeature {
    private Transaction transaction;
    private String frequency;  // DAILY, WEEKLY, MONTHLY, YEARLY
    private Integer occurrences;

    public RecurringTransaction(Transaction transaction, String frequency) {
        this.transaction = transaction;
        this.frequency = frequency;
        this.occurrences = 1;
    }

    @Override
    public BigDecimal getNetAmount() {
        return transaction.getNetAmount();
    }

    @Override
    public String getDescription() {
        String baseDescription = transaction.getDescription() != null ? 
            transaction.getDescription() : "";
        return baseDescription + " [Recurring: " + frequency + "]";
    }

    @Override
    public Object getFeatureData() {
        return frequency;
    }

    public void incrementOccurrences() {
        this.occurrences++;
    }
}
