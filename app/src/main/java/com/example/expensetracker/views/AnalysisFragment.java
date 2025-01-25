package com.example.expensetracker.views;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentAnalysisBinding;
import com.example.expensetracker.repository.displayEntities.CategoryEntries;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalysisFragment extends Fragment {
    private FragmentAnalysisBinding binding;
    private MainActivityViewModel viewModel;
    private BarChart barChart;
    private HorizontalBarChart categoriesChart;
    private final ArrayList<BarEntry> dayEntries = new ArrayList<>();
    private final ArrayList<String> dates = new ArrayList<>();
    private final Map<String, CategoryEntries> dateCategoryEntriesMap = new HashMap<>();
    private final CategoryEntries categoryEntries = new CategoryEntries(null, null);
    private final String TAG = "AnalysisFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_analysis, container, false);
        barChart = binding.barChart;
        categoriesChart = binding.categoriesChart;
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        initializeData();
        setupBarChartListener();

        return binding.getRoot();
    }

    private void initializeData() {
        viewModel.getSevenDayExpense(dayEntries, dates, this::initializeBarChart);
        viewModel.getSevenDaysCategoriesAmounts(categoryEntries, this::initializeCategoriesChart);
        viewModel.getAllDatesCategoriesAmounts(dateCategoryEntriesMap);
    }

    private void setupBarChartListener() {
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, com.github.mikephil.charting.highlight.Highlight h) {
                String date = dates.get((int) e.getX());
                Log.d(TAG, "Selected Date: " + date);
                getActivity().runOnUiThread(() -> updateCategoriesChartForDate(date));
            }

            @Override
            public void onNothingSelected() {
                getActivity().runOnUiThread(AnalysisFragment.this::initializeCategoriesChart);
            }
        });
    }

    private void initializeBarChart() {
        if (dayEntries.isEmpty()) return;

        BarDataSet barDataSet = createBarDataSet(dayEntries, "Daily Expenses");
        setupChart(barChart, barDataSet, dates, true);
    }

    private void initializeCategoriesChart() {
        if (categoryEntries.getEntries() == null || categoryEntries.getEntries().isEmpty()) return;

        BarDataSet barDataSet = createBarDataSet(categoryEntries.getEntries(), "Category Expenses");
        setupChart(categoriesChart, barDataSet, categoryEntries.getCategoryNames(), false);
    }

    private void updateCategoriesChartForDate(String date) {
        CategoryEntries entries = dateCategoryEntriesMap.get(date);
        if (entries == null || entries.getEntries().isEmpty()) return;

        BarDataSet barDataSet = createBarDataSet(entries.getEntries(), "Expenses for " + date);
        setupChart(categoriesChart, barDataSet, entries.getCategoryNames(), false);
    }

    private BarDataSet createBarDataSet(ArrayList<BarEntry> entries, String label) {
        BarDataSet barDataSet = new BarDataSet(entries, label);
        barDataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.WHITE);
        return barDataSet;
    }

    private void setupChart(BarChart chart, BarDataSet dataSet, ArrayList<String> labels, boolean enableTouch) {
        BarData barData = new BarData(dataSet);

        // Calculate bar width in relation to chart width and max allowed bar width in dp
        int maxBarWidthInDp = 50; // Max width in dp
        float maxBarWidthInPx = convertDpToPx(maxBarWidthInDp); // Convert dp to px

        // Calculate bar width based on the total chart width and number of entries
        float chartWidth = chart.getWidth(); // Chart width in pixels
        float totalBars = labels.size(); // Number of x-axis labels (bars)

        if (totalBars > 0) {
            float calculatedBarWidth = Math.min(maxBarWidthInPx / chartWidth, 1f / totalBars);
            barData.setBarWidth(calculatedBarWidth);
        }

        chart.setData(barData);

        // X-Axis configuration
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);

        // Y-Axis configuration
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(Color.WHITE);

        // General chart settings
        chart.zoomOut();
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(enableTouch);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDragEnabled(enableTouch);
        chart.setScaleEnabled(enableTouch);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
    }

    // Helper method to convert dp to pixels
    private float convertDpToPx(int dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

}
