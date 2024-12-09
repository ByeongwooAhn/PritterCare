package com.example.prittercare.view.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.prittercare.R;
import com.example.prittercare.controller.StyleManager;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.MQTTHelper;

public class LightFragment extends Fragment {

    // UI 요소
    private LinearLayout lightContainerLayout;

    private ImageView lightLevelIcon;
    private TextView lightLevelText;

    private ImageButton leftButton, rightButton;
    private SeekBar lightStepSeekBar;

    private Button lightSetButton;

    // 조명 상태
    private int maxLightLevel = 5;

    private MQTTHelper mqttHelper;
    private StyleManager styleManager;

    // 사용자 및 장치 정보
    private String userid;
    private String serialnumber;
    private String animalType;
    private int currentLightLevel = 1;


    // MQTT 토픽
    private String LIGHT_TOPIC;

    public static LightFragment newInstance(MQTTHelper mqttHelper) {
        LightFragment fragment = new LightFragment();
        fragment.mqttHelper = mqttHelper;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_light, container, false);

        userid = DataManager.getInstance().getUserName();
        serialnumber = DataManager.getInstance().getCurrentCageSerialNumber();
        animalType = DataManager.getInstance().getCurrentAnimalType();

        // MQTT 토픽 초기화
        LIGHT_TOPIC = "${userid}/${serialnumber}/light".replace("${userid}", userid).replace("${serialnumber}", serialnumber);

        // 초기화 및 설정
        initializeViews(rootView);
        applyAnimalStyle(getContext());
        setupListeners(rootView);

        // UI 초기 상태
        return rootView;
    }

    // 뷰 초기화
    private void initializeViews(View rootView) {
        lightContainerLayout = rootView.findViewById(R.id.layout_container_light);

        lightLevelText = rootView.findViewById(R.id.tv_light_level);
        lightLevelIcon = rootView.findViewById(R.id.iv_ic_light_level);

        leftButton = rootView.findViewById(R.id.btn_left);
        rightButton = rootView.findViewById(R.id.btn_right);
        lightStepSeekBar = rootView.findViewById(R.id.seekbar_step_light);

        lightSetButton = rootView.findViewById(R.id.btn_set_light);
    }

    private void applyAnimalStyle(Context context) {
        styleManager = new StyleManager(context, animalType);
        lightContainerLayout.setBackground(AppCompatResources.getDrawable(context, styleManager.getButton02ShapeId()));
        lightLevelText.setTextColor(ContextCompat.getColor(context, styleManager.getBasicColor01Id()));
        lightSetButton.setBackground(AppCompatResources.getDrawable(context, styleManager.getButton01ShapeId()));
        lightSetButton.setTextColor(ContextCompat.getColor(context, styleManager.getBasicColor03Id()));
        lightStepSeekBar.setThumb(ResourcesCompat.getDrawable(getResources(), styleManager.getSeekBarThumbIconId(), null));
    }

    // 이벤트 리스너 설정
    private void setupListeners(View rootView) {
        rootView.findViewById(R.id.btn_left).setOnClickListener(view -> adjustLightLevel(-1));
        rootView.findViewById(R.id.btn_right).setOnClickListener(view -> adjustLightLevel(1));
    }

    // 조명 단계를 조정하는 메서드
    private void adjustLightLevel(int delta) {
        int newLevel = currentLightLevel + delta;
        if (newLevel < 1 || newLevel > maxLightLevel) {
            showToast("조명 단계는 1~5 사이의 값이어야 합니다.");
        } else {
            currentLightLevel = newLevel;
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
    // sendCommand(LIGHT_TOPIC, String.valueOf(currentLightLevel), "조명 단계: " + currentLightLevel);
    // sendCommand("light/topic", "0", "조명 OFF");

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
