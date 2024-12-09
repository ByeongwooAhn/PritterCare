package com.example.prittercare.view.Activities;

import static com.example.prittercare.R.*;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.R;
import com.example.prittercare.model.data.ReservationData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * AlarmEditActivity 클래스
 * 예약 데이터를 추가하거나 수정하는 액티비티입니다.
 */
public class AlarmEditActivity extends AppCompatActivity {

    // UI 요소
    private TimePicker timePicker;
    private Button saveButton, cancelButton;
    private TextView reservationDate;
    private ImageView calendarIcon;
    private Spinner dailyCycleSpinner, hourlyCycleSpinner;

    // 예약 데이터 관련 필드
    private Calendar selectedDate; // 선택된 날짜
    private ReservationData alarm; // 예약 데이터 객체
    private int alarmPosition = -1; // RecyclerView에서의 예약 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_edit);

        // UI 요소 초기화
        timePicker = findViewById(R.id.timePicker);
        reservationDate = findViewById(R.id.alarmDate);
        calendarIcon = findViewById(R.id.calendarIcon);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        dailyCycleSpinner = findViewById(R.id.dailyCycleSpinner);
        hourlyCycleSpinner = findViewById(R.id.hourlyCycleSpinner);

        // 현재 날짜를 기본값으로 설정
        selectedDate = Calendar.getInstance();
        updateDateText();

        // 달력 아이콘 클릭 시 날짜 선택 다이얼로그 표시
        calendarIcon.setOnClickListener(v -> showDatePickerDialog());

        // TimePicker를 24시간 형식으로 설정
        timePicker.setIs24HourView(false);

        // 인텐트에서 기존 예약 데이터 가져오기
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("alarm_data")) {
            alarm = (ReservationData) intent.getSerializableExtra("alarm_data");
            alarmPosition = intent.getIntExtra("alarm_position", -1);
            if (alarm != null) {
                setExistingAlarmData(); // 기존 데이터 UI에 표시
            } else {
                Log.e("AlarmEditActivity", "Received null alarm data");
            }
        } else {
            // 새로운 예약 데이터 생성 (기본값 포함)
            alarm = new ReservationData("", "", "", "");
        }

        // 저장 버튼 클릭 시 예약 데이터 저장
        saveButton.setOnClickListener(v -> saveAlarm());
        // 취소 버튼 클릭 시 액티비티 종료
        cancelButton.setOnClickListener(v -> finish());

        // 예약 타입 선택 라디오 그룹
        RadioGroup typeSelectionGroup = findViewById(R.id.typeSelectionGroup);
        TextView lightLevelLabel = findViewById(R.id.lightLevelLabel);
        SeekBar lightLevelSeekBar = findViewById(R.id.lightLevelSeekBar);

        // 예약 타입에 따른 조명 단계 UI 표시 여부 설정
        typeSelectionGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLight) {
                lightLevelLabel.setVisibility(View.VISIBLE);
                lightLevelSeekBar.setVisibility(View.VISIBLE);
            } else {
                lightLevelLabel.setVisibility(View.GONE);
                lightLevelSeekBar.setVisibility(View.GONE);
            }
        });

        // 조명 단계 변경 리스너
        lightLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lightLevelLabel.setText("조명 단계 (0~5): " + progress);
                alarm.setLightLevel(progress); // 조명 단계 저장
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 일 주기 스피너 설정 (0 ~ 7일)
        Integer[] dailyCycleOptions = {0, 1, 2, 3, 4, 5, 6, 7};
        ArrayAdapter<Integer> dailyCycleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dailyCycleOptions);
        dailyCycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dailyCycleSpinner.setAdapter(dailyCycleAdapter);

        // 시간 주기 스피너 설정 (0 ~ 24시간)
        Integer[] hourlyCycleOptions = new Integer[25];
        for (int i = 0; i <= 24; i++) {
            hourlyCycleOptions[i] = i;
        }
        ArrayAdapter<Integer> hourlyCycleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hourlyCycleOptions);
        hourlyCycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourlyCycleSpinner.setAdapter(hourlyCycleAdapter);

        // 스피너 선택 리스너 (일 주기와 시간 주기가 동시에 설정되지 않도록 처리)
        dailyCycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int dailyCycleValue = (Integer) dailyCycleSpinner.getSelectedItem();
                if (dailyCycleValue != 0) {
                    hourlyCycleSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        hourlyCycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int hourlyCycleValue = (Integer) hourlyCycleSpinner.getSelectedItem();
                if (hourlyCycleValue != 0) {
                    dailyCycleSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // 날짜 선택 다이얼로그 표시
    private void showDatePickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    updateDateText();
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    // 날짜 텍스트 업데이트
    private void updateDateText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 (E)", Locale.KOREAN);
        reservationDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    // 기존 예약 데이터를 UI에 설정
    private void setExistingAlarmData() {
        String time = alarm.getReserveTime();
        int hour, minute;

        try {
            if (time.startsWith("오전")) {
                time = time.replace("오전 ", "");
                String[] timeParts = time.split(":");
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
            } else if (time.startsWith("오후")) {
                time = time.replace("오후 ", "");
                String[] timeParts = time.split(":");
                hour = Integer.parseInt(timeParts[0]) + 12;
                minute = Integer.parseInt(timeParts[1]);
            } else {
                hour = 0;
                minute = 0;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
            } else {
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(minute);
            }
        } catch (NumberFormatException e) {
            Log.e("AlarmEditActivity", "Invalid time format", e);
        }
    }

    // 예약 데이터 저장
    private void saveAlarm() {
        EditText alarmNameInput = findViewById(R.id.alarmNameInput);
        String alarmName = alarmNameInput.getText().toString();

        RadioGroup typeSelectionGroup = findViewById(R.id.typeSelectionGroup);
        int selectedTypeId = typeSelectionGroup.getCheckedRadioButtonId();
        String type = "";

        if (selectedTypeId == R.id.radioWater) {
            type = "water";
        } else if (selectedTypeId == R.id.radioFood) {
            type = "food";
        } else if (selectedTypeId == R.id.radioLight) {
            type = "light";
        }

        alarm.setReserveName(alarmName);
        alarm.setReserveType(type);

        // 주기 설정 저장
        int dailyCycle = (int) dailyCycleSpinner.getSelectedItem();
        int hourlyCycle = (int) hourlyCycleSpinner.getSelectedItem();
        alarm.setDayLoop(dailyCycle);
        alarm.setTimeLoop(hourlyCycle);

        // 시간 설정
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        String amPm = hour < 12 ? "오전" : "오후";
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;

        alarm.setReserveTime(String.format("%s %02d:%02d", amPm, displayHour, minute));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 (E)", Locale.KOREAN);
        alarm.setReserveDate(dateFormat.format(selectedDate.getTime()));

        // 결과 데이터 반환
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_alarm", alarm);
        resultIntent.putExtra("alarm_position", alarmPosition);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}