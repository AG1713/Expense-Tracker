package com.example.expensetracker.views;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.expensetracker.databinding.FragmentPartiesBinding;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.PartiesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.function.Consumer;

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
        viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        listView = binding.listview;
        viewModel.getParties().observe(getViewLifecycleOwner(), new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                if (cursor != null){
                    if (adapter != null) adapter.swapCursor(cursor);
                    else {
                        adapter = new PartiesAdapter(getContext(), cursor, 0);
                        listView.setAdapter(adapter);
                    }
                }
            }
        });

        swipeRefreshLayout = binding.swipeRefreshLayout;

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


        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu menu = new PopupMenu(getContext(), view);
            menu.getMenuInflater().inflate(R.menu.menu_item_options1, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_update){
                    initializePartiesDialog();
                    TextView info = dialog.findViewById(R.id.info);
                    EditText partyName = dialog.findViewById(R.id.party_name);
                    EditText partyNickname = dialog.findViewById(R.id.party_nickname);
                    Button updatePartyBtn = dialog.findViewById(R.id.add_party_btn);

                    Cursor selectedParty = viewModel.getParties().getValue();
                    selectedParty.moveToPosition(position);

                    info.setText("Update party");
                    partyName.setHint("New name");
                    partyName.setText(selectedParty.getString(1));
                    partyNickname.setHint("New nickname");
                    partyNickname.setText(selectedParty.getString(2));
                    updatePartyBtn.setText("Update Party");

                    updatePartyBtn.setOnClickListener(v -> {
                        String name = partyName.getText().toString();
                        String nickname = partyNickname.getText().toString();

                        if (name.isEmpty() || name.matches("\\s+")){
                            Toast.makeText(getContext(), "Party name cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (nickname.isEmpty() || nickname.matches("\\s+")) {
                            Toast.makeText(getContext(), "Party nickname cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            Party party = new Party(name, nickname);
                            party.setId(selectedParty.getLong(0));
                            viewModel.updateParty(party, new ErrorCallback() {
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
                    });



                    return true;
                }
                else if (item.getItemId() == R.id.menu_delete){
                    Cursor selectedParty = viewModel.getParties().getValue();
                    selectedParty.moveToPosition(position);
                    viewModel.removeParty(selectedParty.getLong(0));
                    return true;
                }
                else return false;
            });


            menu.show();
            return false;
        });

        return binding.getRoot();
    }

    private void initializePartiesDialog(){
        dialog = new Dialog(getContext());
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