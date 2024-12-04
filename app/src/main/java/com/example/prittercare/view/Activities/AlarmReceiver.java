package com.example.prittercare.view.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.MQTTHelper;
import com.example.prittercare.model.data.ReservationData;

/**
 * AlarmReceiver 클래스
 * 예약된 알람이 발생했을 때 실행되는 BroadcastReceiver입니다.
 * 예약 데이터를 기반으로 MQTT 메시지를 생성하고 전송합니다.
 */
public class AlarmReceiver extends BroadcastReceiver {

    // 태그: 디버깅 로그 출력을 위한 상수
    private static final String TAG = "AlarmReceiver";

    // MQTT 헬퍼 객체
    private MQTTHelper mqttHelper;

    /**
     * 알람 발생 시 호출되는 메서드
     *
     * @param context 애플리케이션의 Context 객체
     * @param intent  예약 데이터를 포함한 인텐트
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AlarmReceiver triggered"); // 알람 발생 로그 출력

        // 예약 데이터 가져오기
        ReservationData reservation = (ReservationData) intent.getSerializableExtra("reservation_data");
        if (reservation == null) {
            Log.e(TAG, "No reservation data provided"); // 예약 데이터가 없는 경우 로그 출력
            return; // 처리 중단
        }

        // MQTT 초기화
        mqttHelper = new MQTTHelper(
                context, // 애플리케이션 컨텍스트
                "tcp://medicine.p-e.kr:1884", // MQTT 브로커 주소
                "myClientId", // 클라이언트 ID
                "GuestMosquitto", // 사용자 이름
                "MosquittoGuest1119!" // 비밀번호
        );
        mqttHelper.initialize(); // MQTT 연결 초기화

        // MQTT 토픽 설정
        String topic = DataManager.getInstance().getUserName() + "/" + DataManager.getInstance() + "/light"; // 토픽 경로
        String payload = ""; // MQTT 메시지 페이로드

        // 예약 타입에 따른 MQTT 메시지 생성
        switch (reservation.getReserveType()) {
            case "water": // 물 예약
            case "food":  // 먹이 예약
                payload = "1"; // 물이나 먹이는 페이로드로 "1" 사용
                break;
            case "light": // 조명 예약
                payload = String.valueOf(reservation.getLightLevel()); // 조명 단계 값 사용
                break;
        }

        // MQTT 메시지 전송
        sendCommand(topic, payload, "예약 실행: " + reservation.getReserveName());
    }

    /**
     * MQTT 메시지를 전송하는 메서드
     *
     * @param topic        MQTT 토픽
     * @param message      MQTT 페이로드
     * @param successToast 성공 시 표시할 토스트 메시지
     */
    private void sendCommand(String topic, String message, String successToast) {
        // MQTT 연결 상태 확인
        if (mqttHelper != null && mqttHelper.isConnected()) {
            mqttHelper.publish(topic, message, 1); // MQTT 메시지 발행
            Log.d(TAG, "MQTT message sent to topic: " + topic + " with payload: " + message); // 디버깅 로그 출력
            showToast(successToast); // 성공 메시지 토스트 표시
        } else {
            Log.e(TAG, "MQTT connection failed"); // 연결 실패 로그 출력
            showToast("MQTT 연결 실패. 다시 시도해주세요."); // 실패 메시지 토스트 표시
        }
    }

    /**
     * 토스트 메시지를 표시하는 메서드
     *
     * @param message 표시할 메시지
     */
    private void showToast(String message) {
        // MQTT 컨텍스트를 사용하여 토스트 메시지 표시
        Toast.makeText(mqttHelper.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
