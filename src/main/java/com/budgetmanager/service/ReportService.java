package com.budgetmanager.service;

import com.budgetmanager.controller.dto.TransactionResponse;
import com.budgetmanager.model.Category;
import com.budgetmanager.model.Transaction;
import com.budgetmanager.model.TransactionType;
import com.budgetmanager.repository.BudgetRepository;
import com.budgetmanager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ReportService - Facade Pattern
 *
 * Acts as a single simplified interface that internally fetches data
 * from TransactionRepository and BudgetRepository (Pure Fabrication / Facade).
 *
 * Template Method pattern is applied: each report follows the steps:
 *   1. fetchData  2. processData  3. formatOutput
 */
@Service
public class ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    // -------------------------------------------------------------------------
    // 1. Monthly Report
    // -------------------------------------------------------------------------

    /**
     * Returns total income and total expense for the current calendar month.
     *
     * Template steps:
     *   fetchData  → findTransactionsByDateRange
     *   process    → separate INCOME vs EXPENSE, sum amounts
     *   format     → return as Map
     */
    public Map<String, Object> getMonthlyReport(Long userId) {
        // Step 1 – fetch
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth   = LocalDateTime.now();
        List<Transaction> transactions = transactionRepository.findTransactionsByDateRange(userId, startOfMonth, endOfMonth);

        // Step 2 – process
        BigDecimal totalIncome  = sumByType(transactions, TransactionType.INCOME);
        BigDecimal totalExpense = sumByType(transactions, TransactionType.EXPENSE);

        // Step 3 – format
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("month", startOfMonth.getMonth().toString());
        report.put("year",  startOfMonth.getYear());
        report.put("totalIncome",  totalIncome);
        report.put("totalExpense", totalExpense);
        report.put("balance", totalIncome.subtract(totalExpense));
        report.put("transactionCount", transactions.size());
        return report;
    }

    // -------------------------------------------------------------------------
    // 2. Category-wise Report
    // -------------------------------------------------------------------------

    /**
     * Groups ALL transactions for the user by category and returns the total
     * amount per category.
     *
     * Template steps:
     *   fetchData  → findByUserIdOrderByDateDesc
     *   process    → group by category, sum amounts
     *   format     → return as Map<categoryName, total>
     */
    public Map<String, Object> getCategoryWiseReport(Long userId) {
        // Step 1 – fetch
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId);

        // Step 2 – process: group by category enum, sum BigDecimal amounts
        Map<Category, BigDecimal> grouped = new HashMap<>();
        for (Transaction t : transactions) {
            grouped.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
        }

        // Step 3 – format: use displayName as key
        Map<String, Object> report = new LinkedHashMap<>();
        grouped.forEach((category, total) ->
                report.put(category.getDisplayName(), total));

        if (report.isEmpty()) {
            report.put("message", "No transactions found for this user.");
        }
        return report;
    }

    // -------------------------------------------------------------------------
    // 3. Summary
    // -------------------------------------------------------------------------

    /**
     * Returns lifetime total income, total expense, and remaining balance.
     *
     * Template steps:
     *   fetchData  → findByUserIdOrderByDateDesc
     *   process    → sum by type
     *   format     → return summary map
     */
    public Map<String, Object> getSummary(Long userId) {
        // Step 1 – fetch
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId);

        // Step 2 – process
        BigDecimal totalIncome  = sumByType(transactions, TransactionType.INCOME);
        BigDecimal totalExpense = sumByType(transactions, TransactionType.EXPENSE);
        BigDecimal balance      = totalIncome.subtract(totalExpense);

        // Step 3 – format
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalIncome",  totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("balance",      balance);
        return summary;
    }

    // -------------------------------------------------------------------------
    // 4. Dashboard Data
    // -------------------------------------------------------------------------

    /**
     * Combines summary figures with the 5 most-recent transactions.
     * Consumed by the frontend dashboard at GET /reports/dashboard/{userId}.
     *
     * Template steps:
     *   fetchData  → summary + recent transactions
     *   process    → map transactions to lightweight DTOs
     *   format     → merge into a single response map
     */
    public Map<String, Object> getDashboardData(Long userId) {
        // Step 1 – fetch (reuse getSummary logic)
        List<Transaction> allTransactions = transactionRepository.findByUserIdOrderByDateDesc(userId);

        BigDecimal totalIncome  = sumByType(allTransactions, TransactionType.INCOME);
        BigDecimal totalExpense = sumByType(allTransactions, TransactionType.EXPENSE);
        BigDecimal balance      = totalIncome.subtract(totalExpense);

        // Recent transactions (up to 5), mapped to the existing TransactionResponse DTO
        List<TransactionResponse> recentTransactions = allTransactions.stream()
                .limit(5)
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());

        // Step 3 – format
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("totalIncome",         totalIncome);
        dashboard.put("totalExpense",         totalExpense);
        dashboard.put("balance",              balance);
        dashboard.put("recentTransactions",   recentTransactions);
        return dashboard;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Sums the amount of all transactions that match the given type.
     */
    private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Maps a Transaction entity to the existing TransactionResponse DTO.
     * Reuses the DTO already defined in the project — no duplication.
     */
    private TransactionResponse toTransactionResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getType().name(),
                t.getCategory().getDisplayName(),
                t.getDescription(),
                t.getDate(),
                t.getCreatedAt(),
                t.getNetAmount()
        );
    }
}
