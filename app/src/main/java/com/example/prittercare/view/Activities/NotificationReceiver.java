package com.example.prittercare.view.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.prittercare.R;
import com.example.prittercare.view.Activities.ReservationActivity;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "prittercare_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmName = intent.getStringExtra("alarm_name");
        String alarmType = intent.getStringExtra("alarm_type");

        // 알림 채널 생성
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "PritterCare 알림",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // 알림 클릭 시 실행할 인텐트 설정
        Intent activityIntent = new Intent(context, ReservationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 알림 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                //.setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("예약 알림")
                .setContentText(alarmName + " (" + alarmType + ") 예약이 실행되었습니다.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(alarmName.hashCode(), builder.build());
    }
}