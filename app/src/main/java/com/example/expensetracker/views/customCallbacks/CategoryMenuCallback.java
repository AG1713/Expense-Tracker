package com.example.expensetracker.views.customCallbacks;

import com.example.expensetracker.repository.displayEntities.CategoryDisplay;

public interface CategoryMenuCallback {
    void onUpdate(CategoryDisplay categoryDisplay);
    void onDelete(CategoryDisplay categoryDisplay);
}
