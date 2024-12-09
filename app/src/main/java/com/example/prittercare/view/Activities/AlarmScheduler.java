package com.example.prittercare.view.Activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.example.prittercare.model.data.ReservationData;
import java.util.Calendar;

public class AlarmScheduler {
    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleAlarm(Context context, ReservationData alarm) {
        // 알람 시간 설정
        Calendar calendar = Calendar.getInstance();
        String[] timeParts = alarm.getReserveTime().replace("오전 ", "").replace("오후 ", "").split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        if (alarm.getReserveTime().startsWith("오후") && hour != 12) {
            hour += 12;
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 예약 시간 이전이면 다음 날로 설정
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        // 알람 매니저 설정
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("alarm_name", alarm.getReserveName());
        intent.putExtra("alarm_type", alarm.getReserveType());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.hashCode(), // 고유한 ID를 생성
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}