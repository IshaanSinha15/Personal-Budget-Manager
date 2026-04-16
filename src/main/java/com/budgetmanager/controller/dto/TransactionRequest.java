package com.budgetmanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for adding a new transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    
    private BigDecimal amount;
    
    private String type; // "INCOME" or "EXPENSE"
    
    private String category;
    
    private LocalDateTime date;
    
    private String description;
}
