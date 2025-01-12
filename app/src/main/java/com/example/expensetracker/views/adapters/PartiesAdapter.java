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

public class PartiesAdapter extends CursorAdapter {
    public PartiesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.party_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int nameIndex = cursor.getColumnIndex(BudgetDB.PARTIES_NAME);
        int nicknameIndex = cursor.getColumnIndex(BudgetDB.PARTIES_NICKNAME);
        int amountIndex = cursor.getColumnIndex("Total");

        TextView name = view.findViewById(R.id.name);
        TextView nickname = view.findViewById(R.id.nickname);
        TextView amount = view.findViewById(R.id.amount);

        name.setText(cursor.getString(nameIndex));
        nickname.setText(cursor.getString(nicknameIndex));
        amount.setText(String.valueOf(cursor.getDouble(amountIndex)));
    }
}
