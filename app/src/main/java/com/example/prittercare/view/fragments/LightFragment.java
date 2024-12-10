package com.example.prittercare.view.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
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

    private ImageButton decreaseButton, increaseButton;
    private SeekBar lightStepSeekBar;

    private Button lightSetButton;

    private MQTTHelper mqttHelper;
    private StyleManager styleManager;

    // 사용자 및 장치 정보
    private String userid;
    private String serialnumber;
    private String animalType;

    // 조명 상태
    private final int MAX_LIGHT_LEVEL = 5;
    private int beforeLightLevel;
    private int currentLightLevel;

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

        currentLightLevel = 1;
        beforeLightLevel = currentLightLevel;

        // MQTT 토픽 초기화
        LIGHT_TOPIC = "${userid}/${serialnumber}/light".replace("${userid}", userid).replace("${serialnumber}", serialnumber);

        // 초기화 및 설정
        initializeViews(rootView);
        applyAnimalStyle(getContext());
        setupListeners(rootView);

        setLightLevelText();
        lightStepSeekBar.setProgress(currentLightLevel);
        manageLightSetButtonEnabled(getContext());

        // UI 초기 상태
        return rootView;
    }

    // 뷰 초기화
    private void initializeViews(View rootView) {
        lightContainerLayout = rootView.findViewById(R.id.layout_container_light);

        lightLevelText = rootView.findViewById(R.id.tv_light_level);
        lightLevelIcon = rootView.findViewById(R.id.iv_ic_light_level);

        decreaseButton = rootView.findViewById(R.id.btn_left);
        increaseButton = rootView.findViewById(R.id.btn_right);
        lightStepSeekBar = rootView.findViewById(R.id.seekbar_step_light);

        lightSetButton = rootView.findViewById(R.id.btn_set_light);
    }

    private void applyAnimalStyle(Context context) {
        styleManager = new StyleManager(context, animalType);
        lightContainerLayout.setBackground(AppCompatResources.getDrawable(context, styleManager.getContainerShapeId()));
        lightLevelText.setTextColor(ContextCompat.getColor(context, styleManager.getBasicColor01Id()));
        lightSetButton.setBackground(AppCompatResources.getDrawable(context, styleManager.getButton01ShapeId()));
        lightSetButton.setTextColor(ContextCompat.getColor(context, styleManager.getBasicColor03Id()));
        lightStepSeekBar.setThumb(ResourcesCompat.getDrawable(getResources(), styleManager.getSeekBarThumbIconId(), null));
    }

    // 이벤트 리스너 설정
    private void setupListeners(View rootView) {
        decreaseButton.setOnClickListener(view -> {
            lightStepSeekBar.setProgress(currentLightLevel - 1);
        });
        increaseButton.setOnClickListener(view -> {
            lightStepSeekBar.setProgress(currentLightLevel + 1);
        });

        lightSetButton.setOnClickListener(view -> {
            if (lightSetButton.isEnabled()) {
                if (currentLightLevel > 0) {
                    sendCommand(LIGHT_TOPIC, String.valueOf(currentLightLevel), "조명 설정 : " + currentLightLevel + "단계");
                } else {
                    sendCommand("light/topic", "0", "조명 설정 : 꺼짐");
                }
            } else {
                showToast("이미 설정된 값입니다.");
            }
            beforeLightLevel = currentLightLevel;
            manageLightSetButtonEnabled(getContext());
        });

        lightStepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentLightLevel = progress;
                setLightLevelText();
                manageLightSetButtonEnabled(getContext());
                changeLightIconColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void changeLightIconColor() {
        switch (currentLightLevel) {
            case 0:
                lightLevelIcon.setColorFilter(getContext().getColor(R.color.lightOFF), PorterDuff.Mode.SRC_IN);
                break;
            case 1:
                lightLevelIcon.setColorFilter(getContext().getColor(R.color.lightLevel1), PorterDuff.Mode.SRC_IN);
                break;
            case 2:
                lightLevelIcon.setColorFilter(getContext().getColor(R.color.lightLevel2), PorterDuff.Mode.SRC_IN);
                break;
            case 3:
                lightLevelIcon.setColorFilter(getContext().getColor(R.color.lightLevel3), PorterDuff.Mode.SRC_IN);
                break;
            case 4:
                lightLevelIcon.setColorFilter(getContext().getColor(R.color.lightLevel4), PorterDuff.Mode.SRC_IN);
                break;
            case 5:
                lightLevelIcon.setColorFilter(getContext().getColor(R.color.lightLevel5), PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    private void manageLightSetButtonEnabled(Context context) {
        if (beforeLightLevel == currentLightLevel) {
            lightSetButton.setEnabled(false);
            lightSetButton.setBackground(AppCompatResources.getDrawable(context, styleManager.getButton01ShapeId()));
            lightSetButton.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            lightSetButton.setEnabled(true);
            lightSetButton.setBackground(AppCompatResources.getDrawable(context, styleManager.getButton02ShapeId()));
            lightSetButton.setTextColor(ContextCompat.getColor(context, styleManager.getBasicColor01Id()));
        }
    }

    private void setLightLevelText() {
        if (currentLightLevel > 0) {
            lightLevelText.setText("조명 : " + currentLightLevel + "단계");
        } else if (currentLightLevel == 0) {
            lightLevelText.setText("조명 : 꺼짐");
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
}
