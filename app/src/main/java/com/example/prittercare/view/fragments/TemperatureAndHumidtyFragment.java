package com.example.prittercare.view.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prittercare.R;
import com.example.prittercare.model.MQTTHelper;


public class TemperatureAndHumidtyFragment extends Fragment {

    private MQTTHelper mqttHelper;

    public TemperatureAndHumidtyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MQTTHelper 객체 초기화
        mqttHelper = new MQTTHelper(requireContext(), "주소", "fragmentClientId", "id", "pw");
        mqttHelper.initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_temperature_and_humidty, container, false);

        // Initialize views
        EditText etSetTemperature = rootView.findViewById(R.id.et_set_temperature);
        AppCompatButton btnSetTemperature = rootView.findViewById(R.id.btn_set_temperature);
        EditText etSetHumidity = rootView.findViewById(R.id.et_set_humidity);
        AppCompatButton btnSetHumidity = rootView.findViewById(R.id.btn_set_humidity);

        // 온도 설정 버튼 클릭 이벤트
        btnSetTemperature.setOnClickListener(v -> {
            String temperature = etSetTemperature.getText().toString().trim();
            if (!temperature.isEmpty()) {
                sendTemperature(temperature);
            } else {
                Toast.makeText(requireContext(), "온도 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 습도 설정 버튼 클릭 이벤트
        btnSetHumidity.setOnClickListener(v -> {
            String humidity = etSetHumidity.getText().toString().trim();
            if (!humidity.isEmpty()) {
                sendHumidity(humidity);
            } else {
                Toast.makeText(requireContext(), "습도 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void sendTemperature(String temperature) {
        if (mqttHelper != null && mqttHelper.isConnected()) {
            mqttHelper.publish("temperature/topic", temperature, 1);
            Toast.makeText(requireContext(), "온도 설정: " + temperature, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "MQTT 연결 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendHumidity(String humidity) {
        if (mqttHelper != null && mqttHelper.isConnected()) {
            mqttHelper.publish("humidity/topic", humidity, 1);
            Toast.makeText(requireContext(), "습도 설정: " + humidity, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "MQTT 연결 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttHelper != null) {
            mqttHelper.disconnect();
        }
    }
}
