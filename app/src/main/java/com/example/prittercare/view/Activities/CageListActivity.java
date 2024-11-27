package com.example.prittercare.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivityCageListBinding;
import com.example.prittercare.model.data.CageData;
import com.example.prittercare.model.CageListRepository;
import com.example.prittercare.view.adapters.CageListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CageListActivity extends AppCompatActivity {

    private ActivityCageListBinding binding;
    private CageListAdapter adapter;
    private List<CageData> cageList;
    private CageListRepository repository;
    private int selectedPosition = -1; // 꾹 눌린 항목의 위치 저장

    private ActivityResultLauncher<Intent> addCageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityCageListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Repository 초기화
        repository = new CageListRepository(this);
        cageList = repository.loadCages();

        if (cageList == null || cageList.isEmpty()) {
            Log.d("CageListActivity", "No data in repository. Initializing empty list.");
            cageList = new ArrayList<>();
        }

        // RecyclerView 설정
        binding.rvCageList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CageListAdapter(cageList);
        binding.rvCageList.setAdapter(adapter);

        // RecyclerView 클릭 리스너 설정
        adapter.setOnItemClickListener(new CageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CageData cage) {
                // 짧게 클릭 -> MainActivity로 이동
                Intent intent = new Intent(CageListActivity.this, MainActivity.class);
                intent.putExtra("cageData", cage);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(CageData cage, int position) {
                // 길게 클릭 시 Repository에 데이터 저장
                repository.saveSelectedCage(cage); // 선택된 데이터 저장
                selectedPosition = position;
                showButtons();
            }
        });

        // 버튼 초기화 및 이벤트 설정
        setupButtons();
    }

    private void setupButtons() {
        binding.btnCageDelete.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                cageList.remove(selectedPosition);
                adapter.notifyItemRemoved(selectedPosition);
                repository.saveCages(cageList);
                hideButtons();
            }
        });

        binding.btnCageEdit.setOnClickListener(v -> {
            // 편집 버튼 클릭 시 CageListEditActivity로 이동
            if (selectedPosition != -1) {
                Intent intent = new Intent(CageListActivity.this, CageListEditActivity.class);
                startActivity(intent);
                hideButtons();
            }
        });

        binding.btnCageCancel.setOnClickListener(v -> {
            hideButtons();
        });

        binding.layoutCageToolbar.btnCageBack.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(CageListActivity.this, R.anim.button_scale));
            finish();
        });

        binding.layoutCageToolbar.btnCageAdd.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(CageListActivity.this, R.anim.button_scale));
            Intent intent = new Intent(this, CageListEditActivity.class);
            intent.putExtra("isNew", true); // 새 데이터를 추가하는 플래그 설정
            startActivity(intent);
        });

        hideButtons();
    }

    private void showButtons() {
        binding.btnCageDelete.setVisibility(View.VISIBLE);
        binding.btnCageEdit.setVisibility(View.VISIBLE);
        binding.btnCageCancel.setVisibility(View.VISIBLE);
    }

    private void hideButtons() {
        binding.btnCageDelete.setVisibility(View.GONE);
        binding.btnCageEdit.setVisibility(View.GONE);
        binding.btnCageCancel.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 메모리 누수 방지
    }
}