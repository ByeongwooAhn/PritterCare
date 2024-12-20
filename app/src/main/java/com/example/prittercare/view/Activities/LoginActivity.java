package com.example.prittercare.view.Activities;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.databinding.ActivityLoginBinding;
import com.example.prittercare.model.ApiResponse;
import com.example.prittercare.model.ApiService;
import com.example.prittercare.model.data.CageData;
import com.example.prittercare.model.CageListRepository;
import com.example.prittercare.model.request.LoginRequest;

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

        // 뒤로가기 버튼 설정
        binding.layoutToolbar.btnBack.setVisibility(View.GONE);

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
                    //testLogin(); // 서버 대체용 테스트 로그인 메서드
                    logIn(username, password); // 로그인 실행
                }
            }
        });
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

        apiService.logIn(loginRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    List<CageData> cages = apiResponse.getCages();

                    if (cages != null && !cages.isEmpty()) {
                        showToast("로그인 성공");
                        moveToSelectCage(cages);
                    } else {
                        showToast("사용자 데이터를 불러오지 못했습니다.");
                    }
                } else {
                    showToast("로그인 실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showToast("네트워크 오류 발생");
                Log.e("LoginActivity", "Network Error", t);
            }
        });
    }

    private void moveToNewCage() {
        Intent intent = new Intent(LoginActivity.this, CageListEditActivity.class);
        startActivity(intent);
    }

    private void moveToSelectCage(List<CageData> cageDataList) {
        // Repository 에 데이터 저장
        CageListRepository repository = new CageListRepository(this);
        repository.saveCages(cageDataList);

        // 다음 화면으로 이동
        Intent intent = new Intent(LoginActivity.this, CageListActivity.class);
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
