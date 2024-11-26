package com.example.prittercare.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.prittercare.R;
import com.example.prittercare.model.MQTTHelper;

public class LightFragment extends Fragment {

    private MQTTHelper mqttHelper;
    private TextView lightLevelText;
    private View containerLightLevel;

    private int currentLightLevel = 1;
    private int maxLightLevel = 5;
    private boolean isLightOn = true;

    public static LightFragment newInstance(MQTTHelper mqttHelper) {
        return new LightFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mqttHelper = new MQTTHelper(requireContext(), "주소", "fragmentClientId", "id", "pw");
        mqttHelper.initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_light, container, false);

        // 뷰 초기화
        lightLevelText = rootView.findViewById(R.id.tv_light_level);
        btnLeft = rootView.findViewById(R.id.btn_left);
        ImageButton btnRight = rootView.findViewById(R.id.btn_right);
        btnOn = rootView.findViewById(R.id.btn_light_on);
        btnOff = rootView.findViewById(R.id.btn_light_off);
        containerLightLevel = rootView.findViewById(R.id.container_light_level);

        // 초기 조명 레벨 업데이트
        updateLightLevel();

        // ON 버튼 클릭 시
        btnOn.setOnClickListener(v -> {
            toggleButtonState(btnOn, btnOff);
            turnLightOn();
            sendLightLevel(); // 현재 조명 레벨 전송
        });

        // OFF 버튼 클릭 시
        btnOff.setOnClickListener(v -> {
            toggleButtonState(btnOff, btnOn);
            turnLightOff();
            sendLightOffSignal(); // 조명 OFF 신호 전송
        });

        // 왼쪽 버튼 클릭 시 조명 레벨 감소
        btnLeft.setOnClickListener(v -> {
            if (isLightOn && currentLightLevel > 1) {
                currentLightLevel--;
                updateLightLevel();
                sendLightLevel(); // 조명 레벨 전송
            } else {
                Toast.makeText(requireContext(), "최소 단계입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 오른쪽 버튼 클릭 시 조명 레벨 증가
        btnRight.setOnClickListener(v -> {
            if (isLightOn && currentLightLevel < maxLightLevel) {
                currentLightLevel++;
                updateLightLevel();
                sendLightLevel(); // 조명 레벨 전송
            } else {
                Toast.makeText(requireContext(), "최대 단계입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    // 조명 레벨을 MQTT로 전송
    private void sendLightLevel() {
        if (mqttHelper.isConnected()) {
            mqttHelper.publish("light/topic", String.valueOf(currentLightLevel), 1);
            Toast.makeText(requireContext(), "조명 단계: " + currentLightLevel, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "MQTT 연결 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // 조명 OFF 신호를 MQTT로 전송
    private void sendLightOffSignal() {
        if (mqttHelper.isConnected()) {
            mqttHelper.publish("light/topic", "0", 1);
            Toast.makeText(requireContext(), "조명 OFF", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "MQTT 연결 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // 조명 레벨을 UI에 업데이트
    private void updateLightLevel() {
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

    private int getColorForLightLevel(int level) {
        switch (level) {
            case 1:
                return R.color.white;
            case 2:
                return R.color.white;
            case 3:
                return R.color.white;
            case 4:
                return R.color.black;
            case 5:
                return R.color.black;
            default:
                return R.color.white;
        }
    }

    private int getContainerColorForLightLevel(int level) {
        switch (level) {
            case 1:
                return R.drawable.shape_card_main_light_level1;
            case 2:
                return R.drawable.shape_card_main_light_level2;
            case 3:
                return R.drawable.shape_card_main_light_level3;
            case 4:
                return R.drawable.shape_card_main_light_level4;
            case 5:
                return R.drawable.shape_card_main_light_level5;
            default:
                return R.drawable.shape_card_main_light_level1;
        }
    }

    private void turnLightOn() {
        isLightOn = true;
        updateLightLevel();
    }

    private void turnLightOff() {
        isLightOn = false;
        updateLightLevel();
    }

    private void toggleButtonState(Button activeButton, Button inactiveButton) {
        activeButton.setBackgroundResource(R.drawable.shape_button01);
        activeButton.setTextColor(getResources().getColor(R.color.basicColor03));

        inactiveButton.setBackgroundResource(R.drawable.shape_button02);
        inactiveButton.setTextColor(getResources().getColor(R.color.white));
    }
}
