package com.budgetmanager.controller;

import com.budgetmanager.controller.adapter.TransactionAdapter;
import com.budgetmanager.controller.dto.TransactionRequest;
import com.budgetmanager.controller.dto.TransactionResponse;
import com.budgetmanager.model.Category;
import com.budgetmanager.model.Transaction;
import com.budgetmanager.model.TransactionType;
import com.budgetmanager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Transaction Management APIs
 * Handles HTTP requests for transaction operations
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionAdapter transactionAdapter;

    /**
     * Add a new transaction
     * POST /api/transactions/add
     * @param request TransactionRequest DTO
     * @param userId User ID (from path variable or header)
     * @return Created transaction response
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addTransaction(
            @RequestBody TransactionRequest request,
            @RequestParam Long userId) {
        
        try {
            // Validate input
            if (request.getAmount() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Amount is required"));
            }
            
            TransactionType type = transactionAdapter.toTransactionType(request);
            Category category = transactionAdapter.toCategory(request.getCategory());
            
            LocalDateTime transactionDate = request.getDate() != null ? 
                request.getDate() : LocalDateTime.now();
            
            // Create transaction using service
            Transaction transaction = transactionService.addTransaction(
                request.getAmount(),
                type,
                category,
                transactionDate,
                userId,
                request.getDescription()
            );
            
            TransactionResponse response = transactionAdapter.toTransactionResponse(transaction);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Transaction added successfully");
            result.put("data", response);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to add transaction: " + e.getMessage()));
        }
    }

    /**
     * Get all transactions for a user
     * GET /api/transactions/{userId}
     * @param userId User ID
     * @return List of transactions
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserTransactions(@PathVariable Long userId) {
        try {
            List<Transaction> transactions = transactionService.getUserTransactions(userId);
            List<TransactionResponse> responses = transactions.stream()
                .map(transactionAdapter::toTransactionResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("count", responses.size());
            result.put("data", responses);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve transactions: " + e.getMessage()));
        }
    }

    /**
     * Get transactions by type
     * GET /api/transactions/{userId}/type/{type}
     * @param userId User ID
     * @param type Transaction type (INCOME or EXPENSE)
     * @return Filtered transactions
     */
    @GetMapping("/{userId}/type/{type}")
    public ResponseEntity<Map<String, Object>> getTransactionsByType(
            @PathVariable Long userId,
            @PathVariable String type) {
        
        try {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            List<Transaction> transactions = transactionService.getUserTransactionsByType(userId, transactionType);
            List<TransactionResponse> responses = transactions.stream()
                .map(transactionAdapter::toTransactionResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("type", type.toUpperCase());
            result.put("count", responses.size());
            result.put("data", responses);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Invalid transaction type: " + type));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve transactions: " + e.getMessage()));
        }
    }

    /**
     * Get transactions by category
     * GET /api/transactions/{userId}/category/{category}
     * @param userId User ID
     * @param category Category
     * @return Transactions in category
     */
    @GetMapping("/{userId}/category/{category}")
    public ResponseEntity<Map<String, Object>> getTransactionsByCategory(
            @PathVariable Long userId,
            @PathVariable String category) {
        
        try {
            Category cat = transactionAdapter.toCategory(category);
            List<Transaction> transactions = transactionService.getTransactionsByCategory(userId, cat);
            List<TransactionResponse> responses = transactions.stream()
                .map(transactionAdapter::toTransactionResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("category", category.toUpperCase());
            result.put("count", responses.size());
            result.put("data", responses);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Invalid category: " + category));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve transactions: " + e.getMessage()));
        }
    }

    /**
     * Get financial summary for user
     * GET /api/transactions/{userId}/summary
     * @param userId User ID
     * @return Summary of income, expenses, and balance
     */
    @GetMapping("/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getFinancialSummary(@PathVariable Long userId) {
        try {
            BigDecimal totalIncome = transactionService.calculateTotalIncome(userId);
            BigDecimal totalExpenses = transactionService.calculateTotalExpenses(userId);
            BigDecimal netBalance = transactionService.calculateNetBalance(userId);
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalIncome", totalIncome);
            summary.put("totalExpenses", totalExpenses);
            summary.put("netBalance", netBalance);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("userId", userId);
            result.put("data", summary);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to calculate summary: " + e.getMessage()));
        }
    }

    /**
     * Clone a transaction for recurring entries
     * POST /api/transactions/{transactionId}/clone
     * @param transactionId Transaction ID to clone
     * @param newDate New date for cloned transaction
     * @return Cloned transaction
     */
    @PostMapping("/{transactionId}/clone")
    public ResponseEntity<Map<String, Object>> cloneTransaction(
            @PathVariable Long transactionId,
            @RequestParam LocalDateTime newDate) {
        
        try {
            if (newDate == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("New date is required"));
            }
            
            Transaction cloned = transactionService.cloneTransaction(transactionId, newDate);
            TransactionResponse response = transactionAdapter.toTransactionResponse(cloned);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Transaction cloned successfully");
            result.put("data", response);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to clone transaction: " + e.getMessage()));
        }
    }

    /**
     * Update a transaction
     * PUT /api/transactions/{transactionId}
     * @param transactionId Transaction ID
     * @param request Updated transaction data
     * @return Updated transaction
     */
    @PutMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> updateTransaction(
            @PathVariable Long transactionId,
            @RequestBody TransactionRequest request) {
        
        try {
            Category category = request.getCategory() != null ? 
                transactionAdapter.toCategory(request.getCategory()) : null;
            
            Transaction updated = transactionService.updateTransaction(
                transactionId,
                request.getAmount(),
                category,
                request.getDescription()
            );
            
            TransactionResponse response = transactionAdapter.toTransactionResponse(updated);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Transaction updated successfully");
            result.put("data", response);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to update transaction: " + e.getMessage()));
        }
    }

    /**
     * Delete a transaction
     * DELETE /api/transactions/{transactionId}
     * @param transactionId Transaction ID
     * @return Success response
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(@PathVariable Long transactionId) {
        try {
            transactionService.deleteTransaction(transactionId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Transaction deleted successfully");
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to delete transaction: " + e.getMessage()));
        }
    }

    /**
     * Get a specific transaction by ID
     * GET /api/transactions/detail/{transactionId}
     * @param transactionId Transaction ID
     * @return Transaction details
     */
    @GetMapping("/detail/{transactionId}")
    public ResponseEntity<Map<String, Object>> getTransactionById(@PathVariable Long transactionId) {
        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            TransactionResponse response = transactionAdapter.toTransactionResponse(transaction);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to retrieve transaction: " + e.getMessage()));
        }
    }

    /**
     * Helper method to create error response
     * @param message Error message
     * @return Error response map
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
