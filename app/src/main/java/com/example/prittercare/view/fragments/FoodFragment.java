package com.example.prittercare.view.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prittercare.R;
import com.example.prittercare.controller.StyleManager;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.MQTTHelper;

public class FoodFragment extends Fragment {

    private MQTTHelper mqttHelper;

    private StyleManager styleManager;
    private String animalType;

    // 사용자 및 장치 정보
    private String userid = DataManager.getInstance().getUserName(); // 사용자 ID
    private String serialnumber = DataManager.getInstance().getCurrentCageSerialNumber();

    // MQTT 토픽
    private String FOOD_TOPIC;
    private String WATER_TOPIC;

    LinearLayout feedWaterButton;
    ImageView feedWaterButtonIcon;
    TextView feeWaterButtonText;
    LinearLayout feetWaterContainer;

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

        WATER_TOPIC = "${userid}/${serialnumber}/water"
                .replace("${userid}", userid)
                .replace("${serialnumber}", serialnumber);
/*        FOOD_TOPIC = "${userid}/${serialnumber}/food"
                .replace("${userid}", userid)
                .replace("${serialnumber}", serialnumber);*/

        feetWaterContainer = rootView.findViewById(R.id.layout_container_feed_water);

        // 버튼 초기화
        feedWaterButton = rootView.findViewById(R.id.btn_feed_water);
        feedWaterButtonIcon = rootView.findViewById(R.id.ic_btn_water_glass);
        feedWaterButtonIcon.setColorFilter(null);
        feeWaterButtonText = rootView.findViewById(R.id.tv_btn_water_glass);


        applyAnimalStyle(getContext());

        // 리스너 설정
        feedWaterButton.setOnClickListener(v -> feedWater());


        return rootView;
    }

    private void applyAnimalStyle(Context context) {
        animalType = DataManager.getInstance().getCurrentAnimalType();
        styleManager = new StyleManager(context, animalType);

        feetWaterContainer.setBackground(AppCompatResources.getDrawable(getContext(), styleManager.getButton02ShapeId()));

        feedWaterButton.setBackgroundResource(styleManager.getButton01ShapeId());
        feedWaterButtonIcon.setColorFilter(getContext().getColor(styleManager.getBasicColor03Id()), PorterDuff.Mode.SRC_IN);
        feeWaterButtonText.setTextColor(ContextCompat.getColor(requireContext(), styleManager.getBasicColor03Id()));
    }

    // 물 공급
    private void feedWater() {
        sendCommand(WATER_TOPIC, "1", "물을 공급합니다.");
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
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
