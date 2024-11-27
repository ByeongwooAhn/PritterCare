package com.example.prittercare.view.Activities;

import android.os.Bundle;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivityMainBinding;
import com.example.prittercare.model.MQTTHelper;
import com.example.prittercare.view.MainTabManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MQTTHelper mqttHelper;
    private MainTabManager tabManager;

    private String animalType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 동물 타입 (예시로 1을 사용)
        animalType = "turtle";

        // MQTTHelper 객체 초기화
        mqttHelper = new MQTTHelper(this, "tcp://medicine.p-e.kr:1884", "myClientId", "GuestMosquitto", "MosquittoGuest1119!");
        mqttHelper.initialize();

        tabManager = new MainTabManager(this, binding, mqttHelper);
        tabManager.initializeTabs();

        // 뒤로가기 버튼 클릭 시
        binding.layoutToolbar.btnBack.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale));
            mqttHelper.disconnect();
            finish();
        });

        // 전체 화면 버튼 클릭 이벤트
        binding.ivFullScreen.setOnClickListener(view -> {
            // 전체 화면 기능 구현 필요
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttHelper != null) {
            mqttHelper.disconnect();
        }
    }

    public MQTTHelper getMqttHelper() {
        return mqttHelper;
    }
}
