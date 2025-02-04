package com.example.expensetracker.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentRecordsBinding;
import com.example.expensetracker.repository.Repository;
import com.example.expensetracker.repository.database.BudgetDB;
import com.example.expensetracker.repository.database.Record;
import com.example.expensetracker.repository.displayEntities.Filter;
import com.example.expensetracker.repository.displayEntities.LineChartData;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.PartiesAdapter;
import com.example.expensetracker.views.adapters.RecordsAdapter;
import com.example.expensetracker.views.customCallbacks.RecordMenuCallback;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class RecordsFragment extends Fragment {
    private static final String TAG = "RecordsFragment";
    FragmentRecordsBinding binding;
    MainActivityViewModel viewModel;
    RecordsAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    Dialog dialog;
    LineChart lineChart;
    RecordMenuCallback callback;
    TextView emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_records, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lineChart = binding.lineChart;
        emptyView = binding.emptyTextView;

        callback = new RecordMenuCallback() {
            @Override
            public void onUpdate(Record record) {
                showUpdateDialog(record);
            }

            @Override
            public void onDelete(Record record) {
                deleteRecord(record);
            }
        };

        viewModel.getFilterMutableLiveData().observe(getViewLifecycleOwner(), filter -> viewModel.getAllRecords(() -> {
            // Do nothing
        }));

        viewModel.getRecords().observe(getViewLifecycleOwner(), cursor -> {
            if (cursor != null){
                if (cursor.getCount() == 0) emptyView.setVisibility(View.VISIBLE);
                else emptyView.setVisibility(View.GONE);

                if (adapter != null) adapter.swapCursor(cursor);
                else {
                    adapter = new RecordsAdapter(cursor, callback);
                    recyclerView.setAdapter(adapter);
                }
            }
        });

        viewModel.getRecordsChartData().observe(getViewLifecycleOwner(), new Observer<LineChartData>() {
            @Override
            public void onChanged(LineChartData lineChartData) {
                Log.d(TAG, "onChanged: " + (lineChartData.getEntries() == null));
                lineChart.clear();
                if (lineChartData.getEntries() != null && !lineChartData.getEntries().isEmpty()){
                    Log.d(TAG, "onChanged: " + lineChartData.getEntries().isEmpty());
                    LineDataSet dataSet = new LineDataSet(lineChartData.getEntries(), "Records");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    dataSet.setValueTextColor(Color.WHITE);
                    dataSet.setValueTextSize(12f);
                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(lineChartData.getLabels()));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTextColor(Color.WHITE);
                    xAxis.setAxisMinimum(-1);
                    xAxis.setAxisMaximum(lineChartData.getLabels().size());
                    xAxis.setAvoidFirstLastClipping(true);
                    xAxis.setGranularity(1f);

                    YAxis yAxis = lineChart.getAxisLeft();
                    yAxis.setTextColor(Color.WHITE);
                    yAxis.setGranularity(1f);
                    yAxis.setInverted(true);

                    yAxis.setAxisMinimum(lineChartData.getMinY() - (Math.abs(lineChartData.getMinY())*0.3f));
                    yAxis.setAxisMaximum(lineChartData.getMaxY() + (Math.abs(lineChartData.getMaxY())*0.3f));

                    lineChart.getLegend().setEnabled(false);
                    lineChart.getAxisRight().setEnabled(false);
                    lineChart.getDescription().setEnabled(false);
                    lineChart.setPinchZoom(false);
                    lineChart.setDoubleTapToZoomEnabled(false);

                    lineChart.setVisibleXRangeMaximum(5);
                    lineChart.setDragEnabled(true);
                    lineChart.setNestedScrollingEnabled(true);
                    lineChart.moveViewToX(lineChartData.getEntries().size()-1);
                    lineChart.invalidate();
                }
            }
        });

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.getAllRecords(() -> swipeRefreshLayout.setRefreshing(false));
        });

        binding.mapping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MappingsActivity.class);
                startActivity(intent);
            }
        });

        fab = binding.fab;

        fab.setOnClickListener(v -> {
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.add_record_dialog);
            dialog.show();

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
                window.setAttributes(params);
            }

            Spinner account = dialog.findViewById(R.id.spinner_account);
            TextView date = dialog.findViewById(R.id.date);
            TextView time = dialog.findViewById(R.id.time);
            AtomicReference<String> selectedDate = new AtomicReference<>();
            AtomicReference<String> selectedTime = new AtomicReference<>();
            EditText amount = dialog.findViewById(R.id.amount);
            RadioGroup operation = dialog.findViewById(R.id.radio_group);
            EditText description = dialog.findViewById(R.id.description);
            Spinner party = dialog.findViewById(R.id.spinner_party);
            Spinner category = dialog.findViewById(R.id.spinner_category);
            Button addRecordBtn = dialog.findViewById(R.id.add_record_btn);
            Log.d(TAG, "onCreateView: " + (addRecordBtn == null));

            // First setting the spinners
            viewModel.getAllAccounts(new Consumer<Cursor>() {
                @Override
                public void accept(Cursor cursor) {
                    MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.ACCOUNTS_ACCOUNT_NO});
                    defaultCursor.addRow(new Object[]{-1, "N/A"});
                    MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor});

                    SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                            getContext(),
                            android.R.layout.simple_spinner_item,
                            mergedCursor,
                            new String[]{BudgetDB.ACCOUNTS_ACCOUNT_NO},
                            new int[] {android.R.id.text1},
                            0
                    );

                    getActivity().runOnUiThread(() -> {
                        account.setAdapter(cursorAdapter);
                    });
                }
            });

            viewModel.getAllParties(cursor -> {
                MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.PARTIES_NICKNAME});
                defaultCursor.addRow(new Object[]{-1, "N/A"});
                MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor});

                SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        mergedCursor,
                        new String[]{BudgetDB.PARTIES_NICKNAME},
                        new int[] {android.R.id.text1},
                        0
                );

                getActivity().runOnUiThread(() -> {
                    party.setAdapter(cursorAdapter);
                });
            });

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

            // Date and time
            date.setOnClickListener(v12 -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                datePickerDialog.show();

                datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                    selectedDate.set(year + "-" + (((month + 1) < 10) ? "0" + (month+1) : (month+1)) + "-" +
                            ((dayOfMonth + 1 < 10) ? "0" + dayOfMonth : dayOfMonth));
                    Log.d(TAG, "selected date: " + selectedDate.get());
                    date.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                });
            });

            time.setOnClickListener(v13 -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                    time.setText(hourOfDay % 12 + ":" + ((minute < 10) ? "0" + minute : minute) + ((hourOfDay < 12) ? " AM" : " PM"));
                    selectedTime.set(((hourOfDay < 10) ? "0" + hourOfDay : hourOfDay) + ":" + ((minute < 10) ? "0" + minute : minute) + ":" + "00");
                    Log.d(TAG, "selected time: " + selectedTime.get());
                    }, 12, 0, false);
                timePickerDialog.show();
            });

            // Radio group



            // Rest of the widgets
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

            addRecordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor selectedAccount = (Cursor) account.getSelectedItem();
                    Cursor selectedParty = (Cursor) party.getSelectedItem();
                    Cursor selectedCategory = (Cursor) category.getSelectedItem();

                    if (date.getText().toString().isEmpty())
                        Toast.makeText(getContext(), "Enter valid date", Toast.LENGTH_SHORT).show();
                    else if (time.getText().toString().isEmpty())
                        Toast.makeText(getContext(), "Enter valid time", Toast.LENGTH_SHORT).show();
                    else if (amount.getText().toString().isEmpty())
                        Toast.makeText(getContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
                    else if (operation.getCheckedRadioButtonId() == -1)
                        Toast.makeText(getContext(), "Please check credited/debited", Toast.LENGTH_SHORT).show();
                    else {
                        Record record = new Record(
                                (selectedAccount.getLong(0) == -1) ? null : selectedAccount.getLong(0),
                                selectedDate.get(),
                                selectedTime.get(),
                                (operation.getCheckedRadioButtonId() == R.id.credited) ? "credited" : "debited",
                                Double.parseDouble(amount.getText().toString()),
                                (selectedParty.getLong(0) == -1) ? null : selectedParty.getLong(0),
                                (selectedCategory.getLong(0) == -1) ? null : selectedCategory.getLong(0)
                        );
                        record.setDescription(description.getText().toString());
                        viewModel.addRecord(record);
                        viewModel.getAllRecords(() -> getActivity().runOnUiThread(() -> dialog.dismiss()));

                    }

                }
            });


        });

        return binding.getRoot();
    }


    void showUpdateDialog(Record record){;
        Dialog dialog1 = new Dialog(getContext());
        dialog1.setContentView(R.layout.add_record_dialog);
        dialog1.show();

        Window window = dialog1.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
            window.setAttributes(params);
        }

        Spinner account = dialog1.findViewById(R.id.spinner_account);
        TextView date = dialog1.findViewById(R.id.date);
        TextView time = dialog1.findViewById(R.id.time);
        AtomicReference<String> selectedDate = new AtomicReference<>();
        AtomicReference<String> selectedTime = new AtomicReference<>();
        EditText amount = dialog1.findViewById(R.id.amount);
        RadioGroup operation = dialog1.findViewById(R.id.radio_group);
        EditText description = dialog1.findViewById(R.id.description);
        Spinner party = dialog1.findViewById(R.id.spinner_party);
        Spinner category = dialog1.findViewById(R.id.spinner_category);
        Button addRecordBtn = dialog1.findViewById(R.id.add_record_btn);

        // First setting the spinners
        viewModel.getAllAccounts(cursor1 -> {
            MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.ACCOUNTS_ACCOUNT_NO});
            defaultCursor.addRow(new Object[]{-1, "N/A"});
            MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor1});

            SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    getContext(),
                    android.R.layout.simple_spinner_item,
                    mergedCursor,
                    new String[]{BudgetDB.ACCOUNTS_ACCOUNT_NO},
                    new int[] {android.R.id.text1},
                    0
            );

            getActivity().runOnUiThread(() -> {
                account.setAdapter(cursorAdapter);

                mergedCursor.moveToFirst();
                do {
                    if (Objects.equals(mergedCursor.getLong(0), record.getAccount_id())) {
                        account.setSelection(mergedCursor.getPosition());
                    }
                } while(mergedCursor.moveToNext());
            });
        });

        viewModel.getAllParties(cursor1 -> {
            MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.PARTIES_NICKNAME});
            defaultCursor.addRow(new Object[]{-1, "N/A"});
            MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor1});

            SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    getContext(),
                    android.R.layout.simple_spinner_item,
                    mergedCursor,
                    new String[]{BudgetDB.PARTIES_NICKNAME},
                    new int[] {android.R.id.text1},
                    0
            );

            getActivity().runOnUiThread(() -> {
                party.setAdapter(cursorAdapter);

                mergedCursor.moveToFirst();
                do {
                    if (Objects.equals(mergedCursor.getLong(0), record.getParty())) {
                        party.setSelection(mergedCursor.getPosition());
                    }
                } while(mergedCursor.moveToNext());
            });
        });

        viewModel.getAllCategories(cursor1 -> {
            MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.CATEGORIES_NAME});
            defaultCursor.addRow(new Object[]{-1, "N/A"});
            MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor1});

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

                mergedCursor.moveToFirst();
                do {
                    if (Objects.equals(mergedCursor.getLong(0), record.getCategory_id())) {
                        category.setSelection(mergedCursor.getPosition());
                    }
                } while(mergedCursor.moveToNext());
            });
        });

        // Date and time
        date.setText(record.getDate());
        selectedDate.set(record.getDate());
        date.setOnClickListener(v12 -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
            datePickerDialog.show();

            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                selectedDate.set(year + "-" + (((month + 1) < 10) ? "0" + (month+1) : (month+1)) + "-" +
                        ((dayOfMonth + 1 < 10) ? "0" + dayOfMonth : dayOfMonth));
                Log.d(TAG, "showUpdateDialog: " + selectedDate.get());
                date.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            });
        });

        time.setText(record.getTime());
        selectedTime.set(record.getTime());
        time.setOnClickListener(v13 -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                time.setText(hourOfDay % 12 + ":" + ((minute < 10) ? "0" + minute : minute) + ((hourOfDay < 12) ? " AM" : " PM"));
                selectedTime.set(((hourOfDay < 10) ? "0" + hourOfDay : hourOfDay) + ":" + ((minute < 10) ? "0" + minute : minute) + ":" + "00");
            }, 12, 0, false);
            timePickerDialog.show();
        });

        // Radio group
        if (record.getOperation().equals("credited")) ((RadioButton) operation.findViewById(R.id.credited)).setChecked(true);
        else ((RadioButton) operation.findViewById(R.id.debited)).setChecked(true);


        // Rest of the widgets
        amount.setText(String.valueOf(record.getAmount()));
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

        description.setText(record.getDescription());

        addRecordBtn.setOnClickListener(v -> {
            Cursor selectedAccount = (Cursor) account.getSelectedItem();
            Cursor selectedParty = (Cursor) party.getSelectedItem();
            Cursor selectedCategory = (Cursor) category.getSelectedItem();

            if (date.getText().toString().isEmpty())
                Toast.makeText(getContext(), "Enter valid date", Toast.LENGTH_SHORT).show();
            else if (time.getText().toString().isEmpty())
                Toast.makeText(getContext(), "Enter valid time", Toast.LENGTH_SHORT).show();
            else if (amount.getText().toString().isEmpty())
                Toast.makeText(getContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
            else if (operation.getCheckedRadioButtonId() == -1)
                Toast.makeText(getContext(), "Please check credited/debited", Toast.LENGTH_SHORT).show();
            else {
                Record newRecord = new Record(
                        (selectedAccount.getLong(0) == -1) ? null : selectedAccount.getLong(0),
                        selectedDate.get(),
                        selectedTime.get(),
                        (operation.getCheckedRadioButtonId() == R.id.credited) ? "credited" : "debited",
                        Double.parseDouble(amount.getText().toString()),
                        (selectedParty.getLong(0) == -1) ? null : selectedParty.getLong(0),
                        (selectedCategory.getLong(0) == -1) ? null : selectedCategory.getLong(0)
                );
                newRecord.setDescription(description.getText().toString());
                Log.d(TAG, "edited record id: " + record.getId());
                newRecord.setId(record.getId());
                viewModel.updateRecord(newRecord, record.getCategory_id(), record.getAmount());
                viewModel.getAllRecords(() -> getActivity().runOnUiThread(dialog1::dismiss));
            }

        });

        TextView info = dialog1.findViewById(R.id.info);
        info.setText("Update record");
        addRecordBtn.setText("Update Record");

    }

    void deleteRecord(Record record){
        viewModel.removeRecord(record.getId(), record.getCategory_id(), record.getAmount(), new Runnable() {
            @Override
            public void run() {
                viewModel.getAllRecords(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Record deleted", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });

    }


}