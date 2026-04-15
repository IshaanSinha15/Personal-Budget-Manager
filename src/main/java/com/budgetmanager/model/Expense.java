package com.budgetmanager.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Concrete implementation of Transaction for Expense
 * Implements LSP - Expense behaves as Transaction
 */
@Entity
@DiscriminatorValue("EXPENSE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Expense extends Transaction {

    public Expense(BigDecimal amount, Category category, LocalDateTime date, 
                   Long userId, String description) {
        super(null, amount, TransactionType.EXPENSE, category, date, userId, description, null);
    }

    @Override
    public BigDecimal getNetAmount() {
        // Expense subtracts from the budget
        return this.getAmount().negate();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Expense cloned = (Expense) super.clone();
        cloned.setId(null); // Reset ID for new transaction
        cloned.setCreatedAt(null); // Will be set in PrePersist
        return cloned;
    }
}
