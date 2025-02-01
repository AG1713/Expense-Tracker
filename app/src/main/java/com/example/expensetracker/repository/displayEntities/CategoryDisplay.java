package com.example.expensetracker.repository.displayEntities;

import com.example.expensetracker.repository.database.Category;

public class CategoryDisplay {
    private Category category;
    private int level;
    private double amount;

    public CategoryDisplay(Category category, int level, double amount) {
        this.category = category;
        this.level = level;
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
