package com.example.expensetracker.views;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        listView = binding.recyclerView;
        viewModel.getAccounts().observe(getViewLifecycleOwner(), cursor -> {
            if (cursor != null){
                if (adapter != null) adapter.swapCursor(cursor);
                else {
                    adapter = new AccountsAdapter(getContext(), cursor, 0);
                    listView.setAdapter(adapter);
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

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu menu = new PopupMenu(getContext(), view);
            menu.getMenuInflater().inflate(R.menu.menu_item_options2, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete){
                    Cursor selectedAccount = viewModel.getAccounts().getValue();
                    selectedAccount.moveToPosition(position);
                    viewModel.removeAccount(selectedAccount.getLong(0));
                    return true;
                }
                else return false;
            });

            menu.show();
            return true;
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