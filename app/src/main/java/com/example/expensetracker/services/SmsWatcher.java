package com.example.expensetracker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.expensetracker.R;
import com.example.expensetracker.repository.Repository;

public class SmsWatcher extends Service {
    private static final String TAG = "SmsWatcher";

    Repository repository = new Repository(getApplication());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // For mandatory persistent notification
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .createNotificationChannel(new NotificationChannel(
                        getString(R.string.persistent_notification_channel),
                        getString(R.string.persistent_notification_channel),
                        NotificationManager.IMPORTANCE_LOW
                ));
        Notification.Builder notification = new Notification.Builder(this, getString(R.string.persistent_notification_channel))
                .setContentTitle("Content Title")
                .setContentText("Context Text")
                .setSubText("SubText")
                .setSmallIcon(R.drawable.baseline_notifications_none_24);

        startForeground(1, notification.build());
        Log.d(TAG, "Service Started");


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
