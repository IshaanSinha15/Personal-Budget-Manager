package com.budgetmanager.decorator;

import com.budgetmanager.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Decorator for adding attachments reference to transactions
 */
@Data
@AllArgsConstructor
public class AttachmentTransaction implements TransactionFeature {
    private Transaction transaction;
    private List<String> attachmentUrls;

    public AttachmentTransaction(Transaction transaction) {
        this.transaction = transaction;
        this.attachmentUrls = new ArrayList<>();
    }

    @Override
    public BigDecimal getNetAmount() {
        return transaction.getNetAmount();
    }

    @Override
    public String getDescription() {
        String baseDescription = transaction.getDescription() != null ? 
            transaction.getDescription() : "";
        return baseDescription + " [Attachments: " + attachmentUrls.size() + "]";
    }

    @Override
    public Object getFeatureData() {
        return attachmentUrls;
    }

    public void addAttachment(String url) {
        this.attachmentUrls.add(url);
    }

    public void removeAttachment(String url) {
        this.attachmentUrls.remove(url);
    }
}
