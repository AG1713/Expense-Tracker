package com.example.expensetracker.repository.displayEntities;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class ChartData {
    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;
    private float minX;
    private float maxX;

    public ChartData() {
        entries = new ArrayList<>();
        labels = new ArrayList<>();
        minX = 0;
        maxX = 0;
    }

    public ChartData(ArrayList<BarEntry> entries, ArrayList<String> labels, float minX, float maxX) {
        this.entries = entries;
        this.labels = labels;
        this.minX = minX;
        this.maxX = maxX;
    }

    public ArrayList<BarEntry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<BarEntry> entries) {
        this.entries = entries;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public void addEntry(BarEntry entry){
        entries.add(entry);
    }

    public void addLabel(String label){
        labels.add(label);
    }

}
