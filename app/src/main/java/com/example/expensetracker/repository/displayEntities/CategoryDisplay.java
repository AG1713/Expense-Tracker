package com.example.expensetracker.repository.displayEntities;

import com.example.expensetracker.repository.database.Category;

public class CategoryDisplay {
    private Category category;
    private int level;

    public CategoryDisplay(Category category, int level) {
        this.category = category;
        this.level = level;
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
}
