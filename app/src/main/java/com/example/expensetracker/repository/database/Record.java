package com.example.expensetracker.repository.database;

import androidx.annotation.NonNull;

public class Record {
    private long id;

    private String account_no;

    @NonNull
    private String date;

    @NonNull
    private String time;

    @NonNull
    private String operation;

    @NonNull
    private Double amount;

    private Long party;

    private String description;

    private Long category_id;

    public Record(String account_no, @NonNull String date, @NonNull String time, @NonNull String operation, @NonNull Double amount, Long party, Long category_id) {
        this.account_no = account_no;
        this.date = date;
        this.time = time;
        this.operation = operation;
        this.amount = amount;
        this.party = party;
        this.category_id = category_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getTime() {
        return time;
    }

    public void setTime(@NonNull String time) {
        this.time = time;
    }

    @NonNull
    public String getOperation() {
        return operation;
    }

    public void setOperation(@NonNull String operation) {
        this.operation = operation;
    }

    @NonNull
    public Double getAmount() {
        return amount;
    }

    public void setAmount(@NonNull Double amount) {
        this.amount = amount;
    }

    public Long getParty() {
        return party;
    }

    public void setParty(Long party) {
        this.party = party;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }
}
