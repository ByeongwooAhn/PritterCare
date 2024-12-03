package com.example.prittercare.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.controller.LogController;
import com.example.prittercare.controller.LoginCallback;
import com.example.prittercare.controller.ToastController;
import com.example.prittercare.databinding.ActivityLoginBinding;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.DataRepository;

public class LoginActivity extends AppCompatActivity {

    private DataRepository repository;
    private ToastController toastController;
    private LogController logController;
    private ActivityLoginBinding binding;
    private boolean isBackPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 초기화 메서드 호출
        initializeControllers();
        setupBackButtonHandler();
        setupButtonListeners();
    }

    // 컨트롤러 초기화
    private void initializeControllers() {
        toastController = new ToastController(this);
        logController = new LogController("LoginActivity");
        repository = new DataRepository();
    }

    // 뒤로가기 버튼 핸들러 설정
    private void setupBackButtonHandler() {
        binding.layoutToolbar.btnBack.setVisibility(View.GONE);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isBackPressedOnce) {
                    finishAffinity();
                } else {
                    toastController.showToast("다시 한번 누르면 앱이 종료 됩니다.");
                    isBackPressedOnce = true;
                    new android.os.Handler().postDelayed(() -> isBackPressedOnce = false, 2000);
                }
            }
        });
    }

    // 버튼 리스너 설정
    private void setupButtonListeners() {
        binding.btnSignup.setOnClickListener(view -> moveToSignUp());
        binding.btnFindAccount.setOnClickListener(view -> moveToFindAccount());
        binding.btnLogin.setOnClickListener(view -> handleLogin());
    }

    // 로그인 버튼 처리
    private void handleLogin() {
        String userName = binding.tvLoginId.getText().toString();
        String password = binding.tvLoginPw.getText().toString();

        if (userName.isEmpty() || password.isEmpty()) {
            toastController.showToast("아이디와 비밀번호를 입력해 주세요.");
            return;
        }

        repository.login(userName, password, new LoginCallback() {
            @Override
            public void onSuccess(String token) {
                toastController.showToast("로그인 성공");
                logController.logInfo("########################\n토큰값 : "+token);
                DataManager.getInstance().setUserName(userName);
                DataManager.getInstance().setToken(token);
                moveToCageList();
            }

            @Override
            public void onFailure(Throwable error) {
                toastController.showToast("로그인 실패");
                logController.logError("로그인 실패", error);
            }
        });
    }

    private void moveToCageList() {
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
}
