package com.example.expensetracker.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentGoalsBinding;
import com.example.expensetracker.repository.database.BudgetDB;
import com.example.expensetracker.repository.database.Goal;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.GoalsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.function.Consumer;

public class GoalsFragment extends Fragment {
    FragmentGoalsBinding binding;
    MainActivityViewModel viewModel;
    GoalsAdapter adapter;
    Cursor cursor;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_goals, container, false);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        listView = binding.listView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        fab = binding.fab;

        viewModel.getAllGoals(c -> {
            getActivity().runOnUiThread(() -> {
                cursor = c;
                adapter = new GoalsAdapter(getContext(), cursor, 0);
                listView.setAdapter(adapter);
            });

        });

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.getAllGoals(c -> getActivity().runOnUiThread(() -> {
            cursor = c;
            adapter.swapCursor(cursor);
            swipeRefreshLayout.setRefreshing(false);
        })));

        fab.setOnClickListener(view -> {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.add_goal_dialog);
            dialog.show();

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
                window.setAttributes(params);
            }

            EditText name = dialog.findViewById(R.id.goal_name);
            Spinner category = dialog.findViewById(R.id.goal_spinner_category);
            EditText amount = dialog.findViewById(R.id.goal_amount);
            EditText expense = dialog.findViewById(R.id.goal_expense);
            TextView start_date = dialog.findViewById(R.id.goal_start_date);
            TextView end_date = dialog.findViewById(R.id.goal_end_date);
            Button addGoalBtn = dialog.findViewById(R.id.add_goal_btn);

            // Spinners first
            viewModel.getAllCategories(cursor -> {
                MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.CATEGORIES_NAME});
                defaultCursor.addRow(new Object[]{-1, "N/A"});
                MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor});

                SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        mergedCursor,
                        new String[]{BudgetDB.CATEGORIES_NAME},
                        new int[] {android.R.id.text1},
                        0
                );

                getActivity().runOnUiThread(() -> {
                    category.setAdapter(cursorAdapter);
                });
            });

            // Dates
            start_date.setOnClickListener(v12 -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                datePickerDialog.show();

                datePickerDialog.setOnDateSetListener((v, year, month, dayOfMonth) -> start_date.setText(year + "-" + month+1 + "-" + dayOfMonth));
            });

            end_date.setOnClickListener(v12 -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                datePickerDialog.show();

                datePickerDialog.setOnDateSetListener((v, year, month, dayOfMonth) -> end_date.setText(year + "-" + month+1 + "-" + dayOfMonth));
            });

            // Amount edittext
            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String input = s.toString();
                    if (input.contains(".")) {
                        String[] parts = input.split("\\.");
                        if (parts.length > 1 && parts[1].length() > 2) {
                            s.replace(0, s.length(), parts[0] + "." + parts[1].substring(0, 2));
                        }
                    }
                }
            });

            addGoalBtn.setOnClickListener(view1 -> {
                Cursor selectedCategory = (Cursor) category.getSelectedItem();

                if (name.getText().toString().trim().isEmpty())
                    Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                else if (amount.getText().toString().trim().isEmpty())
                    Toast.makeText(getContext(), "Target cannot be empty", Toast.LENGTH_SHORT).show();
                else if (start_date.getText().toString().trim().isEmpty())
                    Toast.makeText(getContext(), "Start date cannot be empty", Toast.LENGTH_SHORT).show();
                else if (end_date.getText().toString().trim().isEmpty())
                    Toast.makeText(getContext(), "End date cannot be empty", Toast.LENGTH_SHORT).show();
                else {
                    viewModel.addGoal(new Goal(
                            name.getText().toString().trim(),
                            (selectedCategory.getLong(0) == -1) ? null : selectedCategory.getLong(0),
                            Double.parseDouble(amount.getText().toString().trim()),
                            Double.parseDouble(expense.getText().toString().trim()),
                            start_date.getText().toString(),
                            end_date.getText().toString(),
                            "active"
                    ), new ErrorCallback() {
                        @Override
                        public void onSuccess() {
                            getActivity().runOnUiThread(dialog::dismiss);
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Goal with same name already exists", Toast.LENGTH_SHORT).show());
                        }
                    });

                }



            });

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_item_options2, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.menu_delete) {
                            deleteGoal(i);
                            return true;
                        }

                        return false;
                    }
                });

                popupMenu.show();
                return true;
            }
        });

        return binding.getRoot();
    }

    public void deleteGoal(int i){
        cursor.moveToPosition(i);
        viewModel.removeGoal(cursor.getLong(0));
    }

}