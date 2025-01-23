package com.example.expensetracker.repository.displayEntities;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class CategoryEntries {
    private ArrayList<BarEntry> entries;
    private ArrayList<String> categoryNames;

    public CategoryEntries(ArrayList<BarEntry> entries, ArrayList<String> categoryNames) {
        this.entries = entries;
        this.categoryNames = categoryNames;
    }

    public ArrayList<BarEntry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<BarEntry> entries) {
        this.entries = entries;
    }

    public ArrayList<String> getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(ArrayList<String> categoryNames) {
        this.categoryNames = categoryNames;
    }
}
