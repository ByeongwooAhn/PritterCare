package com.example.prittercare.view.main.reservation;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmEditActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button saveButton, cancelButton;
    private TextView alarmDate;
    private ImageView calendarIcon;
    private Spinner dailyCycleSpinner, hourlyCycleSpinner;
    private Calendar selectedDate;
    private Alarm alarm;
    private int alarmPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_edit);

        timePicker = findViewById(R.id.timePicker);
        alarmDate = findViewById(R.id.alarmDate);
        calendarIcon = findViewById(R.id.calendarIcon);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        dailyCycleSpinner = findViewById(R.id.dailyCycleSpinner);
        hourlyCycleSpinner = findViewById(R.id.hourlyCycleSpinner);

        selectedDate = Calendar.getInstance();
        updateDateText();

        calendarIcon.setOnClickListener(v -> showDatePickerDialog());
        timePicker.setIs24HourView(false);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("alarm_data")) {
            alarm = (Alarm) intent.getSerializableExtra("alarm_data");
            alarmPosition = intent.getIntExtra("alarm_position", -1);
            if (alarm != null) {
                setExistingAlarmData();
            } else {
                Log.e("AlarmEditActivity", "Received null alarm data");
            }
        } else {
            alarm = new Alarm("", "", false, "");
        }

        saveButton.setOnClickListener(v -> saveAlarm());
        cancelButton.setOnClickListener(v -> finish());

        // 일 주기 설정 (0 ~ 7일)
        Integer[] dailyCycleOptions = {0, 1, 2, 3, 4, 5, 6, 7}; // 0 추가
        ArrayAdapter<Integer> dailyCycleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dailyCycleOptions);
        dailyCycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dailyCycleSpinner.setAdapter(dailyCycleAdapter);

        // 시간 주기 설정 (0 ~ 24시간)
        Integer[] hourlyCycleOptions = new Integer[25]; // 0 ~ 24시간 설정
        for (int i = 0; i <= 24; i++) {
            hourlyCycleOptions[i] = i;
        }
        ArrayAdapter<Integer> hourlyCycleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hourlyCycleOptions);
        hourlyCycleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourlyCycleSpinner.setAdapter(hourlyCycleAdapter);

        // 일 주기 Spinner 선택 리스너 설정
        dailyCycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int dailyCycleValue = (Integer) dailyCycleSpinner.getSelectedItem();
                if (dailyCycleValue != 0) { // 일 주기가 0이 아닌 값으로 설정되었을 때
                    hourlyCycleSpinner.setSelection(0); // 시간 주기를 0으로 설정
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택이 없을 때는 아무 동작도 하지 않음
            }
        });

        // 시간 주기 Spinner 선택 리스너 설정
        hourlyCycleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int hourlyCycleValue = (Integer) hourlyCycleSpinner.getSelectedItem();
                if (hourlyCycleValue != 0) { // 시간 주기가 0이 아닌 값으로 설정되었을 때
                    dailyCycleSpinner.setSelection(0); // 일 주기를 0으로 설정
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택이 없을 때는 아무 동작도 하지 않음
            }
        });
    }

    private void showDatePickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        Locale locale = new Locale("ko", "KR");
        Locale.setDefault(locale);

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

    private void updateDateText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 (E)", Locale.KOREAN);
        String formattedDate = dateFormat.format(selectedDate.getTime());
        alarmDate.setText(formattedDate);
    }

    private void setExistingAlarmData() {
        String time = alarm.getTime();
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
                Log.e("AlarmEditActivity", "Unexpected time format: " + time);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(0);
                timePicker.setMinute(0);
            } else {
                timePicker.setCurrentHour(0);
                timePicker.setCurrentMinute(0);
            }
        }
    }

    private void saveAlarm() {
        EditText alarmNameInput = findViewById(R.id.alarmNameInput);
        String alarmName = alarmNameInput.getText().toString();

        int hour, minute;
        boolean isAM;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        isAM = hour < 12;
        String amPm = isAM ? "오전" : "오후";
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;

        String formattedTime = String.format("%s %02d:%02d", amPm, displayHour, minute);
        alarm.setTime(formattedTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 (E)", Locale.KOREAN);
        String formattedDate = dateFormat.format(selectedDate.getTime());
        alarm.setDate(formattedDate);

        alarm.setName(alarmName);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_alarm", alarm);
        resultIntent.putExtra("alarm_position", alarmPosition);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}