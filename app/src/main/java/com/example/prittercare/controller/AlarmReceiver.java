package com.example.prittercare.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmName = intent.getStringExtra("alarm_name");
        Toast.makeText(context, "알람: " + (alarmName == null ? "기본 알람" : alarmName), Toast.LENGTH_LONG).show();
    }
}
