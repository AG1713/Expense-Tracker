package com.example.expensetracker.views;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private double[] min = {0};
    private double[] max = {0};
    private final Map<String, CategoryEntries> dateCategoryEntriesMap = new HashMap<>();
    private final CategoryEntries categoryEntries = new CategoryEntries(null, null);
    private final String TAG = "AnalysisFragment";

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_analysis, container, false);
        barChart = binding.barChart;
        categoriesChart = binding.categoriesChart;
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        swipeRefreshLayout = binding.swipeRefreshLayout;

        initializeCharts();
        initializeData();
        setupBarChartListener();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            initializeCharts();
            initializeData();
            setupBarChartListener();
            getActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
        });

        return binding.getRoot();
    }

    private void initializeData() {
        initializeCharts();
        viewModel.getSevenDayExpense(dayEntries, dates, min, max, () -> setMainChartData());
        viewModel.getSevenDaysCategoriesAmounts(categoryEntries, () -> setDefaultCategoriesChartData(categoryEntries));
//        viewModel.getAllDatesCategoriesAmounts(dateCategoryEntriesMap);
    }

    private void setupBarChartListener() {
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, com.github.mikephil.charting.highlight.Highlight h) {
                String date = dates.get((int) e.getX());
                Log.d(TAG, "Selected Date: " + date);
                getActivity().runOnUiThread(() -> setDefaultCategoriesChartData(dateCategoryEntriesMap.get(date)));
            }

            @Override
            public void onNothingSelected() {
                getActivity().runOnUiThread(() -> setDefaultCategoriesChartData(categoryEntries));
            }
        });
    }

    private BarDataSet createBarDataSet(ArrayList<BarEntry> entries, String label) {
        BarDataSet barDataSet = new BarDataSet(entries, label);
        barDataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.WHITE);
        return (entries.isEmpty()) ? null : barDataSet;
    }

    // Helper method to convert dp to pixels
    private float convertDpToPx(int dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    private void setMainChartData(){
        {
            BarDataSet barDataSet = createBarDataSet(dayEntries, "Daily Expenses");

            if (barDataSet != null){// Calculate bar width in relation to chart width and max allowed bar width in dp
                BarData barData = new BarData(barDataSet);
                int maxBarWidthInDp = 50; // Max width in dp
                float maxBarWidthInPx = convertDpToPx(maxBarWidthInDp); // Convert dp to px

                // Set a fixed bar width to prevent recalculation issues
                float fixedBarWidth = 0.3f; // Adjust as needed
                barData.setBarWidth(fixedBarWidth);

                // Calculate bar width based on the total chart width and number of entries
                float chartWidth = barChart.getWidth(); // Chart width in pixels
                float totalBars = dates.size(); // Number of x-axis labels (bars)

                if (totalBars < 3) {
                    float calculatedBarWidth = Math.min(maxBarWidthInPx / chartWidth, 1f / totalBars);
                    barData.setBarWidth(calculatedBarWidth);
                }

                barChart.setData(barData);

                // X-Axis configuration
                XAxis xAxis = barChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextColor(Color.WHITE);

                // Y-Axis configuration
                YAxis leftAxis = barChart.getAxisLeft();
                leftAxis.setTextColor(Color.WHITE);
                leftAxis.setAxisMinimum((float) (min[0] + (min[0] * 0.5f)));
                leftAxis.setAxisMaximum((float) (max[0] + (max[0] * 0.5f)));
                Log.d(TAG, "min: " + (float) (min[0] + (min[0] * 0.1f)) + "\n" +
                        "max: " + (float) (max[0] - (max[0] * 0.1f)));
                barChart.invalidate();
            }
        }
    }

    private void setDefaultCategoriesChartData(CategoryEntries categoryEntries){
        // This is not working properly
        BarDataSet barDataSet = createBarDataSet(categoryEntries.getEntries(), "All days expenses");
        BarData barData = new BarData(barDataSet);

        // Calculate bar width in relation to chart width and max allowed bar width in dp
        int maxBarWidthInDp = 50; // Max width in dp
        float maxBarWidthInPx = convertDpToPx(maxBarWidthInDp); // Convert dp to px

        // Calculate bar width based on the total chart width and number of entries
        float chartWidth = categoriesChart.getWidth(); // Chart width in pixels
        float totalBars = categoryEntries.getCategoryNames().size(); // Number of x-axis labels (bars)

        // Define a base height per entry (in dp)
        int heightPerEntryDp = 40; // Adjust this value as needed
        int minHeightDp = 200;     // Minimum height in dp for the chart
        int maxHeightDp = 400;     // Maximum height in dp for the chart

        int entryCount = categoryEntries.getEntries().size();
        // Convert dp to pixels
        float heightPerEntryPx = convertDpToPx(heightPerEntryDp);
        float minHeightPx = convertDpToPx(minHeightDp);
        float maxHeightPx = convertDpToPx(maxHeightDp);

        // Calculate the desired height
        float calculatedHeightPx = entryCount * heightPerEntryPx;

        // Run the UI update on the main thread
        categoriesChart.post(() -> {
            ViewGroup.LayoutParams layoutParams = categoriesChart.getLayoutParams();
            layoutParams.height = (int) calculatedHeightPx;
            categoriesChart.setLayoutParams(layoutParams);
        });

//        if (totalBars < 3) {
//            float calculatedBarWidth = Math.min(maxBarWidthInPx / chartWidth, 1f / totalBars);
//            barData.setBarWidth(calculatedBarWidth);
//        }

        categoriesChart.setData(barData);

        // X-Axis configuration
        XAxis xAxis = categoriesChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(categoryEntries.getCategoryNames()));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);

        // Y-Axis configuration
        YAxis leftAxis = categoriesChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum((float) (categoryEntries.getMin() + (categoryEntries.getMin() * 0.1f)));
        leftAxis.setAxisMaximum((float) (categoryEntries.getMax() + (categoryEntries.getMax() * 0.1f)));

        categoriesChart.invalidate();
    }
    private void initializeCharts(){
        barChart.getAxisRight().setEnabled(false);
        barChart.zoomOut();
        barChart.getDescription().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();

        categoriesChart.getAxisRight().setEnabled(false);
        categoriesChart.getDescription().setEnabled(false);
        categoriesChart.setTouchEnabled(false);
        categoriesChart.setPinchZoom(false);
        categoriesChart.setDoubleTapToZoomEnabled(false);
        categoriesChart.setDragEnabled(false);
        categoriesChart.setScaleEnabled(false);
        categoriesChart.getLegend().setEnabled(false);
        categoriesChart.invalidate();

    }

}
