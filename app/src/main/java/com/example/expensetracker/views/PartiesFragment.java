package com.example.expensetracker.views;

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
import com.example.expensetracker.databinding.FragmentPartiesBinding;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.PartiesAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PartiesFragment extends Fragment {
    FragmentPartiesBinding binding;
    MainActivityViewModel viewModel;
    ListView listView;
    PartiesAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_parties, container, false);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        listView = binding.listview;
        adapter = new PartiesAdapter(getContext(), viewModel.getALlPartiesWithAmount(), 0);
        listView.setAdapter(adapter);
        swipeRefreshLayout = binding.swipeRefreshLayout;

        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.swapCursor(viewModel.getALlPartiesWithAmount());
            swipeRefreshLayout.setRefreshing(false);
        });

        return binding.getRoot();
    }
}