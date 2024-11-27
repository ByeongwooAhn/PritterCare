package com.example.prittercare.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.prittercare.R;
import com.example.prittercare.model.MQTTHelper;

public class LightFragment extends Fragment {

    // UI 요소
    private TextView lightLevelText;
    private View containerLightLevel;

    // 조명 상태
    private int currentLightLevel = 1;
    private int maxLightLevel = 5;
    private boolean isLightOn = true;

    private MQTTHelper mqttHelper;

    public static LightFragment newInstance(MQTTHelper mqttHelper) {
        LightFragment fragment = new LightFragment();
        fragment.mqttHelper = mqttHelper;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_light, container, false);

        // 초기화 및 설정
        initializeViews(rootView);
        setupListeners(rootView);

        // UI 초기 상태
        updateUI();
        return rootView;
    }

    // 뷰 초기화
    private void initializeViews(View rootView) {
        lightLevelText = rootView.findViewById(R.id.tv_light_level);
        containerLightLevel = rootView.findViewById(R.id.container_light_level);
    }

    // 이벤트 리스너 설정
    private void setupListeners(View rootView) {
        rootView.findViewById(R.id.btn_light_on).setOnClickListener(view -> turnLightOn());
        rootView.findViewById(R.id.btn_light_off).setOnClickListener(view -> turnLightOff());
        rootView.findViewById(R.id.btn_left).setOnClickListener(view -> adjustLightLevel(-1));
        rootView.findViewById(R.id.btn_right).setOnClickListener(view -> adjustLightLevel(1));
    }

    // 조명을 켜는 메서드
    private void turnLightOn() {
        isLightOn = true;
        updateUI();
        sendCommand("light/topic", String.valueOf(currentLightLevel), "조명 ON");
    }

    // 조명을 끄는 메서드
    private void turnLightOff() {
        isLightOn = false;
        updateUI();
        sendCommand("light/topic", "0", "조명 OFF");
    }

    // 조명 단계를 조정하는 메서드
    private void adjustLightLevel(int delta) {
        if (!isLightOn) {
            showToast("조명이 꺼져있습니다.");
            return;
        }

        int newLevel = currentLightLevel + delta;
        if (newLevel < 1 || newLevel > maxLightLevel) {
            showToast("조명 단계는 1~5 사이의 값이어야 합니다.");
        } else {
            currentLightLevel = newLevel;
            updateUI();
            sendCommand("light/topic", String.valueOf(currentLightLevel), "조명 단계: " + currentLightLevel);
        }
    }

    // 조명 레벨을 UI에 업데이트
    private void updateUI() {
        if (isLightOn) {
            lightLevelText.setText(currentLightLevel + " 단계");
            lightLevelText.setTextColor(getResources().getColor(getColorForLightLevel(currentLightLevel)));
            containerLightLevel.setBackgroundResource(getContainerColorForLightLevel(currentLightLevel));
        } else {
            lightLevelText.setText("조명 꺼짐");
            lightLevelText.setTextColor(getResources().getColor(R.color.white));
            containerLightLevel.setBackgroundResource(R.drawable.shape_card_main_light_off);
        }
    }

    // MQTT 메시지 전송
    private void sendCommand(String topic, String message, String successToast) {
        if (mqttHelper != null && mqttHelper.isConnected()) {
            mqttHelper.publish(topic, message, 1);
            showToast(successToast);
        } else {
            showToast("MQTT 연결 실패. 다시 시도해주세요.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // UI 업데이트: 현재 조명 상태와 단게에 따라
    private int getColorForLightLevel(int level) {
        return switch (level) {
            case 4, 5 -> R.color.black;
            default -> R.color.white;
        };
    }

    // 단계에 따른 배경 리소스 반환
    private int getContainerColorForLightLevel(int level) {
        return switch (level) {
            case 1 -> R.drawable.shape_card_main_light_level1;
            case 2 -> R.drawable.shape_card_main_light_level2;
            case 3 -> R.drawable.shape_card_main_light_level3;
            case 4 -> R.drawable.shape_card_main_light_level4;
            case 5 -> R.drawable.shape_card_main_light_level5;
            default -> R.drawable.shape_card_main_light_level1;
        };
    }
}
