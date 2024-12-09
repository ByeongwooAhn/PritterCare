package com.example.prittercare.view.Activities;

import static com.example.prittercare.view.Activities.AlarmScheduler.scheduleAlarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.controller.ReservationReceiver;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.MQTTHelper;
import com.example.prittercare.model.data.ReservationData;
import com.example.prittercare.view.adapters.ReservationAdapter;
import com.example.prittercare.view.fragments.TemperatureAndHumidtyFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ReservationActivity 클래스
 * 예약 데이터를 표시하고 추가, 수정, 삭제하는 기능을 제공하는 메인 화면 액티비티입니다.
 */
public class ReservationActivity extends AppCompatActivity {

    // RecyclerView 및 어댑터
    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private List<ReservationData> alarmList; // 예약 데이터를 저장하는 리스트

    // 삭제 레이아웃 및 선택된 알람 위치
    private LinearLayout deleteLayout; // 삭제 버튼과 취소 버튼을 포함한 레이아웃
    private int selectedPosition = -1; // 현재 선택된 예약의 위치

    // 요청 코드 상수
    private static final int REQUEST_CODE_ADD_ALARM = 1; // 알람 추가 요청 코드
    private static final int REQUEST_CODE_EDIT_ALARM = 2; // 알람 수정 요청 코드

    private MQTTHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // 권한 확인 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        // MQTTHelper 초기화
        mqttHelper = new MQTTHelper(this, "tcp://medicine.p-e.kr:1884", "myClientId", "GuestMosquitto", "MosquittoGuest1119!");


        Log.d("MQTT Connection", "MQTT 상태 (초기화 후): " + mqttHelper.isConnected());

        if (!mqttHelper.isConnected()) {
            Log.e("MQTT Initialization", "MQTT 연결 초기화 실패!");
        }

        // RecyclerView 초기화 및 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 수직 스크롤 리스트로 설정

        // 뒤로가기 버튼 초기화 및 클릭 리스너 추가
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // 뒤로가기 버튼 클릭 시 현재 액티비티 종료

        // 삭제 레이아웃 및 버튼 초기화
        deleteLayout = findViewById(R.id.deleteLayout);
        Button cancelButton = findViewById(R.id.cancelButton); // 취소 버튼
        Button deleteButton = findViewById(R.id.deleteButton); // 삭제 버튼

        alarmList = new ArrayList<>();

        // 어댑터 초기화 및 RecyclerView에 연결
        adapter = new ReservationAdapter(alarmList, this::editAlarm, position -> {
            selectedPosition = position; // 선택된 예약의 위치 저장
            deleteLayout.setVisibility(View.VISIBLE); // 삭제 레이아웃 표시
        });
        recyclerView.setAdapter(adapter);

        checkReservations();

        // 알람 추가 버튼 클릭 시 알람 추가 화면으로 이동
        ImageButton addAlarmButton = findViewById(R.id.addAlarmButton);
        addAlarmButton.setOnClickListener(view -> {
            Intent intent = new Intent(ReservationActivity.this, AlarmEditActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_ALARM); // 알람 추가 요청 코드로 AlarmEditActivity 실행
        });

        // 취소 버튼 클릭 시 삭제 레이아웃 숨김
        cancelButton.setOnClickListener(v -> {
            deleteLayout.setVisibility(View.GONE); // 삭제 레이아웃 숨김
            selectedPosition = -1; // 선택 상태 초기화
        });

