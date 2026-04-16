package com.budgetmanager.decorator;

/**
 * Decorator Pattern Implementation for adding optional features to transactions
 * Allows adding tags, notes, attachments, etc. without modifying Transaction class
 * 
 * Each decorator is now in a separate file:
 * - TransactionFeature.java: Base interface
 * - TaggedTransaction.java: Add tags to transactions
 * - NotedTransaction.java: Add notes to transactions
 * - AttachmentTransaction.java: Add attachments reference to transactions
 * - RecurringTransaction.java: Mark transactions as recurring
 * 
 * Example usage:
 * 
 * // Create a transaction
 * Transaction income = new Income(amount, category, date, userId, description);
 * 
 * // Add tags
 * TaggedTransaction taggedIncome = new TaggedTransaction(income);
 * taggedIncome.addTag("salary");
 * taggedIncome.addTag("monthly");
 * 
 * // Add note
 * NotedTransaction notedTransaction = new NotedTransaction(income);
 * notedTransaction.setNote("Base salary payment");
 * 
 * // Add attachment for receipt
 * AttachmentTransaction attachedIncome = new AttachmentTransaction(income);
 * attachedIncome.addAttachment("https://example.com/receipt-001.pdf");
 * 
 * // Mark as recurring
 * RecurringTransaction recurringIncome = new RecurringTransaction(income, "MONTHLY");
 * 
 * // All decorators preserve the original net amount
 */
