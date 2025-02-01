package com.example.expensetracker.views;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ActivitySettingsBinding;
import com.example.expensetracker.services.SmsWatcher;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    SharedPreferences preferences;
    ActivitySettingsBinding binding;

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

        binding.presistentNotificationSetting.setChecked(preferences.getBoolean(getString(R.string.persistent_notification_enabled), false));
        binding.presistentNotificationSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(getString(R.string.persistent_notification_enabled), true);
                    editor.apply();
                    startForegroundService(new Intent(getApplicationContext(), SmsWatcher.class));
                }
                else {
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Disable Persistent Notification?")
                            .setMessage("The persistent notification helps track your UPI messages. Are you sure you want to disable it?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean(getString(R.string.persistent_notification_enabled), false);
                                editor.apply();

                                Intent i = new Intent(getApplicationContext(), SmsWatcher.class);
                                i.setAction("STOP_SERVICE");
                                startForegroundService(i);
                            })
                            .setNegativeButton("No", ((dialog, which) -> {
                                binding.presistentNotificationSetting.setChecked(true);
                            }))
                            .setCancelable(false)
                            .show();

                }
            }
        });




    }
}