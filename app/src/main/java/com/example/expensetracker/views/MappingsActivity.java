package com.example.expensetracker.views;

import android.app.Dialog;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ActivityMappingsBinding;
import com.example.expensetracker.repository.database.BudgetDB;
import com.example.expensetracker.repository.database.Mapping;
import com.example.expensetracker.viewmodels.MappingsActivityViewModel;
import com.example.expensetracker.views.adapters.MappingsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.function.Consumer;

public class MappingsActivity extends AppCompatActivity {
    ActivityMappingsBinding binding;
    MappingsActivityViewModel viewModel;
    ListView listView;
    MappingsAdapter adapter;
    FloatingActionButton fab;
    SwipeRefreshLayout swipeRefreshLayout;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mappings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(MappingsActivityViewModel.class);
        fab = binding.fab;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        listView = binding.listView;
        TextView emptyView = binding.emptyTextView;
        listView.setEmptyView(emptyView);
        viewModel.getMappings().observe(this, cursor -> {
            if (cursor != null){
                if (adapter != null) adapter.swapCursor(cursor);
                else {
                    adapter = new MappingsAdapter(MappingsActivity.this, cursor, 0);
                    listView.setAdapter(adapter);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.getAllMappings(() -> swipeRefreshLayout.setRefreshing(false)));

        fab.setOnClickListener(v -> {
            initializeDialog();

            Spinner party = dialog.findViewById(R.id.spinner_party);
            EditText amount = dialog.findViewById(R.id.amount);
            Spinner category = dialog.findViewById(R.id.spinner_category);
            Button addMappingBtn = dialog.findViewById(R.id.add_mapping_btn);



            // Spinners
            viewModel.getAllParties(new Consumer<Cursor>() {
                @Override
                public void accept(Cursor cursor) {
                    MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.PARTIES_NICKNAME});
                    defaultCursor.addRow(new Object[]{-1, "Any"});
                    MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor});

                    SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                            MappingsActivity.this,
                            android.R.layout.simple_spinner_item,
                            mergedCursor,
                            new String[]{BudgetDB.PARTIES_NICKNAME},
                            new int[] {android.R.id.text1},
                            0
                    );

                    runOnUiThread(() -> {
                        party.setAdapter(cursorAdapter);
                    });
                }
            });

            viewModel.getAllCategories(cursor -> {

                SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                        MappingsActivity.this,
                        android.R.layout.simple_spinner_item,
                        cursor,
                        new String[]{BudgetDB.CATEGORIES_NAME},
                        new int[] {android.R.id.text1},
                        0
                );

                runOnUiThread(() -> {
                    category.setAdapter(cursorAdapter);
                });
            });


            // Edittext
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

            addMappingBtn.setOnClickListener(v1 -> {
                Cursor selectedParty = (Cursor) party.getSelectedItem();
                Cursor selectedCategory = (Cursor) category.getSelectedItem();

                if (amount.getText().toString().isEmpty() && selectedParty.getLong(0) == -1){
                    Toast.makeText(MappingsActivity.this, "Either amount or party should be non empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    String amount_str = amount.getText().toString();
                    viewModel.addMapping(new Mapping(
                                    (selectedParty.getLong(0) == -1) ? null : selectedParty.getLong(0),
                                    (amount_str.isEmpty()) ? null : Double.parseDouble(amount_str),
                                    selectedCategory.getLong(0)
                            ),
                            new ErrorCallback() {
                                @Override
                                public void onSuccess() {
                                    viewModel.getAllMappings(() -> runOnUiThread(dialog::dismiss));
                                }

                                @Override
                                public void onError(Exception e) {
                                    runOnUiThread(() -> Toast.makeText(MappingsActivity.this, "Such a mapping already exists", Toast.LENGTH_SHORT).show());
                                }
                            });
                }
            });

        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu menu = new PopupMenu(MappingsActivity.this, view);
            menu.getMenuInflater().inflate(R.menu.menu_item_options2, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete){
                    Cursor cursor = viewModel.getMappings().getValue();
                    cursor.moveToPosition(position);
                    viewModel.removeMapping(cursor.getLong(0), () -> viewModel.getAllMappings(() -> {
                        runOnUiThread(() -> Toast.makeText(MappingsActivity.this, "Mapping deleted", Toast.LENGTH_SHORT).show());
                    }));
                    return true;
                }

                return false;
            });

            menu.show();
            return false;
        });

    }

    private void initializeDialog(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_mapping_dialog);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
            window.setAttributes(params);
        }
    }

}