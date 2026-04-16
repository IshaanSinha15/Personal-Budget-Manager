package com.budgetmanager.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Concrete implementation of Transaction for Income
 * Implements LSP - Income behaves as Transaction
 */
@Entity
@DiscriminatorValue("INCOME")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Income extends Transaction {

    public Income(BigDecimal amount, Category category, LocalDateTime date, 
                  Long userId, String description) {
        super(null, amount, TransactionType.INCOME, category, date, userId, description, null);
    }

    @Override
    public BigDecimal getNetAmount() {
        // Income adds to the budget
        return this.getAmount();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Income cloned = (Income) super.clone();
        cloned.setId(null); // Reset ID for new transaction
        cloned.setCreatedAt(null); // Will be set in PrePersist
        return cloned;
    }
}
