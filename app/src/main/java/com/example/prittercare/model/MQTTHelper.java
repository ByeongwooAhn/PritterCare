package com.example.prittercare.model;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTHelper {
    private static final String TAG = "MQTTHelper";
    private MqttClient mqttClient;
    private String serverUri;
    private String clientId;
    private String username;
    private String password;

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
            if (username != null && password != null) {
                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }
            mqttClient.connect(options);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    Log.d(TAG, "Message received: " + message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Delivery complete");
                }
            });

            Log.d(TAG, "MQTT initialized successfully");
        } catch (MqttException e) {
            Log.e(TAG, "Error initializing MQTT: " + e.getMessage(), e);
        }
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
}