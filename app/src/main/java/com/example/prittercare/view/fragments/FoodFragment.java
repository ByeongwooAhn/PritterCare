package com.example.prittercare.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.prittercare.R;
import com.example.prittercare.model.MQTTHelper;

public class FoodFragment extends Fragment {

    private MQTTHelper mqttHelper;

    // FoodFragment 생성자
    public static FoodFragment newInstance(MQTTHelper mqttHelper) {
        FoodFragment fragment = new FoodFragment();
        fragment.mqttHelper = mqttHelper;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_food, container, false);

        // 버튼 초기화
        LinearLayout feedFoodButton = rootView.findViewById(R.id.btn_feed_food);
        LinearLayout feedWaterButton = rootView.findViewById(R.id.btn_feed_water);

        // 리스너 설정
        feedFoodButton.setOnClickListener(v -> feedFood());
        feedWaterButton.setOnClickListener(v -> feedWater());

        return rootView;
    }

    // 먹이 공급
    private void feedFood() {
        sendCommand("feed/water/topic", "1", "먹이를 공급합니다.");
    }

    // 물 공급
    private void feedWater() {
        sendCommand("feed/water/topic", "1", "물을 공급합니다.");
    }

    // 공통 MQTT 메시지 전송 메서드
    private void sendCommand(String topic, String message, String successToast) {
        if (mqttHelper != null && mqttHelper.isConnected()) {
            mqttHelper.publish(topic, message, 1);
            Toast.makeText(requireContext(), successToast, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "MQTT 연결 실패, 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
