package com.example.prittercare.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ReservationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 예약 실행 시 토스트 메시지 표시
        String reservationName = intent.getStringExtra("reservation_name");
        String message = (reservationName != null && !reservationName.isEmpty())
                ? "예약 '" + reservationName + "'이(가) 실행되었습니다."
                : "예약이 실행되었습니다.";
    }
}
