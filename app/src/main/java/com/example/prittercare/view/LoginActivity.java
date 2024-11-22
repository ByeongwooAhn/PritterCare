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
import com.example.prittercare.model.LoginRequest;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding; // View Binding을 위한 변수
    private boolean isBackPressedOnce = false; // 뒤로 가기 버튼 중복 클릭 확인용 변수

    // Retrofit을 위한 기본 URL 설정
    private static final String BASE_URL = "http://medicine.p-e.kr"; // 서버의 Spring Boot API 엔드포인트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater()); // View Binding 객체 초기화
        setContentView(binding.getRoot()); // View 설정

        // 로그인 화면 상단의 뒤로 가기 버튼 숨기기
        binding.layoutToolbar.btnBack.setVisibility(View.GONE);

        // 뒤로 가기 버튼이 눌렸을 때의 동작 설정
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isBackPressedOnce) {
                    // 두 번 눌렀을 경우 앱 종료
                    finishAffinity();
                } else {
                    // 처음 눌렀을 경우 안내 메시지 표시
                    Toast.makeText(LoginActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                    isBackPressedOnce = true;

                    // 2초 후 isBackPressedOnce 값을 초기화하여 중복 클릭 방지
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isBackPressedOnce = false;
                        }
                    }, 2000); // 2초 설정
                }
            }
        });

        // Sign Up 버튼 클릭 시 SignUpActivity로 이동
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Find Account 버튼 클릭 시 FindAccountActivity로 이동
        binding.btnFindAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindAccountActivity.class);
                startActivity(intent);
            }
        });

        // Login 버튼 클릭 시 ID와 PW 입력 확인 후 로그인 요청
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 성공
                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class); // MainActivity로 이동
                startActivity(intent);

                String username = binding.tvLoginId.getText().toString(); // 사용자 ID 가져오기
                String password = binding.tvLoginPw.getText().toString(); // 사용자 PW 가져오기

                if (username.isEmpty() || password.isEmpty()) {
                    // 입력 필드가 비어있는 경우 메시지 출력
                    Toast.makeText(LoginActivity.this, "로그인 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    logIn(username, password); // 로그인 메서드 호출
                }
            }
        });
    }

    // 로그인 요청 메서드 정의
    private void logIn(String username, String password) {
        // Retrofit 인스턴스 생성 및 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // 기본 URL 지정
                .addConverterFactory(GsonConverterFactory.create()) // Gson 컨버터 팩토리 추가
                .build();

        // API 인터페이스 생성
        ApiService apiService = retrofit.create(ApiService.class);

        // 로그인 요청 데이터 생성
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Retrofit 비동기 요청 호출
        Call<Boolean> call = apiService.logIn(loginRequest); // Boolean 반환 값으로 로그인 결과 받기

        // 서버 응답을 처리하는 콜백 설정
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) { // 응답 성공 시
                    Boolean isLoginSuccessful = response.body(); // 응답 본문에서 결과 가져오기
                    if (isLoginSuccessful != null && isLoginSuccessful) {
                        // 로그인 성공
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class); // MainActivity로 이동
                        startActivity(intent);
                    } else {
                        // 로그인 실패 (잘못된 ID/PW)
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 서버가 오류를 반환한 경우
                    Toast.makeText(LoginActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
                    Log.e("LoginError", "Response Code: " + response.code()); // 오류 코드 로그 출력
                    try {
                        String errorBody = response.errorBody().string(); // 오류 본문 로그 출력
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
}
