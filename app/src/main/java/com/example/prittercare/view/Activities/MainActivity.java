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
import com.example.prittercare.controller.callback.CageSingleUpdateCallBack;
import com.example.prittercare.databinding.ActivityMainBinding;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.DataRepository;
import com.example.prittercare.model.MQTTHelper;
import com.example.prittercare.model.data.CageData;
import com.example.prittercare.view.MainTabManager;
import com.example.prittercare.view.fragments.TemperatureAndHumidtyFragment;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MQTTHelper mqttHelper;
    private MainTabManager tabManager;

    private String animalType;
    private String cageSerialNumber;
    private String cageName;
    private String userName;
    private String SENSOR_TOPIC;

    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 5000;

    private DataRepository repository;
    private boolean isFullscreen = false;

    // WebView 관련
    private WebView webView;
    private WebSettings webSettings;

    // 초기화 메서드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeUIComponents();
        initializeCameraStreaming();
        initializeMQTT();
        applyAnimalStyle(); // 스타일 적용
        initializeTabs();
        loadCageSettings(); // 설정값 로드
    }

    /**
     * UI 관련 초기화
     */
    private void initializeUIComponents() {
        repository = new DataRepository();
        cageSerialNumber = DataManager.getInstance().getCurrentCageSerialNumber();
        userName = DataManager.getInstance().getUserName();
        cageName = DataManager.getInstance().getCurrentCageName();
        animalType = DataManager.getInstance().getCurrentAnimalType();

        if (cageName != null) {
            binding.layoutToolbar.tvTitleToolbar.setText(cageName);
        }

        binding.layoutToolbar.btnBack.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale));
            finish();
        });

        binding.ivFullScreen.setOnClickListener(view -> toggleFullscreen());
    }

    /**
     * 카메라 스트리밍 초기화
     */
    private void initializeCameraStreaming() {
        webView = findViewById(R.id.webView_video);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:(function() {" +
                        "document.body.innerHTML = '<img id=\"stream\" src=\"http://192.168.27.46:81/stream\" crossorigin=\"\">';" +
                        "document.getElementById('stream').click();" +
                        "})()");
            }
        });

        webView.loadUrl("http://192.168.27.46");
    }

    /**
     * MQTT 관련 초기화
     */
    private void initializeMQTT() {
        if (cageSerialNumber == null || userName == null) {
            Log.e(TAG, "MQTT 초기화 실패: 사용자 또는 케이지 정보가 없습니다.");
            return;
        }

        SENSOR_TOPIC = String.format("%s/%s/sensor", userName, cageSerialNumber);
        mqttHelper = new MQTTHelper(this, "tcp://medicine.p-e.kr:1884", "myClientId", "GuestMosquitto", "MosquittoGuest1119!");
        mqttHelper.initialize();

        try {
            mqttHelper.getMqttClient().setCallback(new org.eclipse.paho.client.mqttv3.MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG, "MQTT 연결 끊김: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message) {
                    processMQTTMessage(topic, new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                    Log.d(TAG, "MQTT 메시지 전송 완료");
                }
            });
            mqttHelper.subscribe(SENSOR_TOPIC);
        } catch (Exception e) {
            Log.e(TAG, "MQTT 초기화 중 오류: " + e.getMessage());
        }

        startPeriodicUpdate();
    }

    private void processMQTTMessage(String topic, String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            String latestTemperature = jsonObject.optString("temperature", "0°C") + "°C";
            String latestHumidity = jsonObject.optString("humidity", "0 %") + "%";

            binding.tvTemperature.setText(latestTemperature);
            binding.tvHumidity.setText(latestHumidity);
        } catch (Exception e) {
            Log.e(TAG, "MQTT 메시지 처리 중 오류: " + e.getMessage());
        }
    }

    private void startPeriodicUpdate() {
        handler.postDelayed(() -> {
            // UI 업데이트 주기 처리
            handler.postDelayed(this::startPeriodicUpdate, UPDATE_INTERVAL);
        }, UPDATE_INTERVAL);
    }

    /**
     * 동물 타입별 스타일 적용
     */
    private void applyAnimalStyle() {
        animalType = DataManager.getInstance().getCurrentAnimalType();
        StyleManager styleManager = new StyleManager(this, animalType);

        binding.main.setBackground(AppCompatResources.getDrawable(this, styleManager.getBackgroundMainId()));
        binding.mainLayoutContainer.setBackground(AppCompatResources.getDrawable(this, styleManager.getCardShapeId()));

        setStatusBarColor(styleManager);
    }

    private void setStatusBarColor(StyleManager styleManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(styleManager.getBasicColor02Id()));
        }
    }

    /**
     * 탭 초기화
     */
    private void initializeTabs() {
        tabManager = new MainTabManager(this, binding, mqttHelper);
        tabManager.initializeTabs();
    }

    /**
     * 세팅값 불러오기
     */
    private void loadCageSettings() {
        String token = DataManager.getInstance().getUserToken();
        if (token == null || cageSerialNumber == null) {
            Log.e(TAG, "설정을 로드할 수 없습니다. 토큰 또는 케이지 일련번호가 누락되었습니다.");
            return;
        }

        repository.loadCageSettings(token, cageSerialNumber, new CageSingleUpdateCallBack() {
            @Override
            public void onSuccess(CageData data) {
                DataManager.getInstance().getCurrentCageData().setEnvTemperature(data.getEnvTemperature());
                DataManager.getInstance().getCurrentCageData().setEnvHumidity(data.getEnvHumidity());
                DataManager.getInstance().getCurrentCageData().setEnvLighting(data.getEnvLighting());

                updateUIWithCageData(data);
                // 초기 데이터를 프래그먼트에 전달
                TemperatureAndHumidtyFragment fragment = (TemperatureAndHumidtyFragment) tabManager.getFragment(MainTabManager.TAB_TEMPERATURE);
                if (fragment != null) {
                    fragment.updateTemperatureAndHumidity(data.getEnvTemperature(), data.getEnvHumidity());
                }

                Log.d(TAG, "케이지 설정을 성공적으로 로드했습니다.");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "케이지 설정 로드 중 오류 발생: " + t.getMessage(), t);
                showErrorToast("설정을 불러오는 데 실패했습니다. 다시 시도해주세요.");
            }
        });
    }

    private void updateUIWithCageData(CageData data) {
        binding.tvTemperature.setText(data.getEnvTemperature() + "°C");
        binding.tvHumidity.setText(data.getEnvHumidity() + "%");
        // 조명 정보가 필요한 경우 추가 설정 가능
        Log.d(TAG, "UI에 설정값을 업데이트했습니다.");
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding.layoutToolbar.toolbarBasic.setVisibility(View.GONE);
        binding.mainTopperContainer.setVisibility(View.GONE);
    }

    private void exitFullscreen() {
        isFullscreen = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding.layoutToolbar.toolbarBasic.setVisibility(View.VISIBLE);
        binding.mainTopperContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            // 전체 화면 상태에서 뒤로가기 버튼을 누르면 exitFullscreen 호출
            exitFullscreen();
        } else {
            // 기본 뒤로가기 동작
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttHelper != null) {
            mqttHelper.disconnect();
        }
        handler.removeCallbacksAndMessages(null);
    }
}
