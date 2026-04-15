package com.budgetmanager.decorator;

import java.math.BigDecimal;

/**
 * Base decorator interface for transaction enhancements
 */
public interface TransactionFeature {
    BigDecimal getNetAmount();
    String getDescription();
    Object getFeatureData();
}
