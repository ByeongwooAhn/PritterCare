package com.example.prittercare.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prittercare.R;
import com.example.prittercare.controller.StyleManager;
import com.example.prittercare.controller.callback.CageUpdateCallback;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.DataRepository;
import com.example.prittercare.model.MQTTHelper;
import com.example.prittercare.model.data.CageData;
import com.google.android.material.tabs.TabLayout;


public class TemperatureAndHumidtyFragment extends Fragment {

    LinearLayout temperatureContainerLayout, humidityContainerLayout;
    TextView temperatureGetTextView, temperatureTextTextView, temperatureSignTextView;
    TextView humidityGetTextView, humidityTextTextView, humiditySignTextView;
    AppCompatButton temperatureButton, humidityButton;


    private MQTTHelper mqttHelper;

    // 사용자 및 장치 정보
    private String userid = DataManager.getInstance().getUserName(); // 사용자 ID
    private String serialnumber = DataManager.getInstance().getCurrentCageSerialNumber();
    private String animalType;

    // MQTT 토픽
    private String TEMPERATURE_TOPIC;
    private String HUMIDITY_TOPIC;

    private StyleManager styleManager;

    public static TemperatureAndHumidtyFragment newInstance(MQTTHelper mqttHelper) {
        TemperatureAndHumidtyFragment fragment = new TemperatureAndHumidtyFragment();
        fragment.mqttHelper = mqttHelper;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_temperature_and_humidty, container, false);

        TEMPERATURE_TOPIC = "${userid}/${serialnumber}/temperature"
                .replace("${userid}", userid)
                .replace("${serialnumber}", serialnumber);
        HUMIDITY_TOPIC = "${userid}/${serialnumber}/humidity"
                .replace("${userid}", userid)
                .replace("${serialnumber}", serialnumber);

        initializeViews(rootView);
        applyAnimalStyle(rootView);
        setupButtonListeners(rootView);

