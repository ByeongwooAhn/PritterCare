package com.example.prittercare.view.Activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.prittercare.R;
import com.example.prittercare.controller.StyleManager;
import com.example.prittercare.databinding.ActivityMainBinding;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.MQTTHelper;
import com.example.prittercare.view.MainTabManager;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MQTTHelper mqttHelper;
    private MainTabManager tabManager;

    private String animalType;

    // 사용자 및 장치 정보
    private String cageSerialNumber; // 장치 일련번호
    private String cageName;
    private String userName; // 사용자 ID

    // MQTT Topics
    private String SENSOR_TOPIC;
    private String TEMPERATURE_TOPIC;
    private String HUMIDITY_TOPIC;

    // Variables to store the latest values
    private String latestTemperature = "0°C";
    private String latestHumidity = "0%";

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 3000; // 3 seconds

    private StyleManager styleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // serialNumber 받기
        cageSerialNumber = DataManager.getInstance().getCurrentCageSerialNumber();
        if (cageSerialNumber == null || cageSerialNumber.isEmpty()) {
            Log.e(TAG, "cageSerialNumber가 null입니다. 기본값으로 설정합니다.");
            cageSerialNumber = "defaultCage";
        }

        // cageName 받기
        cageName = DataManager.getInstance().getCurrentCageName();
        if (cageName != null) {
            Log.d(TAG, "Received cageName: " + cageName);
        } else {
            cageName = "Unknown Cage";
        }

        // userName 받기
        userName = DataManager.getInstance().getUserName();
        if (userName == null || userName.isEmpty()) {
            Log.e(TAG, "userName이 null입니다. 기본값으로 설정합니다.");
            userName = "defaultUser";
        }

        // MQTT 토픽 초기화
        SENSOR_TOPIC = "${userName}/${cageSerialNumber}/sensor"
                .replace("${userName}", userName)
                .replace("${cageSerialNumber}", cageSerialNumber);

        if (SENSOR_TOPIC == null || SENSOR_TOPIC.isEmpty()) {
            Log.e(TAG, "SENSOR_TOPIC이 null 또는 비어 있습니다.");
            return;
        }

        // MQTTHelper 초기화
        mqttHelper = new MQTTHelper(this, "tcp://medicine.p-e.kr:1884", "myClientId", "GuestMosquitto", "MosquittoGuest1119!");
        mqttHelper.initialize();

        // MainTabManager 초기화
        tabManager = new MainTabManager(this, binding, mqttHelper);
        tabManager.initializeTabs();

        // MQTT Topics 구독 및 메시지 처리
        try {
            subscribeToTopics();
        } catch (Exception e) {
            Log.e(TAG, "MQTT 구독 중 오류: " + e.getMessage());
        }

        // UI 업데이트 루프 시작
        startPeriodicUpdate();

        // 뒤로가기 버튼 클릭 이벤트
        binding.layoutToolbar.btnBack.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale));
            mqttHelper.disconnect();
            finish();
        });

        binding.layoutToolbar.tvTitleToolbar.setText(cageName);
        applyAnimalStyle();
    }

    private void subscribeToTopics() {
        mqttHelper.getMqttClient().setCallback(new org.eclipse.paho.client.mqttv3.MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "MQTT 연결 끊김: " + cause.getMessage());
                mqttHelper.initialize();
            }

            @Override
            public void messageArrived(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message) {
                String receivedMessage = new String(message.getPayload());
                Log.d(TAG, "토픽 [" + topic + "]에서 메시지 수신: " + receivedMessage);

                try {
                    // JSON 파싱
                    JSONObject jsonObject = new JSONObject(receivedMessage);
                    if (jsonObject.has("temperature")) {
                        latestTemperature = jsonObject.getString("temperature") + "°C";
                    }
                    if (jsonObject.has("humidity")) {
                        latestHumidity = jsonObject.getString("humidity") + "%";
                    }
                } catch (Exception e) {
                    Log.e(TAG, "JSON 파싱 오류: " + e.getMessage());
                }
            }

            @Override
            public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                Log.d(TAG, "메시지 전송 완료");
            }
        });

        // 토픽 구독
        mqttHelper.subscribe(SENSOR_TOPIC);
    }

    private void applyAnimalStyle() {
        animalType = DataManager.getInstance().getCurrentAnimalType();
        styleManager = new StyleManager(this, animalType);

        binding.main.setBackground(AppCompatResources.getDrawable(this, styleManager.getBackgroundMainId()));
        binding.mainLayoutContainer.setBackground(AppCompatResources.getDrawable(this, styleManager.getCardShapeId()));
        setStatusBar(styleManager);
    }

    private void setStatusBar(StyleManager styleManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(styleManager.getBasicColor02Id()));
        }
    }

    private void startPeriodicUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // UI에 최신 데이터 표시
                binding.tvTemperature.setText(latestTemperature);
                binding.tvHumidity.setText(latestHumidity);

                // 5초 후 다시 실행
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttHelper != null) {
            mqttHelper.disconnect();
        }
        handler.removeCallbacksAndMessages(null); // 핸들러 정리
    }

    public MQTTHelper getMqttHelper() {
        return mqttHelper;
    }
}
