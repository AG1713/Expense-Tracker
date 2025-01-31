package com.example.expensetracker.views;

import android.app.Dialog;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.views.customCallbacks.CategoryMenuCallback;
import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentCategoriesBinding;
import com.example.expensetracker.repository.database.BudgetDB;
import com.example.expensetracker.repository.database.Category;
import com.example.expensetracker.repository.displayEntities.CategoryDisplay;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.CategoriesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.function.Consumer;

public class CategoriesFragment extends Fragment {
    FragmentCategoriesBinding binding;
    CategoriesAdapter adapter;
    MainActivityViewModel viewModel;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    Dialog dialog;
    private final String TAG = "CategoriesFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        swipeRefreshLayout = binding.swipeRefreshLayout;
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        fab = binding.fab;

        viewModel.getCategories().observe(getViewLifecycleOwner(), categoryDisplays -> {
            if (categoryDisplays != null){
                if (adapter!= null) adapter.setCategoryDisplays(categoryDisplays);
                else {
                    adapter = new CategoriesAdapter(categoryDisplays, new CategoryMenuCallback() {
                        @Override
                        public void onUpdate(CategoryDisplay categoryDisplay) {
                            dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.add_category_dialog);
                            dialog.show();

                            Window window = dialog.getWindow();
                            if (window != null) {
                                WindowManager.LayoutParams params = window.getAttributes();
                                params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
                                window.setAttributes(params);
                            }

                            TextView info = dialog.findViewById(R.id.info);
                            EditText edtCategoryName = dialog.findViewById(R.id.category_name);
                            Button addCategoryBtn = dialog.findViewById(R.id.add_category_btn);
                            Spinner spinner = dialog.findViewById(R.id.spinner);

                            viewModel.getAllCategories(cursor -> {
                                MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.CATEGORIES_NAME});
                                defaultCursor.addRow(new Object[]{-1, "Select an option"});
                                MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor});

                                SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                                        getContext(),
                                        android.R.layout.simple_spinner_item, // this is the sample layout provided
                                        mergedCursor,
                                        new String[]{BudgetDB.CATEGORIES_NAME}, // this is the sample layout provided
                                        new int[] {android.R.id.text1},
                                        0
                                );

                                getActivity().runOnUiThread(() -> {
                                    spinner.setAdapter(cursorAdapter);

                                    mergedCursor.moveToFirst();
                                    do {
                                        if (Objects.equals(mergedCursor.getLong(0), categoryDisplay.getCategory().getParent_id())) {
                                            spinner.setSelection(mergedCursor.getPosition());
                                        }
                                    } while(mergedCursor.moveToNext());
                                });

                            });


                            addCategoryBtn.setOnClickListener(v1 -> {
                                String categoryName = edtCategoryName.getText().toString();

                                Cursor selectedItem = (Cursor) spinner.getSelectedItem();

                                int _idIndex = selectedItem.getColumnIndex("_id");
                                long categoryId = selectedItem.getLong(_idIndex);

                                Log.d(TAG, "onCreateView: " + categoryId);

                                if (categoryName.isEmpty() || categoryName.matches("\\s+")){
                                    Toast.makeText(getContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                                }
                                else if (categoryId == -1) {
                                    viewModel.addCategory(new Category(categoryName, null));
                                    dialog.dismiss();
                                }
                                else {
                                    Category category = new Category(edtCategoryName.getText().toString(), categoryId);
                                    category.setId(categoryDisplay.getCategory().getId());
                                    viewModel.updateCategory(category, new ErrorCallback() {
                                        @Override
                                        public void onSuccess() {
                                            viewModel.getAllCategoriesInDFS(() -> dialog.dismiss());
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Category with same name exists", Toast.LENGTH_SHORT).show());
                                        }
                                    });
                                    
                                }

                            });
                            
                            info.setText("Update Category");
                            edtCategoryName.setText(categoryDisplay.getCategory().getName());
                            addCategoryBtn.setText("Update Category");
                            
                        }

                        @Override
                        public void onDelete(CategoryDisplay categoryDisplay) {
                            viewModel.removeCategory(categoryDisplay.getCategory().getId(), () -> viewModel.getAllCategoriesInDFS(() -> getActivity().runOnUiThread(() -> dialog.dismiss())));
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() ->
                viewModel.getAllCategoriesInDFS(() -> {
            swipeRefreshLayout.setRefreshing(false);
        }));

        fab.setOnClickListener(v -> {
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.add_category_dialog);
            dialog.show();

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
                window.setAttributes(params);
            }

            Button addCategoryBtn = dialog.findViewById(R.id.add_category_btn);
            Spinner spinner = dialog.findViewById(R.id.spinner);

            viewModel.getAllCategories(new Consumer<Cursor>() {
                @Override
                public void accept(Cursor cursor) {
                    MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.CATEGORIES_NAME});
                    defaultCursor.addRow(new Object[]{-1, "Select an option"});
                    MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor});

                    SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                            getContext(),
                            android.R.layout.simple_spinner_item, // this is the sample layout provided
                            mergedCursor,
                            new String[]{BudgetDB.CATEGORIES_NAME}, // this is the sample layout provided
                            new int[] {android.R.id.text1},
                            0
                    );

                    getActivity().runOnUiThread(() -> {
                        spinner.setAdapter(cursorAdapter);
                    });

                }
            });


            addCategoryBtn.setOnClickListener(v1 -> {
                EditText edtCategoryName = dialog.findViewById(R.id.category_name);
                String categoryName = edtCategoryName.getText().toString();

                Cursor selectedItem = (Cursor) spinner.getSelectedItem();

                int _idIndex = selectedItem.getColumnIndex("_id");
                long categoryId = selectedItem.getLong(_idIndex);

                Log.d(TAG, "onCreateView: " + categoryId);

                if (categoryName.isEmpty() || categoryName.matches("\\s+")){
                    Toast.makeText(getContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if (categoryId == -1) {
                    viewModel.addCategory(new Category(categoryName, null));
                    dialog.dismiss();
                }
                else {
                    viewModel.addCategory(new Category(categoryName, categoryId));
                    dialog.dismiss();
                }

            });


        });



        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}