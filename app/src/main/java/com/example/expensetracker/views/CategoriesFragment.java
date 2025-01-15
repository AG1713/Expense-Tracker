package com.example.expensetracker.views;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentCategoriesBinding;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.CategoriesAdapter;

public class CategoriesFragment extends Fragment {
    FragmentCategoriesBinding binding;
    CategoriesAdapter adapter;
    MainActivityViewModel viewModel;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        swipeRefreshLayout = binding.swipeRefreshLayout;
        recyclerView = binding.recyclerView;

        adapter = new CategoriesAdapter(viewModel.getAllCategoriesInDFS());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.setCategoryDisplays(viewModel.getAllCategoriesInDFS());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}