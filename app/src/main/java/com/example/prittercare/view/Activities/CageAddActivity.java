package com.example.prittercare.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivityCageAddBinding;

public class CageAddActivity extends AppCompatActivity {

    private ActivityCageAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityCageAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.layoutCageToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CageAddActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 현재 Activity 종료
            }
        });
    }
}