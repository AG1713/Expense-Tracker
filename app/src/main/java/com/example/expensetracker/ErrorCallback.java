package com.example.expensetracker;

public interface ErrorCallback {
    void onSuccess();
    void onError(Exception e);

}
