package com.example.expensetracker.views.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.expensetracker.R;
import com.example.expensetracker.repository.database.BudgetDB;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GoalsAdapter extends CursorAdapter {
    public GoalsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.goal_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int nameIndex = cursor.getColumnIndex(BudgetDB.GOALS_NAME);
        int categoryNameIndex = cursor.getColumnIndex("category_name");
        int amountIndex = cursor.getColumnIndex(BudgetDB.GOALS_AMOUNT);
        int expenseIndex = cursor.getColumnIndex(BudgetDB.GOALS_EXPENSE);
        int startDateIndex = cursor.getColumnIndex(BudgetDB.GOALS_START_DATE);
        int endDateIndex = cursor.getColumnIndex(BudgetDB.GOALS_END_DATE);
        int statusIndex = cursor.getColumnIndex(BudgetDB.GOALS_STATUS);

        TextView name = view.findViewById(R.id.goal_name);
        TextView categoryName = view.findViewById(R.id.goal_category);
        TextView amount = view.findViewById(R.id.original_amount);
        TextView expense = view.findViewById(R.id.remaining_amount);
        TextView startDate = view.findViewById(R.id.goal_start_date);
        TextView endDate = view.findViewById(R.id.goal_end_date);
        TextView status = view.findViewById(R.id.goal_status);
        PieChart pieChart = view.findViewById(R.id.pieChart);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float)cursor.getDouble(expenseIndex), ""));
        entries.add(new PieEntry((float)(((cursor.getDouble(amountIndex) - cursor.getDouble(expenseIndex)) <= 0) ? 0 : cursor.getDouble(amountIndex) - cursor.getDouble(expenseIndex)), ""));
        PieDataSet dataSet = new PieDataSet(entries, "Label");
        dataSet.setColors(Color.CYAN, Color.TRANSPARENT);
        dataSet.setDrawValues(false);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTouchEnabled(false);
        pieChart.invalidate();

        name.setText(cursor.getString(nameIndex));
        categoryName.setText("Category: " + ((cursor.getString(categoryNameIndex) == null) ? "All" : cursor.getString(categoryNameIndex)));
        amount.setText("Goal: " + cursor.getDouble(amountIndex));
        expense.setText("Expense: " + cursor.getDouble(expenseIndex));
        startDate.setText("Start date: " + cursor.getString(startDateIndex));
        endDate.setText("End date: " + cursor.getString(endDateIndex));
        if (Objects.equals(cursor.getString(statusIndex), "failed")) status.setTextColor(Color.RED);
        else if (Objects.equals(cursor.getString(statusIndex), "completed")) status.setTextColor(Color.GREEN);
        else if (Objects.equals(cursor.getString(statusIndex), "upcoming")) status.setTextColor(Color.YELLOW);
        else status.setTextColor(Color.CYAN);
        status.setText(cursor.getString(statusIndex));


    }
}
