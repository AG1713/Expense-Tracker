package com.example.expensetracker;

import com.example.expensetracker.repository.displayEntities.CategoryDisplay;

public interface CustomListener {
    void onUpdate(CategoryDisplay categoryDisplay);
    void onDelete(CategoryDisplay categoryDisplay);
}
