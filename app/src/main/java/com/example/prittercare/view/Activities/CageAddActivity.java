package com.example.prittercare.view.Activities;

import android.os.Bundle;

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
    }
}