package com.example.prittercare.view.main;

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
import com.example.prittercare.view.main.reservation.Alarm;
import com.example.prittercare.view.main.reservation.AlarmAdapter;
import com.example.prittercare.view.main.reservation.AlarmEditActivity;

import java.util.ArrayList;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private List<Alarm> alarmList;
    private LinearLayout deleteLayout;
    private int selectedPosition = -1;
    private static final int REQUEST_CODE_ADD_ALARM = 1;
    private static final int REQUEST_CODE_EDIT_ALARM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        deleteLayout = findViewById(R.id.deleteLayout);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        alarmList = new ArrayList<>();


        adapter = new AlarmAdapter(alarmList, this::editAlarm, position -> {
            selectedPosition = position;
            deleteLayout.setVisibility(View.VISIBLE); // 롱 클릭 시 삭제 레이아웃 표시
        });
        recyclerView.setAdapter(adapter);

        // 알람 추가 버튼 클릭 시
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
                alarmList.remove(selectedPosition);
                adapter.notifyItemRemoved(selectedPosition);
                deleteLayout.setVisibility(View.GONE);
                selectedPosition = -1;
            }
        });
    }

    // 알람을 수정할 때
    private void editAlarm(int position) {
        Intent intent = new Intent(ReservationActivity.this, AlarmEditActivity.class);
        intent.putExtra("alarm_position", position);
        intent.putExtra("alarm_data", alarmList.get(position));
        startActivityForResult(intent, REQUEST_CODE_EDIT_ALARM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Alarm updatedAlarm = (Alarm) data.getSerializableExtra("updated_alarm");
            if (requestCode == REQUEST_CODE_ADD_ALARM) {
                alarmList.add(updatedAlarm);
            } else if (requestCode == REQUEST_CODE_EDIT_ALARM) {
                int position = data.getIntExtra("alarm_position", -1);
                if (position != -1) {
                    alarmList.set(position, updatedAlarm);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }


}