        return rootView;
    }

    private void initializeViews(View rootView) {
        temperatureContainerLayout = rootView.findViewById(R.id.layout_container_temperature);
        temperatureGetTextView = rootView.findViewById(R.id.tv_get_temperature);
        temperatureTextTextView = rootView.findViewById(R.id.tv_temperature_text);
        temperatureSignTextView = rootView.findViewById(R.id.tv_temperature_sign);
        temperatureButton = rootView.findViewById(R.id.btn_set_temperature);

        humidityContainerLayout = rootView.findViewById(R.id.layout_container_humidity);
        humidityGetTextView = rootView.findViewById(R.id.tv_get_humidity);
        humidityTextTextView = rootView.findViewById(R.id.tv_humidity_text);
        humiditySignTextView = rootView.findViewById(R.id.tv_humidity_sign);
        humidityButton = rootView.findViewById(R.id.btn_set_humidity);

        CageData currentCage = DataManager.getInstance().getCurrentCageData();
        String temperatureValue = currentCage.getEnvTemperature();

        String humidityValue = currentCage.getEnvHumidity();

        if(temperatureValue != null) {
            temperatureGetTextView.setText("설정된 온도 값 : " + temperatureValue + "°C");
        }

        if(humidityValue != null) {
            humidityGetTextView.setText("설정된 습도 값 : " + humidityValue + "%");
        }
    }

    private void applyAnimalStyle(View rootView) {
        animalType = DataManager.getInstance().getCurrentAnimalType();
        styleManager = new StyleManager(getContext(), animalType);

        temperatureContainerLayout.setBackground(AppCompatResources.getDrawable(getContext(), styleManager.getContainerShapeId()));
        humidityContainerLayout.setBackground(AppCompatResources.getDrawable(getContext(), styleManager.getContainerShapeId()));

        // TextView 스타일 변경
        temperatureGetTextView.setTextColor(ContextCompat.getColor(requireContext(), styleManager.getBasicColor03Id()));
        humidityGetTextView.setTextColor(ContextCompat.getColor(requireContext(), styleManager.getBasicColor03Id()));
        temperatureTextTextView.setTextColor(ContextCompat.getColor(requireContext(), styleManager.getBasicColor01Id()));
        humidityTextTextView.setTextColor(ContextCompat.getColor(requireContext(), styleManager.getBasicColor01Id()));
        temperatureSignTextView.setTextColor(ContextCompat.getColor(requireContext(), styleManager.getBasicColor01Id()));
        humiditySignTextView.setTextColor(ContextCompat.getColor(requireContext(), styleManager.getBasicColor01Id()));

        // 버튼 스타일 변경
        temperatureButton.setBackgroundResource(styleManager.getButton01ShapeId());
        temperatureButton.setTextColor(ContextCompat.getColor(requireContext(), styleManager.getBasicColor03Id()));
        humidityButton.setBackgroundResource(styleManager.getButton01ShapeId());
        humidityButton.setTextColor(ContextCompat.getColor(requireContext(), styleManager.getBasicColor03Id()));
    }

    private void setupButtonListeners(View rootView) {
        rootView.findViewById(R.id.btn_set_temperature).setOnClickListener(view -> {
            String temperature = ((EditText) rootView.findViewById(R.id.et_set_temperature)).getText().toString();
            sendCommand(TEMPERATURE_TOPIC, temperature, "온도 설정: " + temperature);
        });

        rootView.findViewById(R.id.btn_set_humidity).setOnClickListener(view -> {
            String humidity = ((EditText) rootView.findViewById(R.id.et_set_humidity)).getText().toString();
            sendCommand(HUMIDITY_TOPIC, humidity, "습도 설정: " + humidity);
        });
    }

    private void sendCommand(String topic, String message, String successToast) {
        if (mqttHelper != null && mqttHelper.isConnected()) {
            mqttHelper.publish(topic, message, 1);
            Toast.makeText(requireContext(), successToast, Toast.LENGTH_SHORT).show();
            // MQTT 메시지 전송 후 서버에 업데이트 요청
            if (topic.equals(TEMPERATURE_TOPIC)) {
                updateCageTemperature(message);
            } else if (topic.equals(HUMIDITY_TOPIC)) {
                updateCageHumidity(message);
            }
        } else {
            Toast.makeText(requireContext(), "MQTT 연결 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCageHumidity(String humidityValue) {
        String token = DataManager.getInstance().getUserToken();

        DataRepository repository = new DataRepository();
        repository.updateHumidity(token, serialnumber, humidityValue, new CageUpdateCallback() {
            @Override
            public void onSuccess(String message) {
                CageData currentCage = DataManager.getInstance().getCurrentCageData();
                if (currentCage != null) {
                    currentCage.setEnvHumidity(humidityValue);
                    DataManager.getInstance().updateCageData(currentCage);
                }
                Log.d("Humidity Server Update", "서버에 습도값 업데이트 성공: " + message);
                humidityGetTextView.setText("설정된 습도 값 : "+humidityValue + "%");
                Log.d("Humidity Update", "화면에 습도값 업데이트 성공: " + message);
            }

            @Override
            public void onFailure(Throwable error) {
                // 실패 시 오류 메시지 출력
                Log.e("HumidityUpdate", "습도값 업데이트 실패: " + error.getMessage());
            }
        });
    }

    private void updateCageTemperature(String temperatureValue) {
        String token = DataManager.getInstance().getUserToken();

        DataRepository repository = new DataRepository();
        repository.updateTemperature(token, serialnumber, temperatureValue, new CageUpdateCallback() {
            @Override
            public void onSuccess(String message) {
                CageData currentCage = DataManager.getInstance().getCurrentCageData();
                if (currentCage != null) {
                    currentCage.setEnvTemperature(temperatureValue);
                    DataManager.getInstance().updateCageData(currentCage);
                }
                Log.d("Temperature Server Update", "서버에 온도값 업데이트 성공: " + message);
                temperatureGetTextView.setText("설정된 온도 값 : " + temperatureValue + "°C");
            }

            @Override
            public void onFailure(Throwable error) {
                // 실패 시 오류 메시지 출력
                Log.e("TemperatureUpdate", "온도값 업데이트 실패: " + error.getMessage());
            }
        });
    }

    public void updateTemperatureAndHumidity(String temperature, String humidity) {
        if (temperature != null) {
            temperatureGetTextView.setText("설정된 온도 값 : " + temperature + "°C");
        }
        if (humidity != null) {
            humidityGetTextView.setText("설정된 습도 값 : " + humidity + "%");
        }
    }
}
