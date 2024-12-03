package com.example.prittercare.view.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.prittercare.model.data.ReservationData;

import java.util.Calendar;

/**
 * AlarmScheduler 클래스
 * 예약 데이터를 기반으로 알람을 설정하거나 취소하는 기능을 제공하는 유틸리티 클래스입니다.
 */
public class AlarmScheduler {

    // 태그: 디버깅 로그 출력을 위한 상수
    private static final String TAG = "AlarmScheduler";

    /**
     * 예약 데이터를 기반으로 알람을 설정합니다.
     *
     * @param context     애플리케이션의 Context 객체
     * @param reservation 예약 데이터 객체 (ReservationData)
     */
    public static void scheduleAlarm(Context context, ReservationData reservation) {
        // AlarmManager를 가져옴
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // AlarmReceiver를 실행하기 위한 Intent 생성
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reservation_data", reservation); // 예약 데이터 전달

        // PendingIntent를 생성하여 AlarmReceiver에 예약 데이터를 전달
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reservation.getId(), // 예약의 고유 ID를 사용하여 PendingIntent 구분
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 예약 시간 설정 (Hour, Minute를 Calendar 객체에 반영)
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, reservation.getHour());
        alarmTime.set(Calendar.MINUTE, reservation.getMinute());

        // 알람 실행 시간 계산
        long triggerTime = alarmTime.getTimeInMillis(); // 예약 시간의 밀리초 값
        long currentTime = System.currentTimeMillis(); // 현재 시간의 밀리초 값

        // 알람 시간이 현재 시간보다 과거일 경우, 알람 시간을 다음 날로 설정
        if (triggerTime <= currentTime) {
            triggerTime += AlarmManager.INTERVAL_DAY; // 하루(24시간)를 추가
        }

        // 디버깅 로그: 알람 설정 정보 출력
        Log.d(TAG, "Setting alarm for reservation: " + reservation.toString());
        Log.d(TAG, "Trigger time (ms): " + triggerTime + ", Current time (ms): " + currentTime);

        // 반복 여부를 확인하고 알람 설정
        if (reservation.getDayLoop() > 0) {
            // 일 단위 반복 알람 설정
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP, // 장치를 깨우는 알람
                        triggerTime, // 처음 알람 시간
                        AlarmManager.INTERVAL_DAY * reservation.getDayLoop(), // 반복 주기
                        pendingIntent // 알람 실행 시 PendingIntent 실행
                );
                Log.d(TAG, "Daily repeating alarm set for reservation ID: " + reservation.getId());
            } else {
                Log.e(TAG, "AlarmManager is null. Cannot set daily repeating alarm.");
            }
        } else if (reservation.getTimeLoop() > 0) {
            // 시간 단위 반복 알람 설정
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP, // 장치를 깨우는 알람
                        triggerTime, // 처음 알람 시간
                        AlarmManager.INTERVAL_HOUR * reservation.getTimeLoop(), // 반복 주기
                        pendingIntent // 알람 실행 시 PendingIntent 실행
                );
                Log.d(TAG, "Hourly repeating alarm set for reservation ID: " + reservation.getId());
            } else {
                Log.e(TAG, "AlarmManager is null. Cannot set hourly repeating alarm.");
            }
        } else {
            // 반복 없이 단일 알람 설정
            if (alarmManager != null) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, // 장치를 깨우는 알람
                        triggerTime, // 실행 시간
                        pendingIntent // 알람 실행 시 PendingIntent 실행
                );
                Log.d(TAG, "Single alarm set for reservation ID: " + reservation.getId());
            } else {
                Log.e(TAG, "AlarmManager is null. Cannot set single alarm.");
            }
        }
    }

    /**
     * 예약 ID를 기반으로 설정된 알람을 취소합니다.
     *
     * @param context       애플리케이션의 Context 객체
     * @param reservationId 예약의 고유 ID
     */
    public static void cancelAlarm(Context context, int reservationId) {
        // AlarmManager를 가져옴
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // AlarmReceiver를 실행하기 위한 Intent 생성
        Intent intent = new Intent(context, AlarmReceiver.class);

        // PendingIntent를 생성하여 예약된 알람 취소
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reservationId, // 예약 ID를 사용하여 구분
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // AlarmManager가 null이 아닌 경우, 알람 취소
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent); // 알람 취소
            Log.d(TAG, "Alarm cancelled for reservation ID: " + reservationId);
        } else {
            Log.e(TAG, "AlarmManager is null. Cannot cancel alarm.");
        }
    }
}
