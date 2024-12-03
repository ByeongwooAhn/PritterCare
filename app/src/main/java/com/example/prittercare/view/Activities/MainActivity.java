package com.example.prittercare.view.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivityMainBinding;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.MQTTHelper;
import com.example.prittercare.view.MainTabManager;

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
    /*private static final String TEMPERATURE_TOPIC = "sensor/temperature";*/
    private String TEMPERATURE_TOPIC;
    private String HUMIDITY_TOPIC;

    // Variables to store the latest values
    private String latestTemperature = "0°C";
    private String latestHumidity = "0 %";

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // serialNumber 받기
        cageSerialNumber = getIntent().getStringExtra("cageSerialNumber");
        if(cageSerialNumber != null) {
            Log.d("MainActivity", "Received cageSerialNumber : " + cageSerialNumber);
        }

        // cageName 받기
        cageName = getIntent().getStringExtra("cageName");
        if(cageName != null) {
            Log.d("MainActivity", "Received cageName : " + cageName);
        }

        userName = DataManager.getInstance().getUserName();

        // MQTT 토픽 초기화
        TEMPERATURE_TOPIC = "${userName}/${cageSerialNumber}/temperature"
                .replace("${userName}", userName)
                .replace("${cageSerialNumber}", this.cageSerialNumber);
        HUMIDITY_TOPIC = "${userName}/${cageSerialNumber}/humidity"
                .replace("${userName}", userName)
                .replace("${cageSerialNumber}", this.cageSerialNumber);

        // 동물 타입 설정
        animalType = "turtle";

        // MQTTHelper 초기화
        mqttHelper = new MQTTHelper(this, "tcp://medicine.p-e.kr:1884", "myClientId", "GuestMosquitto", "MosquittoGuest1119!");
/*        mqttHelper.initialize();*/

        // MainTabManager 초기화
        tabManager = new MainTabManager(this, binding, mqttHelper);
        tabManager.initializeTabs();

/*        // MQTT Topics 구독 및 메시지 처리
        subscribeToTopics();*/

/*        // UI 업데이트 루프 시작
        startPeriodicUpdate();*/

        // 뒤로가기 버튼 클릭 이벤트
        binding.layoutToolbar.btnBack.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale));
            mqttHelper.disconnect();
            finish();
        });

        // 전체 화면 버튼 클릭 이벤트 (필요 시 구현)
        binding.ivFullScreen.setOnClickListener(view -> {
            // 전체 화면 기능 구현 필요
        });

        binding.layoutToolbar.tvTitleToolbar.setText(cageName);
    }

    private void subscribeToTopics() {
        mqttHelper.getMqttClient().setCallback(new org.eclipse.paho.client.mqttv3.MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "MQTT 연결 끊김: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message) {
                String receivedMessage = new String(message.getPayload());
                Log.d(TAG, "토픽 [" + topic + "]에서 메시지 수신: " + receivedMessage);

                // 최신 값 업데이트
                if (topic.equals(TEMPERATURE_TOPIC)) {
                    latestTemperature = receivedMessage + "°C";
                } else if (topic.equals(HUMIDITY_TOPIC)) {
                    latestHumidity = receivedMessage + " %";
                }
            }

            @Override
            public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                Log.d(TAG, "메시지 전송 완료");
            }
        });

        // 각각의 토픽 구독
        mqttHelper.subscribe(TEMPERATURE_TOPIC);
        mqttHelper.subscribe(HUMIDITY_TOPIC);
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
