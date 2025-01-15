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
import com.example.expensetracker.databinding.FragmentAccountsBinding;
import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.AccountsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AccountsFragment extends Fragment {
    FragmentAccountsBinding binding;
    MainActivityViewModel viewModel;
    ListView listView;
    AccountsAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_accounts, container, false);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        adapter = new AccountsAdapter(getContext(), viewModel.getAllAccountsWithAmount(), 0);
        listView = binding.recyclerView;
        listView.setAdapter(adapter);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        fab = binding.fab;

        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.swapCursor(viewModel.getAllAccountsWithAmount());
            swipeRefreshLayout.setRefreshing(false);
        });

        fab.setOnClickListener(v -> {
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.add_account_dialog);
            dialog.show();

            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
                window.setAttributes(params);
            }

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
                            getActivity().runOnUiThread(() -> dialog.dismiss());
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
}