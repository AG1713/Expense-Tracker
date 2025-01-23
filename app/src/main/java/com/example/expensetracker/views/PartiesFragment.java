package com.example.expensetracker.views;

import android.app.Dialog;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.expensetracker.ErrorCallback;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentPartiesBinding;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.PartiesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PartiesFragment extends Fragment {
    FragmentPartiesBinding binding;
    MainActivityViewModel viewModel;
    ListView listView;
    PartiesAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.fragment_parties, container, false);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        listView = binding.listview;
        adapter = new PartiesAdapter(getContext(), viewModel.getAllPartiesWithAmount(), 0);
        listView.setAdapter(adapter);
        swipeRefreshLayout = binding.swipeRefreshLayout;

        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.swapCursor(viewModel.getAllPartiesWithAmount());
            swipeRefreshLayout.setRefreshing(false);
        });
        fab = binding.fab;

        fab.setOnClickListener(v -> {
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.add_party_dialog);
            dialog.show();

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
                window.setAttributes(params);
            }

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
                                getActivity().runOnUiThread(() -> dialog.dismiss());
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
}