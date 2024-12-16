package com.example.prittercare.view.Activities;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.prittercare.R;
import com.example.prittercare.controller.StyleManager;
import com.example.prittercare.controller.callback.CageListCallback;
import com.example.prittercare.controller.callback.CageSingleUpdateCallBack;
import com.example.prittercare.databinding.ActivityMainBinding;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.DataRepository;
import com.example.prittercare.model.MQTTHelper;
import com.example.prittercare.model.data.CageData;
import com.example.prittercare.view.MainTabManager;

import java.util.List;

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
    /*private static final String TEMPERATURE_TOPIC = "sensor/temperature";*/
    private String SENSOR_TOPIC;
    private String TEMPERATURE_TOPIC;
    private String HUMIDITY_TOPIC;

    // Variables to store the latest values
    private String latestTemperature = "0°C";
    private String latestHumidity = "0 %";

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000; // 5 seconds

    private StyleManager styleManager;

    WebView webView;
    WebSettings webSettings;

    private boolean isFullscreen = false;

    DataRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

// WebView 설정
        webView = findViewById(R.id.webView_video);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

// WebViewClient를 설정하여 페이지 로딩 완료 후 작업 수행
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // 특정 요소만 표시하고 나머지 숨기기 및 스트리밍 자동 시작
                webView.loadUrl("javascript:(function() {" +
                        "document.body.innerHTML = '<img id=\"stream\" src=\"http://192.168.27.46:81/stream\" crossorigin=\"\">';" +
                        "document.getElementById('stream').click();" + // 스트림 강제 시작
                        "})()");
            }
        });

// 스트리밍 페이지 URL 불러오기
        webView.loadUrl("http://192.168.27.46");

        repository = new DataRepository();

        // serialNumber 받기
        cageSerialNumber = DataManager.getInstance().getCurrentCageSerialNumber();
        if (cageSerialNumber != null) {
            Log.d("MainActivity", "Received cageSerialNumber : " + cageSerialNumber);
        }

        loadCageSettings();

        // userName 받기
        userName = DataManager.getInstance().getUserName();

        // MQTT 토픽 초기화
        SENSOR_TOPIC = "${userName}/${cageSerialNumber}/sensor".replace("${userName}", userName).replace("${cageSerialNumber}", cageSerialNumber);

        if (SENSOR_TOPIC == null || SENSOR_TOPIC.isEmpty()) {
            Log.e(TAG, "SENSOR_TOPIC이 null 또는 비어 있습니다.");
            return;
        }

        // MQTTHelper 초기화
        mqttHelper = new MQTTHelper(this, "tcp://medicine.p-e.kr:1884", "myClientId", "GuestMosquitto", "MosquittoGuest1119!");
        mqttHelper.initialize();


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

        // 전체 화면 버튼 클릭 이벤트 (필요 시 구현)
        binding.ivFullScreen.setOnClickListener(view -> {
            // 전체 화면 기능 구현 필요
        });

        //testShowStyle("hamster", "햄스터케이지");

        // cageName 받기
        cageName = DataManager.getInstance().getCurrentCageName();
        if (cageName != null) {
            Log.d("MainActivity", "Received cageName : " + cageName);
        }

        binding.layoutToolbar.tvTitleToolbar.setText(cageName);

        // MainTabManager 초기화
        tabManager = new MainTabManager(this, binding, mqttHelper);
        tabManager.initializeTabs();

        applyAnimalStyle();
        setupFullScreenButton();
    }

    private void testShowStyle(String type, String name) {
        DataManager.getInstance().getCurrentCageData().setAnimalType(type);
        DataManager.getInstance().getCurrentCageData().setCageName(name);
    }

    private void loadCageSettings() {
        String token = DataManager.getInstance().getUserToken();
        Log.d(TAG, "Loading cage settings for SerialNumber: " + cageSerialNumber);

        repository.loadCageSettings(token, cageSerialNumber, new CageSingleUpdateCallBack() {
            @Override
            public void onSuccess(CageData data) {
                // 받아온 데이터를 DataManager에 저장
                DataManager.getInstance().getCurrentCageData().setEnvTemperature(data.getEnvTemperature());
                DataManager.getInstance().getCurrentCageData().setEnvHumidity(data.getEnvHumidity());
                DataManager.getInstance().getCurrentCageData().setEnvLighting(data.getEnvLighting());

                // UI 업데이트
                updateUIWithCageData(data);

                Log.d(TAG, "Cage settings loaded successfully.");
            }

            @Override
            public void onFailure(Throwable t) {
                // 에러 처리 및 로그 기록
                Log.e(TAG, "Error loading cage settings", t);
                showErrorToast("Failed to load cage settings. Please try again.");
            }
        });
    }

    private void updateUIWithCageData(CageData data) {
        binding.tvTemperature.setText(data.getEnvTemperature() + "°C");
        Log.d("Main : Setting Update UI data", "온도 : " + data.getEnvTemperature());
        binding.tvHumidity.setText(data.getEnvHumidity() + "%");
        Log.d("Main : Setting Update UI data", "습도 : " + data.getEnvHumidity());
        // Lighting 정보를 사용할 경우 UI에 추가
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            // 전체화면 모드에서 축소모드로 변경
            exitFullscreen();
        } else {
            // 기본 동작 (이전 화면으로 이동)
            super.onBackPressed();
        }
    }

    private void setupFullScreenButton() {
        binding.ivFullScreen.setOnClickListener(view -> toggleFullscreen());
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
    }

    private void enterFullscreen() {
        isFullscreen = true;

        // 가로 모드로 강제 전환
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // 전체화면으로 상태 변경
        binding.layoutToolbar.toolbarBasic.setVisibility(View.GONE);
        binding.mainTopperContainer.setVisibility(View.GONE);

        // 상태바 숨기기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void exitFullscreen() {
        isFullscreen = false;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 상태바 보이기
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 원래 상태로 복원
        binding.layoutToolbar.toolbarBasic.setVisibility(View.VISIBLE);
        binding.mainTopperContainer.setVisibility(View.VISIBLE);

        // 축소화면 버튼 아이콘 변경
        binding.ivFullScreen.setImageResource(R.drawable.ic_expand);
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
