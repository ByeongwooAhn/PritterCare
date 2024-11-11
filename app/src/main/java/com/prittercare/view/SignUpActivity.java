package com.prittercare.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.databinding.ActivitySignupBinding;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    // Retrofit 초기화
    private static final String BASE_URL = "http://medicine.p-e.kr:80"; // Spring Boot 서버 URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Back 버튼 클릭 시 현재 Activity 종료
        binding.layoutToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 Activity 종료
            }
        });

        // Sign Up 버튼 클릭 시
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.tvLoginId.getText().toString();
                String password = binding.tvLoginPw.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                } else {
                    signUp(username, password); // Sign Up 메서드 호출
                }
            }
        });
    }

    private void signUp(String username, String password) {
        // Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // GsonConverterFactory 추가
                .build();

        // API 인터페이스 생성
        ApiService apiService = retrofit.create(ApiService.class);

        // 요청 데이터 생성
        SignUpRequest signUpRequest = new SignUpRequest(username, password);

        // Retrofit 비동기 요청
        Call<Void> call = apiService.signUp(signUpRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 서버에서 성공적으로 응답받은 경우
                    Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                } else {
                    // 서버 응답이 실패한 경우
                    Toast.makeText(SignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();

                    // 실패한 응답 코드 콘솔에 출력
                    Log.e("SignUpError", "Response Code: " + response.code());
                    // 추가적으로 서버에서 반환한 에러 메시지가 있다면 출력할 수 있습니다
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("SignUpError", "Error Body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 오류 등 실패한 경우
                Toast.makeText(SignUpActivity.this, "Network Error", Toast.LENGTH_SHORT).show();

                // 에러 내용 콘솔에 출력
                Log.e("SignUpActivity", "Network Error", t); // 에러 메시지와 함께 스택 트레이스를 로그로 출력
            }
        });
    }
}