package com.budgetmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Abstract base class for all transaction types (Income/Expense)
 * Implements LSP - Income and Expense will behave as Transaction
 */
@Entity
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Transaction implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }

    /**
     * Abstract method for calculating net impact
     * Income returns positive, Expense returns negative
     */
    public abstract BigDecimal getNetAmount();

    /**
     * Clone method for Prototype pattern
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
