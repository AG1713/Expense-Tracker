package com.example.expensetracker.repository.database;

public class Account {
    private String account;

    public Account(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
