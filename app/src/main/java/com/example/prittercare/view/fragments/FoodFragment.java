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

    public FoodFragment() {
        // Required empty public constructor
    }

    public static FoodFragment newInstance() {
        return new FoodFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize MQTT helper
        mqttHelper = new MQTTHelper(requireContext(), "주소", "fragmentClientId", "id", "pw");
        mqttHelper.initialize();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_food, container, false);

        // Initialize buttons
        LinearLayout feedFoodButton = rootView.findViewById(R.id.btn_feed_food);
        LinearLayout feedWaterButton = rootView.findViewById(R.id.btn_feed_water);

        // Set up listeners
        feedFoodButton.setOnClickListener(v -> feedFood());
        feedWaterButton.setOnClickListener(v -> feedWater());

        return rootView;
    }

    private void feedFood() {
        if (mqttHelper.isConnected()) {
            mqttHelper.publish("feed/food/topic", "1", 1); // Publish food supply message
            Toast.makeText(requireContext(), "먹이를 공급합니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "MQTT 연결 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void feedWater() {
        if (mqttHelper.isConnected()) {
            mqttHelper.publish("feed/water/topic", "1", 1); // Publish water supply message
            Toast.makeText(requireContext(), "물을 공급합니다.", Toast.LENGTH_SHORT).show();
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
