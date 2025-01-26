package com.example.expensetracker.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expensetracker.ErrorCallback;
import android.Manifest;
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
    private static final int PERMISSION_REQUEST_CODE = 123;
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

        checkPermissions();

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



//        viewModel.addAccount(new Account("XX123"), new ErrorCallback() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(MainActivity.this, "Default additions error", Toast.LENGTH_SHORT).show();
//            }
//        });
//        viewModel.addAccount(new Account("X1234"), new ErrorCallback() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(MainActivity.this, "Default additions error", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        viewModel.addParty(new Party("Chill guy", "Chill guy (nickname)"), new ErrorCallback() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(MainActivity.this, "Default additions error", Toast.LENGTH_SHORT).show();
//            }
//        });
//        viewModel.addParty(new Party("Not chill guy", "Not so chill"), new ErrorCallback() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(MainActivity.this, "Default additions error", Toast.LENGTH_SHORT).show();
//            }
//        });

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

//        viewModel.addRecord(new Record((long) 1, "2025-01-12", "02:00:00", "debited", 1000.00, (long) 1, null));
//        viewModel.addRecord(new Record((long) 1, "2024-12-24", "03:28:00", "debited", 600.00, null, (long) 1));
//        viewModel.addRecord(new Record((long) 1, "2024-12-24", "03:28:00", "debited", 400.00, (long) 1, null));


//        viewModel.addTransaction(new Record((long) 1, "2025-01-10", "06:03:00", "debited", 20.00, (long) 1, null)
//            , "Chill guy", "X1234");

        try {
            BudgetDB db = new BudgetDB(getApplicationContext());

        }
        catch (Exception e){
            Log.d(TAG, "onCreate: " + e.getMessage());
        }

    }

    private void checkPermissions() {
        // List of permissions to check
        String[] permissions = {
                Manifest.permission.POST_NOTIFICATIONS,  // Notification (Android 13+)
                Manifest.permission.RECEIVE_SMS,        // Receive SMS
                Manifest.permission.READ_SMS            // Read SMS
        };

        // List of permissions to request
        ArrayList<String> permissionsToRequest = new ArrayList<>();

        // Check each permission
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        // Request permissions if needed
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE
            );
        }
        else {
            startForegroundService(new Intent(this, SmsWatcher.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    startForegroundService(new Intent(this, SmsWatcher.class));
                } else {
                    // Permission denied
                    System.out.println("Permission denied: " + permissions[i]);
                }
            }
        }
    }


}