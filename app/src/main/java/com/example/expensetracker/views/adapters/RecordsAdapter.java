package com.example.expensetracker.views.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.expensetracker.R;
import com.example.expensetracker.repository.database.BudgetDB;

public class RecordsAdapter extends CursorAdapter {
    public RecordsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.record_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int amountIndex = cursor.getColumnIndex(BudgetDB.RECORDS_AMOUNT);
        int operationIndex = cursor.getColumnIndex(BudgetDB.RECORDS_OPERATION);
        int partyIndex = cursor.getColumnIndex(BudgetDB.PARTIES_NICKNAME);
        int dateIndex = cursor.getColumnIndex(BudgetDB.RECORDS_DATE);
        int timeIndex = cursor.getColumnIndex(BudgetDB.RECORDS_TIME);
        int categoryIndex = cursor.getColumnIndex(BudgetDB.CATEGORIES_NAME);
        int descriptionIndex = cursor.getColumnIndex(BudgetDB.RECORDS_DESCRIPTION);

        TextView amount = view.findViewById(R.id.amount);
        TextView operation = view.findViewById(R.id.operation);
        TextView party = view.findViewById(R.id.party);
        TextView date = view.findViewById(R.id.date);
        TextView time = view.findViewById(R.id.time);
        TextView category = view.findViewById(R.id.category);
        TextView description = view.findViewById(R.id.description);

        amount.setText(cursor.getString(amountIndex));
        operation.setText(cursor.getString(operationIndex));
        party.setText(cursor.getString(partyIndex));
        date.setText(cursor.getString(dateIndex));
        time.setText(cursor.getString(timeIndex));
        category.setText(cursor.getString(categoryIndex));
        description.setText(cursor.getString(descriptionIndex));
    }


}
