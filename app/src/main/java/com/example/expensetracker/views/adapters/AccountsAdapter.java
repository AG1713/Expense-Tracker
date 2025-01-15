package com.example.expensetracker.views.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.expensetracker.R;
import com.example.expensetracker.repository.database.BudgetDB;

public class AccountsAdapter extends CursorAdapter {
    public AccountsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.account_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int accountNoIndex = cursor.getColumnIndex(BudgetDB.ACCOUNTS_ACCOUNT_NO);
        int total = cursor.getColumnIndex("Total");

        TextView accountNo = view.findViewById(R.id.account_no);
        TextView amount = view.findViewById(R.id.amount);

        accountNo.setText(cursor.getString(accountNoIndex));
        amount.setText(String.valueOf(cursor.getDouble(total)));
    }
}
