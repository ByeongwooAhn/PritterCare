package com.prittercare.view;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.databinding.ActivityFindAccountBinding;

public class FindAccountActivity extends AppCompatActivity {

    private ActivityFindAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layoutToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 Activity 종료
            }
        });
    }
}