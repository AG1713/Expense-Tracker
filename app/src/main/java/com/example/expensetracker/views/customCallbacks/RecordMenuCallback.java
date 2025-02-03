package com.example.expensetracker.views.customCallbacks;

import com.example.expensetracker.repository.database.Record;

public interface RecordMenuCallback {
    void onUpdate(Record record);
    void onDelete(Record record);
}
