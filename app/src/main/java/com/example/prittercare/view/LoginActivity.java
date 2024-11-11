package com.example.prittercare.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private boolean isBackPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 로그인 화면에서는 뒤로가기 버튼 숨기기
        binding.layoutToolbar.btnBack.setVisibility(View.GONE);

        // 뒤로가기 버튼 눌렀을 때 Toast 메시지 표시
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isBackPressedOnce) {
                    // If back button pressed twice, exit the activity
                    finishAffinity(); // or call System.exit(0) to terminate the app
                } else {
                    // Show a toast message on first back button press
                    Toast.makeText(LoginActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                    isBackPressedOnce = true;

                    // Reset the flag after 2 seconds
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isBackPressedOnce = false;
                        }
                    }, 2000); // Adjust time as needed
                }
            }
        });

        // Sign Up 버튼 클릭 시 SignInActivity로 이동
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

        // Sign Up 버튼 클릭 시 SignInActivity로 이동
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
