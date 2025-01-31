package com.example.expensetracker.views.customCallbacks;

import com.example.expensetracker.repository.database.Party;

public interface PartyMenuCallback {
    void onUpdate(Party party);
    void onDelete(Party party);
}
