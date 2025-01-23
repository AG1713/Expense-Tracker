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

public class MappingsAdapter extends CursorAdapter {
    public MappingsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.mapping_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int partyIndex = cursor.getColumnIndex(BudgetDB.PARTIES_NICKNAME);
        int amountIndex = cursor.getColumnIndex(BudgetDB.MAPPINGS_AMOUNT);
        int categoryIndex = cursor.getColumnIndex(BudgetDB.CATEGORIES_NAME);

        TextView party = view.findViewById(R.id.mapping_party);
        TextView amount = view.findViewById(R.id.mapping_amount);
        TextView category = view.findViewById(R.id.mapping_category);

        party.setText(cursor.getString(partyIndex));
        amount.setText(String.valueOf(cursor.getDouble(amountIndex)));
        category.setText(String.valueOf(cursor.getString(categoryIndex)));

    }
}
