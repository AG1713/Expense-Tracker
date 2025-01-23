package com.example.expensetracker.repository.database;

import androidx.annotation.NonNull;

public class Goal {
    private long id;
    @NonNull
    private String name;
    private Long category_id;
    private double amount;
    private double expense;
    @NonNull
    private String start_date;
    @NonNull
    private String end_date;
    @NonNull
    private String status;

    public Goal(@NonNull String name, Long category_id, double amount, double expense, @NonNull String start_date, @NonNull String end_date, @NonNull String status) {
        this.name = name;
        this.category_id = category_id;
        this.amount = amount;
        this.expense = expense;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    @NonNull
    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(@NonNull String start_date) {
        this.start_date = start_date;
    }

    @NonNull
    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(@NonNull String end_date) {
        this.end_date = end_date;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }
}
