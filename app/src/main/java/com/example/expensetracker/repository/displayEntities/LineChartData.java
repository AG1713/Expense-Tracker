package com.example.expensetracker.repository.displayEntities;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class LineChartData {
    private ArrayList<Entry> entries;
    private ArrayList<String> labels;
    private float minY;
    private float maxY;

    public LineChartData() {
        entries = new ArrayList<>();
        labels = new ArrayList<>();
        minY = 0;
        maxY = 0;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public void addEntry(Entry entry){
        entries.add(entry);
    }

    public void addLabel(String label){
        labels.add(label);
    }
}
