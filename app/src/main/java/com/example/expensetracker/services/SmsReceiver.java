package com.example.expensetracker.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.expensetracker.repository.Repository;
import com.example.expensetracker.repository.database.Record;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Repository repository = new Repository(context.getApplicationContext());
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        String format = bundle.getString("format");
        Object[] smsObj = (Object[]) bundle.get("pdus");

        for (Object obj : smsObj){
            SmsMessage message = SmsMessage.createFromPdu((byte[]) obj, format);

            // Now begins the main sms parsing part
            Log.d(TAG, "Message: " + message.getMessageBody());

            String regex = "(\\d+).*?" +
                    "(credited|debited).*?" +
                    "(\\d+\\.\\d+|\\d+).*?" +
                    "(\\d+-?\\w+-?\\d+).*?" +
                    "(\\d+:\\d+:\\d+|).*?" +
                    "(?:to|by|from|;)\\s+(.+?)\\s+(?:ref ?no|rrn|credited|debited|upi|\\.)";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message.getMessageBody());

            if (matcher.find()) {
                Record record = new Record(
                        (matcher.group(1).length() == 4) ? "X" + matcher.group(1) : "XX" + matcher.group(1), // Assuming at minimum there are last 3 digits, and at max 4
                        parseDate(matcher.group(4)),
                        (matcher.group(5).isEmpty()) ? new SimpleDateFormat("HH:mm:ss").format(message.getTimestampMillis()) : matcher.group(5),
                        matcher.group(2).toLowerCase(),
                        Double.valueOf(matcher.group(3)),
                        null,
                        null
                );

                repository.addRecord(record);
                Log.d(TAG, "onReceive: " + message.getMessageBody());
            }

        }

    }

    private String parseDate(String s){
        /*
        Possible formats:
        DD-MM-YYYY
        DD-Short name of month-YYYY
        DDShort name of the monthYYYY (Note there are no spaces)
        */
        String date = "";

        if (s.contains("-")){
            String[] parts = s.split("-");
            if (!parts[1].matches("\\d+")){
                parts[1] = getMonth(parts[1]);
            }
            if (parts[2].length() == 2) parts[2] = "20" + parts[2];
            date = parts[2] +"-"+ parts[1] +"-"+ parts[0];
        }
        else {
            // No '-'s
            String year = s.substring(5);
            if (s.length() == 2) s = "20" + s.substring(5);
            if (s.substring(5).length() == 2) s = s.substring(0, 5) + year;
            date = s.substring(5) +"-"+ getMonth(s.substring(2, 5)) +"-"+ s.substring(0, 2);
        }
        
        return date;
    }

    private String getMonth(String monthName){
        switch (monthName) {
            case "Jan": {
                return "01";
            }
            case "Feb": {
                return "02";
            }
            case "Mar": {
                return "03";
            }
            case "Apr": {
                return "04";
            }
            case "May": {
                return "05";
            }
            case "Jun": {
                return "06";
            }
            case "Jul": {
                return "07";
            }
            case "Aug": {
                return "08";
            }
            case "Sep": {
                return "09";
            }
            case "Oct": {
                return "10";
            }
            case "Nov": {
                return "11";
            }
            case "Dec": {
                return "12";
            }
        }
        return "01";
    }


}
