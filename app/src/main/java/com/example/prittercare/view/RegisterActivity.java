package com.example.prittercare.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etCageName;
    private RadioGroup rgAnimalType;
    private EditText etTemperature, etHumidity, etLighting, etWaterLevel;
    private View waterLevelLayout; // 수위 설정 레이아웃

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etCageName = findViewById(R.id.etCageName);
        rgAnimalType = findViewById(R.id.rgAnimalType);
        etTemperature = findViewById(R.id.etTemperature);
        etHumidity = findViewById(R.id.etHumidity);
        etLighting = findViewById(R.id.etLighting);
        etWaterLevel = findViewById(R.id.etWaterLevel);
        waterLevelLayout = findViewById(R.id.waterLevelLayout); // 수위 설정 레이아웃

        // 동물 선택 시 수위 설정 표시/숨김 처리
        rgAnimalType.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = findViewById(checkedId);
            if (selectedButton != null) {
                String selectedAnimal = selectedButton.getText().toString();
                if ("햄스터".equals(selectedAnimal)) {
                    waterLevelLayout.setVisibility(View.GONE); // 수위 설정 숨김
                } else {
                    waterLevelLayout.setVisibility(View.VISIBLE); // 수위 설정 표시
                }
            }
        });
    }

    // 뒤로가기 버튼 클릭 시 이전 화면으로 이동
    public void onBackClick(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent); // 취소 결과 반환
        finish(); // 현재 액티비티 종료
    }

    // 완료 버튼 클릭 시 데이터 확인 및 이전 화면으로 전달
    public void onSubmitClick(View view) {
        String cageName = etCageName.getText().toString();
        int selectedAnimalId = rgAnimalType.getCheckedRadioButtonId();
        String selectedAnimal = selectedAnimalId == -1 ? "" : ((RadioButton) findViewById(selectedAnimalId)).getText().toString();

        String temperatureStr = etTemperature.getText().toString();
        String humidityStr = etHumidity.getText().toString();
        String lightingStr = etLighting.getText().toString();
        String waterLevelStr = etWaterLevel.getText().toString();

        // 모든 필드가 입력되었는지 확인
        if (cageName.isEmpty() || selectedAnimal.isEmpty() || temperatureStr.isEmpty() || humidityStr.isEmpty() || lightingStr.isEmpty()
                || (waterLevelLayout.getVisibility() == View.VISIBLE && waterLevelStr.isEmpty())) {
            Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                int temperature = Integer.parseInt(temperatureStr);
                int humidity = Integer.parseInt(humidityStr);
                int lighting = Integer.parseInt(lightingStr);
                int waterLevel = waterLevelLayout.getVisibility() == View.VISIBLE ? Integer.parseInt(waterLevelStr) : -1;

                // 데이터를 이전 화면으로 반환
                Intent resultIntent = new Intent();
                resultIntent.putExtra("cageName", cageName);
                resultIntent.putExtra("animalType", selectedAnimal);
                resultIntent.putExtra("temperature", temperature);
                resultIntent.putExtra("humidity", humidity);
                resultIntent.putExtra("lighting", lighting);
                if (waterLevelLayout.getVisibility() == View.VISIBLE) {
                    resultIntent.putExtra("waterLevel", waterLevel);
                }
                setResult(RESULT_OK, resultIntent); // 성공 결과 반환
                finish(); // 현재 액티비티 종료

            } catch (NumberFormatException e) {
                Toast.makeText(this, "숫자를 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
