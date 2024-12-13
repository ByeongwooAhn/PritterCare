package com.example.prittercare.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prittercare.R;
import com.example.prittercare.controller.callback.CageRegisterCallback;
import com.example.prittercare.controller.callback.CageUpdateCallback;
import com.example.prittercare.databinding.ActivityCageAddBinding;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.DataRepository;
import com.example.prittercare.model.data.CageData;

public class CageAddActivity extends AppCompatActivity {

    private ActivityCageAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityCageAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.layoutCageToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CageAddActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 현재 Activity 종료
            }
        });

        binding.btnCageResister.setOnClickListener(v -> {
            addCage();
        });
    }

    private void addCage() {
        String token = DataManager.getInstance().getUserToken();
        String serialNumber = DataManager.getInstance().getCurrentCageSerialNumber();

        if (serialNumber == null || token == null) {
            Toast.makeText(this, "시리얼 넘버나 토큰이 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CageData cageData = new CageData();
        cageData.setCageSerialNumber(serialNumber);
        cageData.setCageName(binding.etCageName.getText().toString());
        cageData.setAnimalType(getAnimalTypeAutomatically(serialNumber));
        cageData.setEnvTemperature(binding.etTemperature.getText().toString());
        cageData.setEnvHumidity(binding.etHumidity.getText().toString());
        cageData.setEnvLighting(binding.etLighting.getText().toString());

        DataRepository dataRepository = new DataRepository();
        dataRepository.addCage(token, cageData, new CageRegisterCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(CageAddActivity.this, "케이지 추가 성공: " + message, Toast.LENGTH_SHORT).show();
                finish(); // 성공 시 Activity 종료
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CageAddActivity.this, "케이지 추가 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAnimalTypeAutomatically(String serialNumber) {
        char type = serialNumber.charAt(0); // 첫 글자 추출

        switch (type) {
            case 'T':
                return "turtle";
            case 'H':
                return "hamster";
            case 'F':
                return "fish";
            default:
                return "";
        }
    }

}