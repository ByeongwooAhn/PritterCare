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
import com.example.prittercare.model.CageData;
import com.example.prittercare.model.CageListRepository;

public class CageListEditActivity extends AppCompatActivity {

    private EditText etCageName, etTemperature, etHumidity, etLighting, etWaterLevel;
    private RadioGroup rgAnimalType;
    private View waterLevelLayout;
    private CageListRepository repository;
    private CageData selectedCage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cage_edit);

        // UI 초기화
        etCageName = findViewById(R.id.etCageName);
        rgAnimalType = findViewById(R.id.rgAnimalType);
        etTemperature = findViewById(R.id.etTemperature);
        etHumidity = findViewById(R.id.etHumidity);
        etLighting = findViewById(R.id.etLighting);
        etWaterLevel = findViewById(R.id.etWaterLevel);
        waterLevelLayout = findViewById(R.id.waterLevelLayout);

        // Repository 및 데이터 로드
        repository = new CageListRepository(this);
        selectedCage = repository.loadSelectedCage();

        // UI에 데이터 바인딩
        if (selectedCage != null) {
            etCageName.setText(selectedCage.getCageName());
            etTemperature.setText(selectedCage.getTemperature());
            etHumidity.setText(selectedCage.getHumidity());
            etLighting.setText(selectedCage.getLighting());

            switch (selectedCage.getAnimalType()) {
                case "fish":
                    ((RadioButton) findViewById(R.id.rbFish)).setChecked(true);
                    waterLevelLayout.setVisibility(View.VISIBLE);
                    etWaterLevel.setText(selectedCage.getWaterLevel());
                    break;
                case "turtle":
                    ((RadioButton) findViewById(R.id.rbTurtle)).setChecked(true);
                    waterLevelLayout.setVisibility(View.VISIBLE);
                    etWaterLevel.setText(selectedCage.getWaterLevel());
                    break;
                case "hamster":
                    ((RadioButton) findViewById(R.id.rbHamster)).setChecked(true);
                    waterLevelLayout.setVisibility(View.GONE);
                    break;
            }
        }

        // 동물 타입 선택 시 수위 설정 표시/숨김
        rgAnimalType.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedAnimal = ((RadioButton) findViewById(checkedId)).getText().toString();
            waterLevelLayout.setVisibility("햄스터".equals(selectedAnimal) ? View.GONE : View.VISIBLE);
        });
    }

    // 완료 버튼 클릭 시 데이터 확인 및 저장
    public void onSubmitClick(View view) {
        String cageName = etCageName.getText().toString();
        int selectedAnimalId = rgAnimalType.getCheckedRadioButtonId();
        String selectedAnimal = selectedAnimalId == -1 ? "" : ((RadioButton) findViewById(selectedAnimalId)).getText().toString();
        String temperatureStr = etTemperature.getText().toString();
        String humidityStr = etHumidity.getText().toString();
        String lightingStr = etLighting.getText().toString();
        String waterLevelStr = etWaterLevel.getText().toString();

        if (cageName.isEmpty() || selectedAnimal.isEmpty() || temperatureStr.isEmpty() ||
                humidityStr.isEmpty() || lightingStr.isEmpty() ||
                (waterLevelLayout.getVisibility() == View.VISIBLE && waterLevelStr.isEmpty())) {
            Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            selectedCage.setCageName(cageName);
            selectedCage.setAniamlTyple(selectedAnimal);
            selectedCage.setTemperature(temperatureStr);
            selectedCage.setHumidity(humidityStr);
            selectedCage.setLighting(lightingStr);
            selectedCage.setWaterLevel(waterLevelLayout.getVisibility() == View.VISIBLE ? waterLevelStr : "");

            repository.saveSelectedCage(selectedCage); // 수정된 데이터 저장
            Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "숫자를 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    // 취소 버튼 클릭 시
    public void onBackClick(View view) {
        finish();
    }
}
