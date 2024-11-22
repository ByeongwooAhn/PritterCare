package com.example.prittercare.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.model.Alarm;
import com.example.prittercare.view.adapters.AlarmAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    // RecyclerView 및 어댑터
    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private List<Alarm> alarmList;

    // 삭제 레이아웃 및 선택된 알람 위치
    private LinearLayout deleteLayout;
    private int selectedPosition = -1;

    // 요청 코드 상수
    private static final int REQUEST_CODE_ADD_ALARM = 1;
    private static final int REQUEST_CODE_EDIT_ALARM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // RecyclerView 초기화 및 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 삭제 레이아웃 및 버튼 초기화
        deleteLayout = findViewById(R.id.deleteLayout);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        // 알람 리스트 초기화
        alarmList = new ArrayList<>();

        // 어댑터 초기화 및 설정
        adapter = new AlarmAdapter(alarmList, this::editAlarm, position -> {
            selectedPosition = position;
            deleteLayout.setVisibility(View.VISIBLE); // 롱 클릭 시 삭제 레이아웃 표시
        });
        recyclerView.setAdapter(adapter);

        // 알람 추가 버튼 클릭 시 알람 추가 화면으로 이동
        ImageButton addAlarmButton = findViewById(R.id.addAlarmButton);
        addAlarmButton.setOnClickListener(view -> {
            Intent intent = new Intent(ReservationActivity.this, AlarmEditActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_ALARM);
        });

        // 취소 버튼 클릭 시 삭제 레이아웃 숨김
        cancelButton.setOnClickListener(v -> {
            deleteLayout.setVisibility(View.GONE);
            selectedPosition = -1;
        });

        // 삭제 버튼 클릭 시 선택된 알람 삭제
        deleteButton.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                alarmList.remove(selectedPosition); // 알람 삭제
                adapter.notifyItemRemoved(selectedPosition); // 리스트 갱신
                deleteLayout.setVisibility(View.GONE); // 삭제 레이아웃 숨김
                selectedPosition = -1;
            }
        });
    }

    // 알람 수정 화면으로 이동
    private void editAlarm(int position) {
        Intent intent = new Intent(ReservationActivity.this, AlarmEditActivity.class);
        intent.putExtra("alarm_position", position); // 알람 위치 전달
        intent.putExtra("alarm_data", alarmList.get(position)); // 알람 데이터 전달
        startActivityForResult(intent, REQUEST_CODE_EDIT_ALARM);
    }

    // 알람 추가 및 수정 후 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 알람 추가 또는 수정 시 처리
        if (resultCode == RESULT_OK) {
            Alarm updatedAlarm = (Alarm) data.getSerializableExtra("updated_alarm");
            if (requestCode == REQUEST_CODE_ADD_ALARM) {
                alarmList.add(updatedAlarm); // 새 알람 추가
            } else if (requestCode == REQUEST_CODE_EDIT_ALARM) {
                int position = data.getIntExtra("alarm_position", -1);
                if (position != -1) {
                    alarmList.set(position, updatedAlarm); // 기존 알람 수정
                }
            }
            adapter.notifyDataSetChanged(); // 어댑터 갱신
        }
    }
}
