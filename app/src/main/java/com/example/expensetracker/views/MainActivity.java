package com.example.expensetracker.views;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ActivityMainBinding;
import com.example.expensetracker.repository.database.Account;
import com.example.expensetracker.repository.database.BudgetDB;
import com.example.expensetracker.repository.database.Category;
import com.example.expensetracker.repository.database.Goal;
import com.example.expensetracker.repository.database.Mapping;
import com.example.expensetracker.repository.database.Party;
import com.example.expensetracker.repository.database.Record;
import com.example.expensetracker.services.SmsWatcher;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.RecordsAdapter;
import com.example.expensetracker.views.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MainActivityViewModel viewModel;
    ActivityMainBinding binding;
    private final String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startForegroundService(new Intent(this, SmsWatcher.class));
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        toolbar = binding.toolbar;

        bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setSelectedItemId(R.id.Records);
        viewPager = binding.viewPager;
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new PartiesAndAccountsFragment());
        fragments.add(new CategoriesFragment());
        fragments.add(new RecordsFragment());
        fragments.add(new GoalsFragment());
        fragments.add(new AnalysisFragment());
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setCurrentItem(2, false);
        bottomNavigationView.setSelectedItemId(R.id.Records);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Records){
                    viewPager.setCurrentItem(2);
                    return true;
                } else if (item.getItemId() == R.id.PartiesAndAccounts) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (item.getItemId() == R.id.Categories) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (item.getItemId() == R.id.Goals) {
                    viewPager.setCurrentItem(3);
                    return true;
                } else if (item.getItemId() == R.id.Analysis) {
                    viewPager.setCurrentItem(4);
                    return true;
                } else {
                    viewPager.setCurrentItem(2);
                    return true;
                }
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position){
                    case 0:{
                        bottomNavigationView.setSelectedItemId(R.id.PartiesAndAccounts);
                        break;
                    }
                    case 1:{
                        bottomNavigationView.setSelectedItemId(R.id.Categories);
                        break;
                    }
                    case 2:{
                        bottomNavigationView.setSelectedItemId(R.id.Records);
                        break;
                    }
                    case 3:{
                        bottomNavigationView.setSelectedItemId(R.id.Goals);
                        break;
                    }
                    case 4:{
                        bottomNavigationView.setSelectedItemId(R.id.Analysis);
                        break;
                    }
                }
            }
        });


//        deleteDatabase("BudgetDB");



//        viewModel.addAccount(new Account("XX123"));
//        viewModel.addAccount(new Account("X1234"));

//        viewModel.addCategory(new Category("Snacks", null));
//        viewModel.addCategory(new Category("Coffee", (long) 1));
//        viewModel.addCategory(new Category("Tea", (long) 1));
//        viewModel.addCategory(new Category("Entertainment", null));
//        viewModel.addCategory(new Category("Online", (long) 4));
//        viewModel.addCategory(new Category("Offline", (long) 4));
//        viewModel.addCategory(new Category("Play", (long) 6));
//        viewModel.addCategory(new Category("Theatre", (long) 6));
//        viewModel.addCategory(new Category("Netflix", (long) 5));
//        viewModel.addCategory(new Category("Hotstar", (long) 5));


//        viewModel.addParty(new Party("Chill guy", "Chill guy (nickname)"));
//        viewModel.addParty(new Party("Not chill guy", "Not so chill"));
//        viewModel.addRecord(new Record((long) 1, "2025-01-12", "02:00:00", "debited", 1000.00, (long) 1, null));
//        viewModel.addRecord(new Record("X1234", "2024-12-24", "03:28:00", "debited", 600.00, null, (long) 1));
//        viewModel.addRecord(new Record("X1234", "2024-12-24", "03:28:00", "debited", 400.00, (long) 1, null));

//        Party party = new Party("Chill guy 2", "NICKNAME");
//        party.setId(1);
//        Category category = new Category("Snacks2", null);
//        category.setId(2);
//        Record record = new Record((long) 1, "2025-01-01", "12:29:00", "debited", 10.0, null, null);
//        record.setId(1);
//        viewModel.updateParty(party);
//        viewModel.updateCategory(category);
//        viewModel.updateRecord(record);

//        viewModel.removeParty(4);

//        viewModel.addTransaction(new Record("X1234", "2025-01-10", "06:03:00", "debited", 20.00, (long) 1, null)
//            , "Chill guy", "X1234");

        try {
            BudgetDB db = new BudgetDB(getApplicationContext());
//            db.insertMapping(new Mapping((long) 1, 20.0, 1));
//            db.addTransaction(new Record((long) 1, "2025-01-14", "08:57:00", "debited", 20.00, (long) 1, null)
//            , "Chill guy", "X1234");
//            db.addTransaction(new Record((long) 2, "2025-01-11", "08:57:00", "debited", 150.00, null, null)
//            , "Chill guy", "X1234");

//            db.insertGoal(new Goal("First", (long) 9, 7000, 10, "2025-01-16", "2025-01-19", "active"));
//            db.insertGoal(new Goal("Second", (long) 5, 7000, 10, "2025-01-16", "2025-01-19", "active"));
//            db.insertGoal(new Goal("Third", null, 7000, 10, "2025-01-16", "2025-01-19", "active"));

//            db.insertGoal(new Goal("Fourth", (long) 9, 7000, 10, "2025-01-16", "2025-01-21", "active"));
//            db.insertGoal(new Goal("Seventh", (long) 5, 7000, 10, "2025-01-16", "2025-01-21", "active"));
//            db.insertGoal(new Goal("Sixth", null, 7000, 10, "2025-01-16", "2025-01-21", "active"));

//            db.addExpenseOnGoal(9, 10000);
//            db.reviseGoalsStatus();
//            db.subtractExpenseOnGoal(5, 300);

//
//            db.getAllGoals();

//            db.getCategoriesInDFS();

        }
        catch (Exception e){
            Log.d(TAG, "onCreate: " + e.getMessage());
        }



    }
}