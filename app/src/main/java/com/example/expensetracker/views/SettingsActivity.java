package com.example.expensetracker.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ActivitySettingsBinding;
import com.example.expensetracker.services.SmsWatcher;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String TAG = "SettingsActivity";
    SharedPreferences preferences;
    ActivitySettingsBinding binding;

    View.OnClickListener layoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
//        if (checkPermissions()) binding.persistentNotificationSetting.setEnabled(true);
//        layoutListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!checkPermissions()){
//                    requestRuntimePermissions();
//                    if (!checkPermissions()) launchSettings();
//                }
//            }
//        };

        if (preferences.getBoolean(getString(R.string.persistent_notification_enabled), false)){
            binding.persistentNotificationSetting.setChecked(true);
        }
        else binding.persistentNotificationSetting.setChecked(false);
        binding.persistentNotificationSettingLayout.setOnClickListener(layoutListener);

        binding.persistentNotificationSetting.setChecked(preferences.getBoolean(getString(R.string.persistent_notification_enabled), false));
        binding.persistentNotificationSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(getString(R.string.persistent_notification_enabled), true);
                    editor.apply();
                }
                else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(getString(R.string.persistent_notification_enabled), false);
                    editor.apply();
                }
            }
        });




    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_REQUEST_CODE){
//            for (int result : grantResults){
//                if (result == PackageManager.PERMISSION_DENIED){
//                    binding.persistentNotificationSetting.setChecked(false);
//                    return;
//                }
//            }
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putBoolean(getString(R.string.persistent_notification_enabled), true);
//            editor.apply();
//            Log.d(TAG, "CHECK");
//            startForegroundService(new Intent(getApplicationContext(), SmsWatcher.class));
//            binding.persistentNotificationSetting.setEnabled(true);
//        }
//
//    }
//
//    private void requestRuntimePermissions(){
//        String[] permissions = {
//                Manifest.permission.RECEIVE_SMS,
//                Manifest.permission.READ_SMS,
//                Manifest.permission.POST_NOTIFICATIONS
//        };
//        List<String> neededPermissions = new ArrayList<>();
//
//        for (String permission : permissions){
//            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED){
//                neededPermissions.add(permission);
//            }
//        }
//
//        Log.d(TAG, "requestRuntimePermissions: " + !neededPermissions.isEmpty());
//        if (!neededPermissions.isEmpty()){
//            requestPermissions(neededPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
//        }
//        else {
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putBoolean(getString(R.string.persistent_notification_enabled), true);
//            editor.apply();
//            startForegroundService(new Intent(getApplicationContext(), SmsWatcher.class));
//        }
//    }
//
//    private boolean checkPermissions(){
//        String[] permissions = {
//                Manifest.permission.RECEIVE_SMS,
//                Manifest.permission.READ_SMS,
//                Manifest.permission.POST_NOTIFICATIONS
//        };
//
//        for (String permission : permissions){
//            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED){
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    private void launchSettings(){
//        String[] permissions = {
//                Manifest.permission.RECEIVE_SMS,
//                Manifest.permission.READ_SMS,
//                Manifest.permission.POST_NOTIFICATIONS
//        };
//
//        for (String permission : permissions){
//            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED){
//                new AlertDialog.Builder(this)
//                        .setTitle("Permissions denied")
//                        .setMessage("You have to enable settings manually")
//                        .setPositiveButton("Go to settings", (dialog, which) -> {
//                            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            i.setData(android.net.Uri.parse("package:" + getPackageName()));
//                            startActivity(i);
//                        })
//                        .setNegativeButton("Cancel", (dialog, which) -> Log.d(TAG, "Permission denied!"))
//                        .show();
//            }
//        }
//    }
//
//
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        if (checkPermissions()) {
//            binding.persistentNotificationSetting.setEnabled(true);
//            binding.persistentNotificationSetting.setChecked(true);
//        }
//    }
}