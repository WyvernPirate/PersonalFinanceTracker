
package com.example.personalfinancetracker;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private double amount;
    private String category;
    private Date date;
    private String notes;

    public Transaction(double amount, String category, Date date, String notes) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.notes = notes;
    }

    // Getters
    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    // For easier serialization/deserialization
    public long getDateMillis() {
        return date != null ? date.getTime() : 0;
    }
}
