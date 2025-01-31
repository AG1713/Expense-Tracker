package com.example.expensetracker.views.customCallbacks;

import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.repository.database.Party;

public interface AccountsMenuCallback {
    void onUpdate(Account account);
    void onDelete(Account account);
}
