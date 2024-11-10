package com.prittercare.view;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.R;
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
                view.startAnimation(AnimationUtils.loadAnimation(FindAccountActivity.this, R.anim.button_scale));
                finish(); // 현재 Activity 종료
            }
        });
    }
}