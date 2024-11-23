package com.example.prittercare.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;  // Toast 메시지용 추가

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivitySignupBinding;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    // Retrofit 초기화: 서버의 기본 URL 지정
    private static final String BASE_URL = "http://medicine.p-e.kr"; // Spring Boot 서버 URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뒤로가기 버튼 클릭 시 Activity 종료
        binding.layoutToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.button_scale));
                finish(); // 현재 Activity 종료
            }
        });

        // 회원가입 버튼 클릭 시 동작 설정
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.tvSignupId.getText().toString();
                String password = binding.tvSignupPw.getText().toString();

                // 입력값 체크
                if (username.isEmpty() || password.isEmpty()) {
                    // 둘 중 하나라도 비어있으면 Toast 메시지로 알림
                    Toast.makeText(SignUpActivity.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                } else {
                    // 비어있지 않으면 회원가입 요청 실행
                    signUp(username, password); // 서버에 요청 보냄
                }
            }
        });
    }

    // 회원가입 요청 메서드
    private void signUp(String username, String password) {
        // Retrofit 인스턴스 생성 및 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기
                .build();

        // API 인터페이스 생성
        ApiService apiService = retrofit.create(ApiService.class);

        // 회원가입 요청 데이터 생성
        SignUpRequest signUpRequest = new SignUpRequest(username, password);

        // 비동기식 API 요청
        Call<Void> call = apiService.signUp(signUpRequest);

        // 요청 실행 및 콜백 처리
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 응답이 성공적일 경우
                    Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                } else {
                    // 응답 실패 (ex: 서버 에러)
                    Toast.makeText(SignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();

                    // 로그에 실패 응답 코드 및 에러 내용 출력
                    Log.e("SignUpError", "Response Code: " + response.code());
                    try {
                        // 서버에서 반환한 에러 메시지 로그로 출력
                        String errorBody = response.errorBody().string();
                        Log.e("SignUpError", "Error Body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 실패 또는 예외 발생 시
                Toast.makeText(SignUpActivity.this, "Network Error", Toast.LENGTH_SHORT).show();

                // 로그에 네트워크 오류 출력
                Log.e("SignUpActivity", "Network Error", t); // 에러 메시지와 스택 트레이스
            }
        });
    }
}
