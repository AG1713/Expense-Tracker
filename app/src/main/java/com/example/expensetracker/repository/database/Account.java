package com.example.expensetracker.repository.database;

public class Account {
    private long account_id;
    private String account_no;

    public Account(String account_no) {
        this.account_no = account_no;
    }

    public long getAccount_id() {
        return account_id;
    }

    public void setAccount_id(long account_id) {
        this.account_id = account_id;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }
}
