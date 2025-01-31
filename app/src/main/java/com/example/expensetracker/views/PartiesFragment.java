package com.example.expensetracker.views;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.Pair;
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
import com.example.expensetracker.databinding.AddPartyDialogBinding;
import com.example.expensetracker.databinding.FragmentPartiesBinding;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.repository.displayEntities.ChartData;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.PartiesAdapter;
import com.example.expensetracker.views.customCallbacks.PartyMenuCallback;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PartiesFragment extends Fragment {
    private static final String TAG = "PartiesFragment";
    FragmentPartiesBinding binding;
    MainActivityViewModel viewModel;
    RecyclerView recyclerView;
    PartiesAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    Dialog dialog;
    HorizontalBarChart barChart;
    PartyMenuCallback partyMenuCallback;
    AddPartyDialogBinding dialogBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_parties, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TextView emptyView = binding.emptyTextView;

        // TODO: Empty textview

        partyMenuCallback = new PartyMenuCallback() {
            @Override
            public void onUpdate(Party party) {
                initializePartiesDialog();

                dialogBinding.setParty(party);
                dialogBinding.info.setText("Update party");
                dialogBinding.partyName.setHint("New name");
                dialogBinding.partyNickname.setHint("New nickname");
                dialogBinding.addPartyBtn.setText("Update Party");

                dialogBinding.addPartyBtn.setOnClickListener(v -> {
                    String name = dialogBinding.partyName.getText().toString();
                    String nickname = dialogBinding.partyNickname.getText().toString();

                    if (name.isEmpty() || name.matches("\\s+")){
                        Toast.makeText(getContext(), "Party name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (nickname.isEmpty() || nickname.matches("\\s+")) {
                        Toast.makeText(getContext(), "Party nickname cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Party newParty = new Party(name, nickname);
                        newParty.setId(party.getId());
                        viewModel.updateParty(newParty, new ErrorCallback() {
                            @Override
                            public void onSuccess() {
                                viewModel.getAllPartiesWithAmount(() -> getActivity().runOnUiThread(() -> dialog.dismiss()));
                            }

                            @Override
                            public void onError(Exception e) {
                                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Party with same name/nickname already exists", Toast.LENGTH_SHORT).show());
                            }
                        });

                    }
                });
            }

            @Override
            public void onDelete(Party party) {
                viewModel.removeParty(party.getId(), new Runnable() {
                    @Override
                    public void run() {
                        viewModel.getAllPartiesWithAmount(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Party deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };

        viewModel.getParties().observe(getViewLifecycleOwner(), new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                if (cursor != null){
                    if (adapter != null) adapter.swapCursor(cursor);
                    else {
                        adapter = new PartiesAdapter(cursor, partyMenuCallback);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
        });

        viewModel.getPartiesChartData().observe(getViewLifecycleOwner(), new Observer<ChartData>() {
            @Override
            public void onChanged(ChartData chartData) {
                if (chartData.getEntries() != null){
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
                    barChart.setDrawValueAboveBar(false);
                    barChart.setFitBars(true);
                    barChart.getDescription().setText("0 values excluded");
                    barChart.getDescription().setTextColor(Color.WHITE);
                    barChart.invalidate();
                }
            }
        });

        swipeRefreshLayout = binding.swipeRefreshLayout;
        barChart = binding.horizontalBarChart;

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.getAllPartiesWithAmount(() -> swipeRefreshLayout.setRefreshing(false));
        });

        fab = binding.fab;
        fab.setOnClickListener(v -> {
            initializePartiesDialog();

            Button addPartyBtn = dialog.findViewById(R.id.add_party_btn);

            addPartyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText edtName = dialog.findViewById(R.id.party_name);
                    EditText edtNickname = dialog.findViewById(R.id.party_nickname);

                    String partyName = edtName.getText().toString();
                    String partyNickname = edtNickname.getText().toString();

                    if (partyName.isEmpty() || partyName.matches("\\s+")){
                        Toast.makeText(getContext(), "Party name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (partyNickname.isEmpty() || partyNickname.matches("\\s+")) {
                        Toast.makeText(getContext(), "Party nickname cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        viewModel.addParty(new Party(partyName, partyNickname), new ErrorCallback() {
                            @Override
                            public void onSuccess() {
                                viewModel.getAllPartiesWithAmount(() -> getActivity().runOnUiThread(() -> dialog.dismiss()));
                            }

                            @Override
                            public void onError(Exception e) {
                                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Party with same name/nickname already exists", Toast.LENGTH_SHORT).show());
                            }
                        });
                        
                    }

                }
            });

        });

        return binding.getRoot();
    }

    private void initializePartiesDialog(){
        dialog = new Dialog(getContext());
        dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.add_party_dialog,
                null,
                false
        );
        dialog.setContentView(R.layout.add_party_dialog);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
            window.setAttributes(params);
        }
    }


}