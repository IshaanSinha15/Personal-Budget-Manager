package com.budgetmanager.controller;

import com.budgetmanager.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ReportController – GRASP Controller
 *
 * Handles all HTTP requests for the Reports & Dashboard module.
 * Delegates ALL business logic to ReportService (no logic here).
 *
 * Base paths: /reports and /api/reports
 */
@RestController
@RequestMapping({"/reports", "/api/reports"})
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * GET /reports/monthly/{userId}
     * Returns income/expense totals for the current month.
     */
    @GetMapping("/monthly/{userId}")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(@PathVariable Long userId) {
        Map<String, Object> report = reportService.getMonthlyReport(userId);
        return ResponseEntity.ok(report);
    }

    /**
     * GET /reports/category/{userId}
     * Returns spending/income grouped by category.
     */
    @GetMapping("/category/{userId}")
    public ResponseEntity<Map<String, Object>> getCategoryWiseReport(@PathVariable Long userId) {
        Map<String, Object> report = reportService.getCategoryWiseReport(userId);
        return ResponseEntity.ok(report);
    }

    /**
     * GET /reports/summary/{userId}
     * Returns lifetime totals: income, expense, balance.
     */
    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long userId) {
        Map<String, Object> summary = reportService.getSummary(userId);
        return ResponseEntity.ok(summary);
    }

    /**
     * GET /reports/dashboard/{userId}
     * Returns combined dashboard data: totals + 5 recent transactions.
     * Consumed by the frontend dashboard UI.
     */
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<Map<String, Object>> getDashboardData(@PathVariable Long userId) {
        Map<String, Object> dashboard = reportService.getDashboardData(userId);
        return ResponseEntity.ok(dashboard);
    }
}
