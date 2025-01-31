package com.example.expensetracker.views.adapters;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.PartyItemBinding;
import com.example.expensetracker.views.customCallbacks.PartyMenuCallback;

public class PartiesAdapter extends RecyclerView.Adapter<PartiesAdapter.MyViewHolder> {
    private Cursor cursor;
    private final PartyMenuCallback listener;

    public PartiesAdapter(Cursor cursor, PartyMenuCallback listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        PartyItemBinding binding;
        public MyViewHolder(PartyItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PartyItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.party_item,
                parent,
                false
        );

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        cursor.moveToPosition(position);

        Party party = new Party(cursor.getString(1), cursor.getString(2));
        party.setId(cursor.getLong(0));
        holder.binding.setParty(party);
        holder.binding.amount.setText(String.valueOf(cursor.getDouble(3)));

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(holder.itemView.getContext(), v);
            menu.getMenuInflater().inflate(R.menu.menu_item_options1, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.menu_update){
                    listener.onUpdate(party);
                    return true;
                }
                else if (item.getItemId() == R.id.menu_delete){
                    listener.onDelete(party);
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

    public void swapCursor(Cursor cursor){
        if (this.cursor != null && !this.cursor.isClosed()) this.cursor.close();
        this.cursor = cursor;
        notifyDataSetChanged();
    }

}
