package com.example.expensetracker.repository.displayEntities;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class CategoryEntries {
    private ArrayList<BarEntry> entries;
    private ArrayList<String> categoryNames;
    private double min;
    private double max;

    public CategoryEntries(ArrayList<BarEntry> entries, ArrayList<String> categoryNames) {
        this.entries = entries;
        this.categoryNames = categoryNames;
        min = 0;
        max = 0;
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

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
