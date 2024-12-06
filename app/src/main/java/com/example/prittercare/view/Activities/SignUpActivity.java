package com.example.prittercare.view.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;  // Toast 메시지용 추가

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivitySignupBinding;
import com.example.prittercare.model.ApiService;
import com.example.prittercare.model.request.SignUpRequest;
import com.example.prittercare.view.SimpleTextWatcher;

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

        initializeView();
        setupEventListeners();
    }

    private void initializeView() {
        hideError(binding.containerSignupId, binding.tvSignupIdError);
        hideError(binding.containerSignupPw, binding.tvSignupPwError);
        hideError(binding.containerSignupCheckPw, binding.tvSignupPwCheckError);
        hideError(binding.containerSignupEmail, binding.tvSignupEmailError);
    }

    private void setupEventListeners() {
        setupTextChangeListeners();
        setupBackButtonListener();
        setupSignupButtonListener();
    }

    private void setupSignupButtonListener() {
        // 회원가입 버튼 클릭 시 동작 설정
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.tvSignupId.getText().toString();
                String password = binding.tvSignupPw.getText().toString();
                String email = binding.tvSignupEmail.getText().toString() + "@gmail.com";

                // 입력값 체크
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "값을 다 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 비어있지 않으면 회원가입 요청 실행
                    executeSignUpRequest(username, password, email); // 서버에 요청 보냄
                }
            }
        });

    }

    private void setupBackButtonListener() {
        // 뒤로가기 버튼 클릭 시 Activity 종료
        binding.layoutToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.button_scale));
                finish(); // 현재 Activity 종료
            }
        });
    }

    private void setupTextChangeListeners() {
        binding.tvSignupId.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateUsername();
            }
        });

        binding.tvSignupPw.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validatePassword();
            }
        });

        binding.tvSignupCheckPw.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validatePasswordCheck();
            }
        });

        binding.tvSignupEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });
    }

    private void validateUsername() {
        String username = binding.tvSignupId.getText().toString();
        LinearLayout container = binding.containerSignupId;

        if (username.isEmpty()) {
            showError(container, binding.tvSignupIdError, "아이디를 입력해주세요.");
        } else if (!username.matches("^[a-zA-Z].*")) {
            showError(container, binding.tvSignupIdError, "아이디는 반드시 영문으로 시작해야 합니다.");
        } else if (!username.matches("^[a-zA-Z][a-zA-Z0-9]{7,}$")) {
            showError(container, binding.tvSignupIdError, "아이디는 최소 8자리의 영문/숫자 조합이어야 합니다.");
        } else {
            showSuccess(container, binding.tvSignupIdError, "사용 가능한 아이디입니다.");
        }
    }

    private void validatePassword() {
        String password = binding.tvSignupPw.getText().toString();
        LinearLayout container = binding.containerSignupPw;

        if (password.isEmpty()) {
            showError(container, binding.tvSignupPwError, "비밀번호를 입력해주세요.");
        } else if (password.length() < 8) {
            showError(container, binding.tvSignupPwError, "비밀번호는 최소 8자리 이상이어야 합니다.");
        } else if (!password.matches(".*[a-zA-Z].*")) {
            showError(container, binding.tvSignupPwError, "비밀번호에 최소 1개의 영문자가 포함되어야 합니다.");
        } else if (!password.matches(".*\\d.*")) {
            showError(container, binding.tvSignupPwError, "비밀번호에 최소 1개의 숫자가 포함되어야 합니다.");
        } else if (!password.matches(".*[!@#$%^&*].*")) {
            showError(container, binding.tvSignupPwError, "비밀번호에 최소 1개의 특수문자가 포함되어야 합니다.");
        } else {
            showSuccess(container, binding.tvSignupPwError, "사용 가능한 비밀번호입니다.");
        }
    }

    private void validatePasswordCheck() {
        String password = binding.tvSignupPw.getText().toString();
        String confirmPassword = binding.tvSignupCheckPw.getText().toString();
        LinearLayout container = binding.containerSignupCheckPw;

        if (confirmPassword.isEmpty()) {
            showError(container, binding.tvSignupPwCheckError, "비밀번호 확인란을 입력해주세요.");
        } else if (!password.equals(confirmPassword)) {
            showError(container, binding.tvSignupPwCheckError, "비밀번호가 일치하지 않습니다.");
        } else {
            showSuccess(container, binding.tvSignupPwCheckError, "비밀번호가 일치합니다.");
        }
    }

    private void validateEmail() {
        String email = binding.tvSignupEmail.getText().toString();
        LinearLayout container = binding.containerSignupEmail;

        if (email.isEmpty()) {
            showError(container, binding.tvSignupEmailError, "이메일을 입력해주세요.");
        } else if (!email.matches("^[A-Za-z0-9._%+-]{4,}@.*")) {
            showError(container, binding.tvSignupEmailError, "이메일의 '@' 앞부분은 최소 4자 이상이어야 합니다.");
        } else if (!email.matches("^[A-Za-z0-9._%+-]{4,}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showError(container, binding.tvSignupEmailError, "이메일 형식이 올바르지 않습니다. 예: example@naver.com");
        } else if (!email.contains("@")) {
            showError(container, binding.tvSignupEmailError, "이메일에는 '@' 기호가 포함되어야 합니다.");
        } else if (!email.matches(".*\\.[A-Za-z]{2,}$")) {
            showError(container, binding.tvSignupEmailError, "도메인은 '.com', '.net'과 같은 형식이어야 합니다.");
        } else {
            showSuccess(container, binding.tvSignupEmailError, "사용 가능한 이메일입니다.");
        }
    }

    private void showError(LinearLayout container, TextView errorView, String errorMessage) {
        // 에러 메시지 표시
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(errorMessage);

        // marginBottom 제거
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container.getLayoutParams();
        params.bottomMargin = 0;
        container.setLayoutParams(params);
    }

    private void showSuccess(LinearLayout container, TextView successView, String successMessage) {
        // 성공 메시지 표시
        successView.setVisibility(View.VISIBLE);
        successView.setText(successMessage);
        successView.setTextColor(getResources().getColor(R.color.signupSuccessTextColor)); // 성공 메시지 색상

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container.getLayoutParams();
        params.bottomMargin = 0;
        container.setLayoutParams(params);
    }

    private void hideError(LinearLayout container, TextView errorView) {
        // 에러 메시지 숨기기
        errorView.setVisibility(View.GONE);

        // marginBottom 복원
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) container.getLayoutParams();
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.signup_default_margin_bottom); // 기본 margin 값
        container.setLayoutParams(params);
    }

    // 회원가입 요청 메서드
    private void executeSignUpRequest(String username, String password, String email) {
        // Retrofit 인스턴스 생성 및 설정
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()) // JSON 변환기
                .build();

        // API 인터페이스 생성
        ApiService apiService = retrofit.create(ApiService.class);

        // 회원가입 요청 데이터 생성
        SignUpRequest signUpRequest = new SignUpRequest(username, password, email);

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
