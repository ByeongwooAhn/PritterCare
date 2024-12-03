package com.example.prittercare.view.Activities;

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
import com.example.prittercare.model.data.ReservationData;
import com.example.prittercare.view.adapters.ReservationAdapter;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

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

        // 알람 리스트 초기화
        alarmList = new ArrayList<>();

        // 어댑터 초기화 및 RecyclerView에 연결
        adapter = new ReservationAdapter(alarmList, this::editAlarm, position -> {
            selectedPosition = position; // 선택된 예약의 위치 저장
            deleteLayout.setVisibility(View.VISIBLE); // 삭제 레이아웃 표시
        });
        recyclerView.setAdapter(adapter);

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

    /**
     * 알람 추가 및 수정 후 결과 처리
     *
     * @param requestCode 요청 코드 (알람 추가 또는 수정)
     * @param resultCode  결과 코드
     * @param data        반환된 데이터
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // 반환된 예약 데이터 가져오기
            ReservationData updatedAlarm = (ReservationData) data.getSerializableExtra("updated_alarm");
            if (requestCode == REQUEST_CODE_ADD_ALARM) {
                // 새 예약 추가
                alarmList.add(updatedAlarm);
                AlarmScheduler.scheduleAlarm(this, updatedAlarm); // 예약 스케줄링
            } else if (requestCode == REQUEST_CODE_EDIT_ALARM) {
                // 기존 예약 수정
                int position = data.getIntExtra("alarm_position", -1);
                if (position != -1) {
                    alarmList.set(position, updatedAlarm); // 수정된 데이터로 업데이트
                    AlarmScheduler.scheduleAlarm(this, updatedAlarm); // 예약 재스케줄링
                }
            }
            adapter.notifyDataSetChanged(); // RecyclerView 갱신
        }
    }
}
