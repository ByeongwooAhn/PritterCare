package com.example.prittercare.view;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.databinding.ActivityLoginBinding;
import com.example.prittercare.model.ApiService;
import com.example.prittercare.model.CageData;
import com.example.prittercare.model.request.LoginRequest;
import com.example.prittercare.model.response.LoginResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private boolean isBackPressedOnce = false;

    private static final String BASE_URL = "http://medicine.p-e.kr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 상단 바 - 뒤로가기 버튼 비활성화
        binding.layoutToolbar.btnBack.setVisibility(View.GONE);

        // 스마트폰 자체 - 뒤로가기 버튼 두번 클릭 시 앱 종료
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isBackPressedOnce) {
                    finishAffinity();
                } else {
                    Toast.makeText(LoginActivity.this, "다시 한번 누르면 앱이 종료 됩니다.", Toast.LENGTH_SHORT).show();
                    isBackPressedOnce = true;
                    new android.os.Handler().postDelayed(() -> isBackPressedOnce = false, 2000);
                }
            }
        });

        // 회원가입 버튼
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToSignUp();
            }
        });

        // 계정 찾기 버튼
        binding.btnFindAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToFindAccount();
            }
        });

        // 로그인 버튼
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.tvLoginId.getText().toString();
                String password = binding.tvLoginPw.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "로그인 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    logIn(username, password); // 로그인 실행
                }
            }
        });
    }

    // 사육장 정보 불러오기 테스트
    private void testCheckCageData(String userName) {
        List<CageData> dummyCages = new ArrayList<>();
        dummyCages.add(new CageData("C123", "햄토리 하우스", "3", "22", "50", "2", "-1"));
        dummyCages.add(new CageData("C124", "닌자 거북이네 집", "2", "28", "70", "3", "5"));

        if (dummyCages != null && !dummyCages.isEmpty()) {
            StringBuilder logMessage = new StringBuilder("###### 사육장 데이터 ######\n");
            for (CageData cage : dummyCages) {
                logMessage.append(String.format(
                        "CageSerialNumber: %s\nCageName: %s\nAnimalType: %s\nTemperature: %s°C\nHumidity: %s%%\nLighting: %s\nWaterLevel: %s\n\n",
                        cage.getCageSerialNumber(),
                        cage.getCageName(),
                        cage.getAnimalType(),
                        cage.getTemperature(),
                        cage.getHumidity(),
                        cage.getLighting(),
                        cage.getWaterLevel()
                ));
            }
            Log.d("CageData", logMessage.toString());
            moveToSelectCage(dummyCages);
        } else {
            Log.d("CageData", "###### 사육장 데이터가 없습니다 ######");
            moveToNewCage();
        }
    }


    // 로그인 요청 메서드
    private void logIn(String username, String password) {
        // Retrofit 인스턴스 생성 및 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // 기본 URL 지정
                .addConverterFactory(GsonConverterFactory.create()) // Gson 컨버터 팩토리 추가
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        LoginRequest loginRequest = new LoginRequest(username, password);

        // 수정된 경로에 맞춰 호출
        Call<Boolean> call = apiService.logIn(loginRequest);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Log.d("LoginResponse", response.body().toString());
                    Boolean isLoginSuccessful = response.body();
                    if (isLoginSuccessful != null && isLoginSuccessful) {
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        testCheckCageData(username);
                    } else {
                        Toast.makeText(LoginActivity.this, "잘못된 아이디 혹은 비밀번호 입니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
                    Log.e("LoginError", "Response Code: " + response.code());
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("LoginError", "Error Body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // 네트워크 오류 등 비정상적인 경우
                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "Network Error", t); // 네트워크 오류 로그 출력
            }
        });
    }

    // 사육장 정보 불러오기 메서드
    private void checkCageData(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<CageData>> call = apiService.getCageData(username);
        call.enqueue(new Callback<List<CageData>>() {
            @Override
            public void onResponse(Call<List<CageData>> call, Response<List<CageData>> response) {
                if (response.isSuccessful()) {
                    List<CageData> cageDataList = response.body();

                    if (cageDataList != null && !cageDataList.isEmpty()) {
                        moveToSelectCage(cageDataList);
                    }else {
                        moveToNewCage();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "사육장 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CageData>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "사육장 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("CageDataError", "Network Error", t);
            }
        });
    }


    private void moveToNewCage() {
        Intent intent = new Intent(LoginActivity.this, CageListEditActivity.class);
        startActivity(intent);
    }

    private void moveToSelectCage(List<CageData> cageDataList) {
        Intent intent = new Intent(LoginActivity.this, CageListActivity.class);
        intent.putParcelableArrayListExtra("cageDataList", new ArrayList<>(cageDataList));
        startActivity(intent);
    }

    private void moveToSignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void moveToFindAccount() {
        Intent intent = new Intent(LoginActivity.this, FindAccountActivity.class);
        startActivity(intent);
    }
}
