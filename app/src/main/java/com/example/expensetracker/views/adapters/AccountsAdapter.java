package com.example.expensetracker.views.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.AccountItemBinding;
import com.example.expensetracker.databinding.PartyItemBinding;
import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.repository.database.BudgetDB;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.views.customCallbacks.AccountMenuCallback;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.MyViewHolder> {
    Cursor cursor;
    AccountMenuCallback menuCallback;

    public AccountsAdapter(Cursor cursor, AccountMenuCallback menuCallback){
        this.cursor = cursor;
        this.menuCallback = menuCallback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AccountItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.account_item,
                parent,
                false
        );

        return new AccountsAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        cursor.moveToPosition(position);

        Account account = new Account(cursor.getString(1));
        account.setAccount_id(cursor.getLong(0));
        holder.binding.setAccount(account);
        holder.binding.amount.setText(String.valueOf(cursor.getDouble(2)));

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(holder.itemView.getContext(), v);
            menu.getMenuInflater().inflate(R.menu.menu_item_options1, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.menu_delete){
                    menuCallback.onDelete(account);
                    return true;
                }

                return false;
            });

            menu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        AccountItemBinding binding;

        public MyViewHolder(AccountItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }



    public void swapCursor(Cursor cursor){
        if (this.cursor != null && !this.cursor.isClosed()) this.cursor.close();
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
