package com.budgetmanager.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding a new transaction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    private Long userId;

    private BigDecimal amount;

    private String type;

    private String category;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime date;

    private String description;
}
