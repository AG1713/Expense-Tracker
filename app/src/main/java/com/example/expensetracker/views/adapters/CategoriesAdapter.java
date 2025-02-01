package com.example.expensetracker.views.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.views.customCallbacks.CategoryMenuCallback;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.CategoryItemBinding;
import com.example.expensetracker.repository.displayEntities.CategoryDisplay;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>{
    ArrayList<CategoryDisplay> categoryDisplays;
    CategoryMenuCallback listener;

    public CategoriesAdapter(ArrayList<CategoryDisplay> categoryDisplays, CategoryMenuCallback listener) {
        this.categoryDisplays = categoryDisplays;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CategoryItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.category_item,
                parent,
                false
        );

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (categoryDisplays.get(position).getLevel() > 1){
            holder.binding.categoryName.setText(" - " + categoryDisplays.get(position).getCategory().getName());
        }
        else holder.binding.categoryName.setText(categoryDisplays.get(position).getCategory().getName());
        
        holder.binding.categoryAmount.setText(String.valueOf(categoryDisplays.get(position).getAmount()));

        holder.itemView.setPadding((categoryDisplays.get(position).getLevel()-1)*75,
                holder.itemView.getPaddingTop(),
                holder.itemView.getPaddingRight(),
                holder.itemView.getPaddingBottom());

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(holder.itemView.getContext(), v);
            menu.getMenuInflater().inflate(R.menu.menu_item_options1, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.menu_update){
                    listener.onUpdate(categoryDisplays.get(position));
                    return true;
                }
                else if (item.getItemId() == R.id.menu_delete){
                    listener.onDelete(categoryDisplays.get(position));
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
        return categoryDisplays.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CategoryItemBinding binding;

        public MyViewHolder(CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setCategoryDisplays(ArrayList<CategoryDisplay> categoryDisplays) {
        this.categoryDisplays = categoryDisplays;
        notifyDataSetChanged();
    }
}
