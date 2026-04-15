package com.budgetmanager.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for transaction response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    
    private Long id;
    
    private BigDecimal amount;
    
    private String type;
    
    private String category;
    
    private String description;
    
    private LocalDateTime date;
    
    private LocalDateTime createdAt;
    
    private BigDecimal netAmount;
}
