package com.budgetmanager.repository;

import com.budgetmanager.model.Transaction;
import com.budgetmanager.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Transaction persistence
 * Handles database operations for all transaction types
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find all transactions for a user
     * @param userId User ID
     * @return List of transactions
     */
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    /**
     * Find transactions by user and type
     * @param userId User ID
     * @param type Transaction type (INCOME or EXPENSE)
     * @return List of filtered transactions
     */
    List<Transaction> findByUserIdAndTypeOrderByDateDesc(Long userId, TransactionType type);

    /**
     * Find transactions within a date range
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of transactions in the range
     */
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    List<Transaction> findTransactionsByDateRange(@Param("userId") Long userId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Find transactions by category
     * @param userId User ID
     * @param category Category name
     * @return List of transactions in category
     */
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId " +
           "AND t.category = :category ORDER BY t.date DESC")
    List<Transaction> findByUserIdAndCategory(@Param("userId") Long userId, 
                                             @Param("category") String category);

    /**
     * Count transactions by user and type
     * @param userId User ID
     * @param type Transaction type
     * @return Count of transactions
     */
    Long countByUserIdAndType(Long userId, TransactionType type);
}
