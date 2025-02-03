package com.example.expensetracker.views.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.RecordItemBinding;
import com.example.expensetracker.repository.database.BudgetDB;
import com.example.expensetracker.repository.database.Record;
import com.example.expensetracker.views.customCallbacks.RecordMenuCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.MyViewHolder> {
    private Cursor cursor;
    private RecordMenuCallback callback;

    public RecordsAdapter(Cursor cursor, RecordMenuCallback callback) {
        this.cursor = cursor;
        this.callback = callback;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        RecordItemBinding binding;
        public MyViewHolder(RecordItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecordItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.record_item,
                parent,
                false
        );

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        cursor.moveToPosition(position);
        int idIndex = cursor.getColumnIndex("_id");
        int accountIdIndex = cursor.getColumnIndex(BudgetDB.ACCOUNTS_ID);
        int accountIndex = cursor.getColumnIndex(BudgetDB.ACCOUNTS_ACCOUNT_NO);
        int amountIndex = cursor.getColumnIndex(BudgetDB.RECORDS_AMOUNT);
        int operationIndex = cursor.getColumnIndex(BudgetDB.RECORDS_OPERATION);
        int partyIdIndex = cursor.getColumnIndex( BudgetDB.PARTIES_ID);
        int partyIndex = cursor.getColumnIndex(BudgetDB.PARTIES_NICKNAME);
        int dateIndex = cursor.getColumnIndex(BudgetDB.RECORDS_DATE);
        int timeIndex = cursor.getColumnIndex(BudgetDB.RECORDS_TIME);
        int categoryIdIndex = cursor.getColumnIndex(BudgetDB.CATEGORIES_ID);
        int categoryIndex = cursor.getColumnIndex(BudgetDB.CATEGORIES_NAME);
        int descriptionIndex = cursor.getColumnIndex(BudgetDB.RECORDS_DESCRIPTION);

        SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat defaultTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat newTimeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        String newDate;
        String newTime;
        try {
            newDate = newDateFormat.format(defaultDateFormat.parse(cursor.getString(dateIndex)));
            newTime = newTimeFormat.format(defaultTimeFormat.parse(cursor.getString(timeIndex)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Record record = new Record(
                cursor.getLong(accountIdIndex),
                cursor.getString(dateIndex),
                cursor.getString(timeIndex),
                cursor.getString(operationIndex),
                cursor.getDouble(amountIndex),
                cursor.getLong(partyIdIndex),
                cursor.getLong(categoryIdIndex)
        );
        record.setId(cursor.getLong(idIndex));
        Log.d("Check", "onBindViewHolder: " + record.getId());
        holder.binding.amount.setText(cursor.getString(amountIndex));
        holder.binding.operation.setText(cursor.getString(operationIndex));
        holder.binding.accountNo.setText((cursor.getString(accountIndex) == null || cursor.getString(accountIndex).isEmpty()) ? "N/A" : cursor.getString(accountIndex));
        holder.binding.party.setText((cursor.getString(partyIndex) == null || cursor.getString(partyIndex).isEmpty()) ? "N/A" : cursor.getString(partyIndex));
        holder.binding.date.setText(newDate);
        holder.binding.time.setText(newTime);
        holder.binding.category.setText((cursor.getString(categoryIndex) == null || cursor.getString(categoryIndex).isEmpty()) ? "N/A" : cursor.getString(categoryIndex));
        holder.binding.description.setText(cursor.getString(descriptionIndex));

        holder.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu menu = new PopupMenu(holder.itemView.getContext(), v);
                menu.getMenuInflater().inflate(R.menu.menu_item_options1, menu.getMenu());

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_update){
                            callback.onUpdate(record);
                            return true;
                        }
                        else if (item.getItemId() == R.id.menu_delete){
                            callback.onDelete(record);
                            return true;
                        }
                        return false;
                    }
                });

                menu.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        if (this.cursor != null && !this.cursor.isClosed()) this.cursor.close();
        this.cursor = cursor;
        notifyDataSetChanged();
    }

}
