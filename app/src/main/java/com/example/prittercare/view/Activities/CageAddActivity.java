package com.example.prittercare.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.R;
import com.example.prittercare.controller.callback.CageRegisterCallback;
import com.example.prittercare.databinding.ActivityCageAddBinding;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.DataRepository;
import com.example.prittercare.model.data.CageData;

import java.util.ArrayList;
import java.util.List;

public class CageAddActivity extends AppCompatActivity {

    private ActivityCageAddBinding binding;

    private String serialNumber;
    private String animalType;

    // 처음 등록 여부 확인 변수
    private boolean isFirstRegister;

    private boolean isBackPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Intent로 전달된 "isFirstRegister" 값 확인
        isFirstRegister = getIntent().getBooleanExtra("isFirstRegister", false);

        // View Binding 초기화
        binding = ActivityCageAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        serialNumber = DataManager.getInstance().getCurrentCageSerialNumber();

        // Back 버튼 리스너
        binding.layoutCageToolbar.btnBack.setOnClickListener(view -> handleBackAction());

        binding.rgAnimalType.check(R.id.rbFish);

        binding.btnCageResister.setOnClickListener(v -> {
            addCage();
        });

        binding.tvInfoCageSerialnumber.setText("시리얼 넘버 : " + serialNumber);

        animalType = getAnimalTypeAutomatically(serialNumber);
        switch (animalType) {
            case "fish":
                binding.rgAnimalType.check(R.id.rbFish);
                break;
            case "hamster":
                binding.rgAnimalType.check(R.id.rbHamster);
                break;
            case "turtle":
                binding.rgAnimalType.check(R.id.rbTurtle);
                break;
        }
    }

    private void addCage() {
        String token = DataManager.getInstance().getUserToken();

        if (serialNumber == null || token == null) {
            Toast.makeText(this, "시리얼 넘버나 토큰이 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        CageData cageData = new CageData();
        cageData.setCageSerialNumber(serialNumber);
        cageData.setCageName(binding.etCageName.getText().toString());
        cageData.setAnimalType(animalType);
        cageData.setEnvTemperature(binding.etTemperature.getText().toString());
        cageData.setEnvHumidity(binding.etHumidity.getText().toString());
        cageData.setEnvLighting(binding.etLighting.getText().toString());

        DataRepository dataRepository = new DataRepository();
        dataRepository.addCage(token, cageData, new CageRegisterCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d("CageAddActivity", "Add Cage -> On Success : " + message);

                // 새 케이지 데이터를 생성하여 DataManager에 추가
                CageData newCage = new CageData();
                newCage.setCageSerialNumber(serialNumber);
                newCage.setCageName(binding.etCageName.getText().toString());
                newCage.setAnimalType(animalType);
                newCage.setEnvTemperature(binding.etTemperature.getText().toString());
                newCage.setEnvHumidity(binding.etHumidity.getText().toString());
                newCage.setEnvLighting(binding.etLighting.getText().toString());

                // DataManager에 추가
                List<CageData> cageList = DataManager.getInstance().getCageList();
                if (cageList != null) {
                    cageList.add(newCage);
                } else {
                    cageList = new ArrayList<>();
                    cageList.add(newCage);
                    DataManager.getInstance().setCageList(cageList);
                }

                finish(); // 액티비티 종료
            }


            @Override
            public void onFailure(Exception e) {
                Log.e("CageAddActivity", "Add Cage -> On Failure : " + e.getMessage());
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

    private void handleBackAction() {
        if (isBackPressedOnce) {
            confirmLogout();
        } else {
            Toast.makeText(getApplicationContext(), "다시 한번 누르면 홈 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
            isBackPressedOnce = true;
            new Handler().postDelayed(() -> isBackPressedOnce = false, BACK_PRESS_DELAY);
        }
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("로그아웃")
                .setMessage("홈 화면으로 돌아 가시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> moveToLoginActivity())
                .setNegativeButton("아니오", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void moveToLoginActivity() {
        Intent intent = new Intent(CageAddActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBackAction();
    }
}
