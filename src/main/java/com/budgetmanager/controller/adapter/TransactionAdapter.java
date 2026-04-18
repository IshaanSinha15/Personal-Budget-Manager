package com.budgetmanager.controller.adapter;

import org.springframework.stereotype.Component;

import com.budgetmanager.controller.dto.TransactionRequest;
import com.budgetmanager.controller.dto.TransactionResponse;
import com.budgetmanager.model.Category;
import com.budgetmanager.model.Transaction;
import com.budgetmanager.model.TransactionType;

/**
 * Adapter to convert between Transaction entities and DTOs
 * Maintains separation of concerns between presentation and model layers
 */
@Component
public class TransactionAdapter {

    /**
     * Convert TransactionRequest DTO to Transaction entity type
     * @param request TransactionRequest DTO
     * @return TransactionType enum
     */
    public TransactionType toTransactionType(TransactionRequest request) {
        try {
            return TransactionType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + request.getType());
        }
    }

    /**
     * Convert category string to Category enum
     * @param categoryString Category string
     * @return Category enum
     */
    public Category toCategory(String categoryString) {
        try {
            String normalized = categoryString == null ? "" : categoryString.trim()
                .toUpperCase()
                .replace('-', '_')
                .replace(' ', '_');
            return Category.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + categoryString);
        }
    }

    /**
     * Convert Transaction entity to TransactionResponse DTO
     * @param transaction Transaction entity
     * @return TransactionResponse DTO
     */
    public TransactionResponse toTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType().name());
        response.setCategory(transaction.getCategory().name());
        response.setDescription(transaction.getDescription());
        response.setDate(transaction.getDate());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setNetAmount(transaction.getNetAmount());
        return response;
    }
}
