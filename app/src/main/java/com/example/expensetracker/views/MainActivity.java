package com.example.expensetracker.views;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ActivityMainBinding;
import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.repository.database.Category;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.repository.database.Record;
import com.example.expensetracker.services.SmsWatcher;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.RecordsAdapter;

public class MainActivity extends AppCompatActivity {
    MainActivityViewModel viewModel;
    ActivityMainBinding binding;
    ListView listView;
    RecordsAdapter adapter;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startForegroundService(new Intent(this, SmsWatcher.class));
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        cursor = viewModel.getAllRecords();
        adapter = new RecordsAdapter(this, cursor, 0);
//        deleteDatabase("BudgetDB");

        listView = binding.recyclerview;
        listView.setAdapter(adapter);



//        viewModel.addAccount(new Account("XX123"));
//        viewModel.addCategory(new Category("Snacks", null));
//        viewModel.addCategory(new Category("Tea", 1));
//        viewModel.addParty(new Party("Chill guy", "Chill guy (nickname)"));
//        viewModel.addParty(new Party("Not chill guy", "Not so chill"));
//        viewModel.addRecord(new Record("X1234", "2024-12-24", "02:00:00", "debited", 1000.00, 1, 1));
//        viewModel.addRecord(new Record("X1234", "2024-12-24", "03:28:00", "debited", 600.00, null, 1));
//        viewModel.addRecord(new Record("X1234", "2024-12-24", "03:28:00", "debited", 400.00, 1, null));
//        Party party = new Party("Chill guy 2", "NICKNAME");
//        party.setId(1);
//        Category category = new Category("Snacks2", null);
//        category.setId(2);
//        Record record = new Record("X1234", "2025-01-01", "12:29:00", "debited", 10.0, null, null);
//        record.setId(1);
//        viewModel.updateParty(party);
//        viewModel.updateCategory(category);
//        viewModel.updateRecord(record);

    }
}