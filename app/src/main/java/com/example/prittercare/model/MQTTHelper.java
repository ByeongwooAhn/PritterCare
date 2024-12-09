package com.example.prittercare.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;

public class MQTTHelper implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String TAG = "MQTTHelper";

    private MqttClient mqttClient;
    private String serverUri;
    private String clientId;
    private String username;
    private String password;
    private boolean isReconnecting = false; // 중복 재연결 방지 플래그

    public MQTTHelper(Context context, String serverUri, String clientId, String username, String password) {
        this.serverUri = serverUri;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    public void initialize() {
        try {
            mqttClient = new MqttClient(serverUri, clientId, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(false); // 수동 재연결을 구현하기 위해 비활성화
            options.setConnectionTimeout(10); // 연결 시간 초과 설정
            options.setKeepAliveInterval(20); // Ping 패킷 전송 간격

            if (username != null && password != null) {
                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "MQTT 연결이 끊어졌습니다: " + cause.getMessage());
                    scheduleReconnect(); // 연결 끊김 시 재연결 시도
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    Log.d(TAG, "Message received: " + topic + " - " + message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Delivery complete");
                }
            });

            mqttClient.connect(options);
            Log.d(TAG, "MQTT 연결 성공");
        } catch (MqttException e) {
            Log.e(TAG, "초기 MQTT 연결 실패: " + e.getMessage(), e);
            scheduleReconnect(); // 초기 연결 실패 시에도 재연결 시도
        }
    }

    private void scheduleReconnect() {
        if (isReconnecting) return; // 이미 재연결 중이면 실행하지 않음
        isReconnecting = true;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                //Log.d(TAG, "MQTT 재연결 시도...");
                if (mqttClient != null) {
                    mqttClient.connect();
                    isReconnecting = false; // 재연결 성공 시 플래그 해제
                    Log.d(TAG, "MQTT 재연결 성공");
                }
            } catch (MqttException e) {
                Log.e(TAG, "MQTT 재연결 실패: " + e.getMessage(), e);
                isReconnecting = false; // 재연결 실패 시 다시 시도 가능하도록 플래그 초기화
                scheduleReconnect(); // 재연결 실패 시 다시 재연결 시도
            }
        }, 5000); // 5초 딜레이
    }

    public void subscribe(String topic) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.subscribe(topic);
                Log.d(TAG, "Subscribed to topic: " + topic);
            } else {
                Log.w(TAG, "MQTT client is not connected");
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error subscribing to topic: " + e.getMessage(), e);
        }
    }

    public void publish(String topic, String message, int qos) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(qos);
                mqttClient.publish(topic, mqttMessage);
                Log.d(TAG, "Message published to topic: " + topic);
            } else {
                Log.w(TAG, "MQTT client is not connected");
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error publishing message: " + e.getMessage(), e);
        }
    }

    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                Log.d(TAG, "MQTT disconnected successfully");
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error disconnecting MQTT: " + e.getMessage(), e);
        }
    }

    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }
}
