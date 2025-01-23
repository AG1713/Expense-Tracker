package com.example.expensetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.expensetracker.repository.database.BudgetDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GoalsStatusWorker extends Worker {
    public GoalsStatusWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        SQLiteDatabase db = getApplicationContext().openOrCreateDatabase(BudgetDB.DATABASE_NAME, Context.MODE_PRIVATE, null);

        ContentValues values = new ContentValues();
        values.put(BudgetDB.GOALS_STATUS, "completed");
        db.update(BudgetDB.TABLE_GOALS, values, BudgetDB.GOALS_STATUS + " = ? AND " +
                BudgetDB.GOALS_END_DATE + " < ?", new String[]{"active", today});

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelID = "Goals";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(new NotificationChannel(channelID, "Goals", NotificationManager.IMPORTANCE_HIGH));
        }

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setSmallIcon(R.drawable.baseline_notifications_none_24)
                .setContentTitle("Goals")
                .setContentText("Goals are refreshed")
                .setSubText("ExpenseTracker")
                .build();

        nm.notify(2, notification); // Notify with ID 2, NOT 1, 1 is for persistent notification here

        return Result.success();
    }
}