        // 삭제 버튼 클릭 시 선택된 알람 삭제
        deleteButton.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                alarmList.remove(selectedPosition); // 리스트에서 알람 삭제
                adapter.notifyItemRemoved(selectedPosition); // RecyclerView 갱신
                deleteLayout.setVisibility(View.GONE); // 삭제 레이아웃 숨김
                selectedPosition = -1; // 선택 상태 초기화
            }
        });
    }

    /**
     * 알람 수정 화면으로 이동
     *
     * @param position 수정할 알람의 위치
     */
    private void editAlarm(int position) {
        Intent intent = new Intent(ReservationActivity.this, AlarmEditActivity.class);
        intent.putExtra("alarm_position", position); // 수정할 예약의 위치 전달
        intent.putExtra("alarm_data", alarmList.get(position)); // 수정할 예약 데이터 전달
        startActivityForResult(intent, REQUEST_CODE_EDIT_ALARM); // 알람 수정 요청 코드로 AlarmEditActivity 실행
    }

    private String convertDateToISO(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM월 dd일 (E)", Locale.KOREAN);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date parsedDate = inputFormat.parse(date);
            return (parsedDate != null) ? outputFormat.format(parsedDate) : null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String convertTimeToISO(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("a hh:mm", Locale.KOREAN);
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Date parsedTime = inputFormat.parse(time);
            return (parsedTime != null) ? outputFormat.format(parsedTime) : null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void scheduleAlarm(Context context, ReservationData reservation) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReservationReceiver.class);
        intent.putExtra("reservation_name", reservation.getReserveName());

        try {
            String dateString = convertDateToISO(reservation.getReserveDate());
            String timeString = convertTimeToISO(reservation.getReserveTime());

            Log.d("ReservationActivity", "Converted Reserve Date: " + dateString);
            Log.d("ReservationActivity", "Converted Reserve Time: " + timeString);

            if (dateString != null && timeString != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date date = dateFormat.parse(dateString + " " + timeString);

                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            context,
                            reservation.hashCode(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    if (alarmManager != null) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
            } else {
                throw new ParseException("Invalid date or time format", 0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context, "잘못된 날짜 또는 시간 형식입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            ReservationData updatedAlarm = (ReservationData) data.getSerializableExtra("updated_alarm");
            if (updatedAlarm != null) {
                if (requestCode == REQUEST_CODE_ADD_ALARM) {
                    if (alarmList == null) {
                        alarmList = new ArrayList<>();
                    }
                    alarmList.add(updatedAlarm);
                } else if (requestCode == REQUEST_CODE_EDIT_ALARM) {
                    int position = data.getIntExtra("alarm_position", -1);
                    if (position != -1 && alarmList != null) {
                        alarmList.set(position, updatedAlarm);
                    }
                }
                scheduleAlarm(this, updatedAlarm);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /* 예약 시간이 되면 토스트 메시지를 표시하는 메서드 */
    private void checkReservations() {
        Timer timer = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mqttHelper.initialize();
                if (mqttHelper != null) {
                    Log.d("MQTT Connection", "MQTT 상태 (주기 확인): " + mqttHelper.isConnected());
                }
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                dateFormat.setTimeZone(TimeZone.getDefault());
                timeFormat.setTimeZone(TimeZone.getDefault());

                String currentDate = dateFormat.format(calendar.getTime());
                String currentTime = timeFormat.format(calendar.getTime());

                Log.d("ReservationActivity", "Current(현재) Date: " + currentDate + ", Time: " + currentTime);

                for (ReservationData alarm : alarmList) {
                    String alarmDate = convertDateToISO(alarm.getReserveDate()).substring(5);
                    String alarmTime = convertTimeToISO(alarm.getReserveTime());

                    if (currentDate.equals(alarmDate) && currentTime.equals(alarmTime)) {
                        handler.post(() -> {
                            Toast.makeText(
                                    ReservationActivity.this,
                                    "예약이 실행되었습니다: " + alarm.getReserveName(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            String topic;
                            String message;

                            switch (alarm.getReserveType()) {
                                case "water":
                                    topic = String.format("%s/%s/water", DataManager.getInstance().getUserName(), DataManager.getInstance().getCurrentCageSerialNumber());
                                    message = "1";
                                    sendMQTTMessage(topic, message);
                                    break;

                                case "light":
                                    topic = String.format("%s/%s/light", DataManager.getInstance().getUserName(), DataManager.getInstance().getCurrentCageSerialNumber());
                                    message = String.valueOf(alarm.getLightLevel());
                                    sendMQTTMessage(topic, message);
                                    break;

                                default:
                                    Log.d("ReservationActivity", "Unknown reserve type: " + alarm.getReserveType());
                            }
                        });
                    }
                }
            }
        }, 0, 10000);
    }

    private void sendMQTTMessage(String topic, String message) {
        if (mqttHelper != null) {
            Log.d("MQTT Connection", "MQTT 상태 (전송 시도 전): " + mqttHelper.isConnected());
        }

        if (mqttHelper != null && mqttHelper.isConnected()) {
            mqttHelper.publish(topic, message, 1);
            Log.d("ReservationActivity", "MQTT 메시지 전송: " + topic + " - " + message);
        } else {
            Toast.makeText(this, "MQTT 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void reconnectMQTT() {
        if (mqttHelper != null && !mqttHelper.isConnected()) {
            mqttHelper.initialize();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Log.d("MQTT Connection", "MQTT 상태 (재연결 시도 후): " + mqttHelper.isConnected());
            }, 5000);
        }
    }
}
