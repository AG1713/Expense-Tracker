package com.example.expensetracker.views;

import android.database.Cursor;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentRecordsBinding;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.RecordsAdapter;

public class RecordsFragment extends Fragment {
    FragmentRecordsBinding binding;
    MainActivityViewModel viewModel;
    RecordsAdapter adapter;
    Cursor cursor;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_records, container, false);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        cursor = viewModel.getAllRecords();
        adapter = new RecordsAdapter(getActivity(), cursor, 0);
        listView = binding.recyclerview;
        listView.setAdapter(adapter);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.swapCursor(viewModel.getAllRecords());
            swipeRefreshLayout.setRefreshing(false);
        });

        return binding.getRoot();
    }
}