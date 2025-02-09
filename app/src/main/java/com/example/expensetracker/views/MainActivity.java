package com.example.expensetracker.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.example.expensetracker.repository.displayEntities.Filter;
import com.example.expensetracker.services.SmsWatcher;
import com.example.expensetracker.viewmodels.MainActivityViewModel;
import com.example.expensetracker.views.adapters.RecordsAdapter;
import com.example.expensetracker.views.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    MainActivityViewModel viewModel;
    ActivityMainBinding binding;
    private final String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Toolbar toolbar;
    ImageButton filterBtn;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        if (preferences.getBoolean("First launch", true)){
            requestRuntimePermissions();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("First launch", false);
            editor.apply();
        }
        if (preferences.getBoolean(getString(R.string.persistent_notification_enabled), false)){
            startForegroundService(new Intent(getApplicationContext(), SmsWatcher.class));
        }

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        toolbar = binding.toolbar;
        filterBtn = binding.filterButton;

        filterBtn.setOnClickListener(v -> showFiltersDialog());

        bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setItemIconTintList(null);
        viewPager = binding.viewPager;
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new RecordsFragment());
        fragments.add(new CategoriesFragment());
        fragments.add(new PartiesAndAccountsFragment());
        fragments.add(new GoalsFragment());
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        viewPager.setAdapter(viewPagerAdapter);

        bottomNavigationView.setSelectedItemId(R.id.Records);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.settings){
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }

            return false;
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Records){
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (item.getItemId() == R.id.Categories) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (item.getItemId() == R.id.PartiesAndAccounts) {
                    viewPager.setCurrentItem(2);
                    return true;
                }
                else {
                    viewPager.setCurrentItem(3);
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
                        bottomNavigationView.setSelectedItemId(R.id.Records);
                        break;
                    }
                    case 1:{
                        bottomNavigationView.setSelectedItemId(R.id.Categories);
                        break;
                    }
                    case 2:{
                        bottomNavigationView.setSelectedItemId(R.id.PartiesAndAccounts);
                        break;
                    }
                    case 3:{
                        bottomNavigationView.setSelectedItemId(R.id.Goals);
                        break;
                    }
                }
            }
        });


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

    void showFiltersDialog(){
        Filter filter = viewModel.getFilterMutableLiveData().getValue();

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.filter_dialog);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT; // Full width
            window.setAttributes(params);
        }

        TextView start_date = dialog.findViewById(R.id.start_date);
        AtomicReference<String> selected_start_date = new AtomicReference<>();
        TextView end_date = dialog.findViewById(R.id.end_date);
        AtomicReference<String> selected_end_date = new AtomicReference<>();
        TextView start_time = dialog.findViewById(R.id.start_time);
        AtomicReference<String> selected_start_time = new AtomicReference<>();
        TextView end_time = dialog.findViewById(R.id.end_time);
        AtomicReference<String> selected_end_time = new AtomicReference<>();
        Spinner category = dialog.findViewById(R.id.category);
        Spinner party = dialog.findViewById(R.id.party);
        Spinner account = dialog.findViewById(R.id.account);
        Button setFilterBtn = dialog.findViewById(R.id.set_filter_btn);
        Button resetBtn = dialog.findViewById(R.id.reset_btn);

        // Date and time
        if (filter.getStart_date() != null) {
            start_date.setText(filter.getStart_date());
            selected_start_date.set(filter.getStart_date());
        }
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                datePickerDialog.show();

                datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                    selected_start_date.set(year + "-" + (((month + 1) < 10) ? "0" + (month+1) : (month+1)) + "-" +
                            ((dayOfMonth + 1 < 10) ? "0" + dayOfMonth : dayOfMonth));
                    Log.d(TAG, "showUpdateDialog: " + selected_start_date.get());
                    start_date.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                });
            }
        });

        if (filter.getEnd_date() != null) {
            end_date.setText(filter.getEnd_date());
            selected_end_date.set(filter.getEnd_date());
        }
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                datePickerDialog.show();

                datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                    selected_end_date.set(year + "-" + (((month + 1) < 10) ? "0" + (month+1) : (month+1)) + "-" +
                            ((dayOfMonth + 1 < 10) ? "0" + dayOfMonth : dayOfMonth));
                    Log.d(TAG, "showUpdateDialog: " + selected_end_date.get());
                    end_date.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                });
            }
        });

        if (filter.getStart_time() != null){
            start_time.setText(filter.getStart_time());
            selected_start_time.set(filter.getStart_time());
        }
        start_time.setOnClickListener(v13 -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, (view, hourOfDay, minute) -> {
                start_time.setText(hourOfDay % 12 + ":" + ((minute < 10) ? "0" + minute : minute) + ((hourOfDay < 12) ? " AM" : " PM"));
                selected_start_time.set(((hourOfDay < 10) ? "0" + hourOfDay : hourOfDay) + ":" + ((minute < 10) ? "0" + minute : minute) + ":" + "00");
            }, 12, 0, false);
            timePickerDialog.show();
        });

        if (filter.getEnd_time() != null){
            end_time.setText(filter.getEnd_time());
            selected_end_time.set(filter.getEnd_time());
        }
        start_time.setOnClickListener(v13 -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, (view, hourOfDay, minute) -> {
                end_time.setText(hourOfDay % 12 + ":" + ((minute < 10) ? "0" + minute : minute) + ((hourOfDay < 12) ? " AM" : " PM"));
                selected_end_time.set(((hourOfDay < 10) ? "0" + hourOfDay : hourOfDay) + ":" + ((minute < 10) ? "0" + minute : minute) + ":" + "00");
            }, 12, 0, false);
            timePickerDialog.show();
        });

        viewModel.getAllCategories(cursor1 -> {
            MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.CATEGORIES_NAME});
            defaultCursor.addRow(new Object[]{-1, "N/A"});
            MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor1});

            SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    MainActivity.this,
                    android.R.layout.simple_spinner_item,
                    mergedCursor,
                    new String[]{BudgetDB.CATEGORIES_NAME},
                    new int[] {android.R.id.text1},
                    0
            );

            MainActivity.this.runOnUiThread(() -> {
                category.setAdapter(cursorAdapter);

                if (filter.getCategory_id() == null) return;
                mergedCursor.moveToFirst();
                do {
                    if (Objects.equals(mergedCursor.getLong(0), filter.getCategory_id())) {
                        category.setSelection(mergedCursor.getPosition());
                    }
                } while(mergedCursor.moveToNext());
            });
        });

        viewModel.getAllParties(cursor1 -> {
            MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.PARTIES_NICKNAME});
            defaultCursor.addRow(new Object[]{-1, "N/A"});
            MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor1});

            SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    MainActivity.this,
                    android.R.layout.simple_spinner_item,
                    mergedCursor,
                    new String[]{BudgetDB.PARTIES_NICKNAME},
                    new int[] {android.R.id.text1},
                    0
            );

            MainActivity.this.runOnUiThread(() -> {
                party.setAdapter(cursorAdapter);

                if (filter.getParty_id() == null) return;
                mergedCursor.moveToFirst();
                do {
                    if (Objects.equals(mergedCursor.getLong(0), filter.getParty_id())) {
                        party.setSelection(mergedCursor.getPosition());
                    }
                } while(mergedCursor.moveToNext());
            });
        });

        viewModel.getAllAccounts(cursor1 -> {
            MatrixCursor defaultCursor = new MatrixCursor(new String[]{"_id", BudgetDB.ACCOUNTS_ACCOUNT_NO});
            defaultCursor.addRow(new Object[]{-1, "N/A"});
            MergeCursor mergedCursor = new MergeCursor(new Cursor[]{defaultCursor, cursor1});

            SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    MainActivity.this,
                    android.R.layout.simple_spinner_item,
                    mergedCursor,
                    new String[]{BudgetDB.ACCOUNTS_ACCOUNT_NO},
                    new int[] {android.R.id.text1},
                    0
            );

            MainActivity.this.runOnUiThread(() -> {
                account.setAdapter(cursorAdapter);

                if (filter.getAccount_id() == null) return;
                mergedCursor.moveToFirst();
                do {
                    if (Objects.equals(mergedCursor.getLong(0), filter.getAccount_id())) {
                        account.setSelection(mergedCursor.getPosition());
                    }
                } while(mergedCursor.moveToNext());
            });
        });

        setFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor selectedCategory = (Cursor) category.getSelectedItem();
                Cursor selectedParty = (Cursor) party.getSelectedItem();
                Cursor selectedAccount = (Cursor) account.getSelectedItem();


                Filter newFilter = new Filter();
                newFilter.setStart_date(selected_start_date.get());
                newFilter.setEnd_date(selected_end_date.get());
                newFilter.setStart_time(selected_start_time.get());
                newFilter.setEnd_time(selected_end_time.get());
                newFilter.setCategory_id((selectedCategory.getLong(0) == -1) ? null : selectedCategory.getLong(0));
                newFilter.setParty_id((selectedParty.getLong(0) == -1) ? null : selectedParty.getLong(0));
                newFilter.setAccount_id((selectedAccount.getLong(0) == -1) ? null : selectedAccount.getLong(0));

                viewModel.postToFilterMutableLiveData(newFilter);
                dialog.dismiss();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.postToFilterMutableLiveData(new Filter());
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE){
            for (int result : grantResults){
                if (result == PackageManager.PERMISSION_DENIED) return;
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.persistent_notification_enabled), true);
            editor.apply();
            startForegroundService(new Intent(getApplicationContext(), SmsWatcher.class));
        }

    }

    private void requestRuntimePermissions(){
        String[] permissions = {
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.POST_NOTIFICATIONS
        };

        for (String permission : permissions){
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{permission}, PERMISSION_REQUEST_CODE);
            }
        }
    }

}