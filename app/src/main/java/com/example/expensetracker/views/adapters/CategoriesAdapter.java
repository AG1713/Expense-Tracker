package com.example.expensetracker.views.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.CategoryItemBinding;
import com.example.expensetracker.repository.displayEntities.CategoryDisplay;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {
    ArrayList<CategoryDisplay> categoryDisplays;

    public CategoriesAdapter(ArrayList<CategoryDisplay> categoryDisplays) {
        this.categoryDisplays = categoryDisplays;
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
        holder.binding.categoryName.setText(categoryDisplays.get(position).getCategory().getName());

        holder.itemView.setPadding((categoryDisplays.get(position).getLevel()-1)*50,
                holder.itemView.getPaddingTop(),
                holder.itemView.getPaddingRight(),
                holder.itemView.getPaddingBottom());

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
