package com.example.expensetracker.views;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentAccountsBinding;
import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.repository.displayEntities.ChartData;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.AccountsAdapter;
import com.example.expensetracker.views.customCallbacks.AccountMenuCallback;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AccountsFragment extends Fragment {
    private static final String TAG = "AccountsFragment";
    FragmentAccountsBinding binding;
    MainActivityViewModel viewModel;
    RecyclerView recyclerView;
    AccountsAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    Dialog dialog;
    HorizontalBarChart barChart;

    AccountMenuCallback menuCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_accounts, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        TextView emptyView = binding.emptyTextView;
        barChart = binding.horizontalBarChart;

        // TODO: empty recyclerview textview

        viewModel.getFilterMutableLiveData().observe(getViewLifecycleOwner(), filter -> viewModel.getAllAccountsWithAmount(() -> {
            // Do nothing
        }));

        menuCallback = new AccountMenuCallback() {
            @Override
            public void onDelete(Account account) {
                viewModel.removeAccount(account.getAccount_id(), () -> {
                    viewModel.getAllAccountsWithAmount(() -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show()));
                });
            }
        };

        viewModel.getAccounts().observe(getViewLifecycleOwner(), cursor -> {
            if (cursor != null){
                if (cursor.getCount() == 0) emptyView.setVisibility(View.VISIBLE);
                else emptyView.setVisibility(View.GONE);

                if (adapter != null) adapter.swapCursor(cursor);
                else {
                    adapter = new AccountsAdapter(cursor, menuCallback);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
        
        viewModel.getAccountsChartData().observe(getViewLifecycleOwner(), new Observer<ChartData>() {
            @Override
            public void onChanged(ChartData chartData) {
                barChart.clear();
                if (chartData.getEntries() != null && !chartData.getEntries().isEmpty()){
                    BarDataSet barDataSet = new BarDataSet(chartData.getEntries(), "Label");
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.WHITE);
                    barDataSet.setValueTextSize(12f);
                    BarData barData = new BarData(barDataSet);
                    barData.setBarWidth(0.75f);
                    barChart.setData(barData);

                    ViewGroup.LayoutParams params = barChart.getLayoutParams();
                    params.height = chartData.getEntries().size()*175;
                    barChart.setLayoutParams(params);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(chartData.getLabels()));
                    xAxis.setTextColor(Color.WHITE);
                    xAxis.setGranularity(1f);

                    YAxis yAxis = barChart.getAxisLeft();
                    yAxis.setTextColor(Color.WHITE);
                    yAxis.setGranularity(1f);
                    yAxis.setInverted(true);
                    yAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            if (value < 0) {
                                return "" + Math.abs((int) value); // Custom label for negative X values
                            } else if (value > 0) {
                                return "+" + (int) value; // Custom label for positive X values
                            } else {
                                return "0"; // Custom label for zero
                            }
                        }
                    });
                    yAxis.setAxisMinimum(chartData.getMinX() - (Math.abs(chartData.getMinX())*0.3f));
                    yAxis.setAxisMaximum(chartData.getMaxX() + (Math.abs(chartData.getMaxX())*0.3f));

                    barChart.getLegend().setEnabled(false);
                    barChart.getAxisRight().setEnabled(false);
                    barChart.setDrawValueAboveBar(true);
                    barChart.setFitBars(true);
                    barChart.setPinchZoom(false);
                    barChart.setDoubleTapToZoomEnabled(false);
                    barChart.getDescription().setText("0 values excluded");
                    barChart.getDescription().setTextColor(Color.WHITE);
                    barChart.invalidate();
                }
            }
        });



        swipeRefreshLayout = binding.swipeRefreshLayout;
        fab = binding.fab;

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.getAllAccountsWithAmount(() -> swipeRefreshLayout.setRefreshing(false));
        });

        fab.setOnClickListener(v -> {
            initializeAccountsDialog();

            Button addAccountBtn = dialog.findViewById(R.id.add_account_btn);

            addAccountBtn.setOnClickListener(v1 -> {
                EditText edtAccountNo = dialog.findViewById(R.id.account_no);
                String accountNo = edtAccountNo.getText().toString();

                if (accountNo.length() < 4)
                    Toast.makeText(getContext(), "Minimum 4 digits required", Toast.LENGTH_SHORT).show();
                else {
                    viewModel.addAccount(new Account("X" + accountNo), new ErrorCallback() {
                        @Override
                        public void onSuccess() {
                            viewModel.getAllAccountsWithAmount(() -> getActivity().runOnUiThread(() -> dialog.dismiss()));
                        }

                        @Override
                        public void onError(Exception e) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "No accounts with same number", Toast.LENGTH_SHORT).show());

                        }
                    });

                }
            });

        });

        return binding.getRoot();
    }

    private void initializeAccountsDialog(){
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.add_account_dialog);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
            window.setAttributes(params);
        }
    }

}