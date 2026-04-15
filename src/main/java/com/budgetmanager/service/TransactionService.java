package com.budgetmanager.service;

import com.budgetmanager.controller.dto.TransactionResponse;
import com.budgetmanager.factory.TransactionFactory;
import com.budgetmanager.model.*;
import com.budgetmanager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for transaction management
 * Implements business logic and orchestrates repository operations
 * GRASP Low Coupling - Module is independent with minimal dependencies
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Add a new transaction
     * Uses Factory Pattern for creating appropriate transaction type
     * @param amount Transaction amount
     * @param type Transaction type (INCOME/EXPENSE)
     * @param category Transaction category
     * @param date Transaction date
     * @param userId User ID
     * @param description Optional description
     * @return Created Transaction
     */
    public Transaction addTransaction(BigDecimal amount, TransactionType type, 
                                     Category category, LocalDateTime date, 
                                     Long userId, String description) {
        
        // Validate user existence (assuming user service would validate)
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        // Use Factory Pattern to create appropriate transaction
        Transaction transaction = TransactionFactory.createTransaction(
            type, amount, category, date, userId, description
        );

        return transactionRepository.save(transaction);
    }

    /**
     * Get all transactions for a user
     * @param userId User ID
     * @return List of user's transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return transactionRepository.findByUserIdOrderByDateDesc(userId);
    }

    /**
     * Get transactions by type
     * @param userId User ID
     * @param type Transaction type
     * @return Filtered transactions
     */
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactionsByType(Long userId, TransactionType type) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return transactionRepository.findByUserIdAndTypeOrderByDateDesc(userId, type);
    }

    /**
     * Get transactions within date range
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @return Transactions in range
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDateRange(Long userId, 
                                                       LocalDateTime startDate, 
                                                       LocalDateTime endDate) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        return transactionRepository.findTransactionsByDateRange(userId, startDate, endDate);
    }

    /**
     * Get transactions by category
     * @param userId User ID
     * @param category Category
     * @return Transactions in category
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCategory(Long userId, Category category) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        return transactionRepository.findByUserIdAndCategory(userId, category.name());
    }

    /**
     * Calculate total income for user
     * @param userId User ID
     * @return Total income
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalIncome(Long userId) {
        List<Transaction> incomeTransactions = getUserTransactionsByType(userId, TransactionType.INCOME);
        return incomeTransactions.stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate total expenses for user
     * @param userId User ID
     * @return Total expenses
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalExpenses(Long userId) {
        List<Transaction> expenseTransactions = getUserTransactionsByType(userId, TransactionType.EXPENSE);
        return expenseTransactions.stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate net balance (income - expenses)
     * @param userId User ID
     * @return Net balance
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateNetBalance(Long userId) {
        BigDecimal totalIncome = calculateTotalIncome(userId);
        BigDecimal totalExpenses = calculateTotalExpenses(userId);
        return totalIncome.subtract(totalExpenses);
    }

    /**
     * Clone a transaction for recurring entries (Prototype Pattern)
     * @param transactionId ID of transaction to clone
     * @param newDate Date for cloned transaction
     * @return Cloned transaction
     */
    public Transaction cloneTransaction(Long transactionId, LocalDateTime newDate) {
        Transaction original = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        try {
            Transaction cloned = (Transaction) original.clone();
            cloned.setDate(newDate);
            return transactionRepository.save(cloned);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone transaction", e);
        }
    }

    /**
     * Update transaction
     * @param transactionId Transaction ID
     * @param amount New amount
     * @param category New category
     * @param description New description
     * @return Updated transaction
     */
    public Transaction updateTransaction(Long transactionId, BigDecimal amount, 
                                        Category category, String description) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            transaction.setAmount(amount);
        }
        if (category != null) {
            transaction.setCategory(category);
        }
        if (description != null) {
            transaction.setDescription(description);
        }
        
        return transactionRepository.save(transaction);
    }

    /**
     * Delete transaction
     * @param transactionId Transaction ID
     */
    public void deleteTransaction(Long transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new IllegalArgumentException("Transaction not found");
        }
        transactionRepository.deleteById(transactionId);
    }

    /**
     * Get transaction by ID
     * @param transactionId Transaction ID
     * @return Transaction
     */
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }
}
