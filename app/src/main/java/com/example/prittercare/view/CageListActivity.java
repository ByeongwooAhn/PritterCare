package com.example.prittercare.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.model.CageData;
import com.example.prittercare.model.CageListRepository;
import com.example.prittercare.view.adapters.CageListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CageListActivity extends AppCompatActivity {

    private RecyclerView rvCageList;
    private CageListAdapter adapter;
    private List<CageData> cageList;
    private CageListRepository repository;
    private Button btnDelete, btnCancel, btnEdit;
    private int selectedPosition = -1; // 꾹 눌린 항목의 위치 저장

    private ActivityResultLauncher<Intent> addCageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cage_list);

        // Repository 초기화
        repository = new CageListRepository(this);
        cageList = repository.loadCages();

        if (cageList == null || cageList.isEmpty()) {
            Log.d("CageListActivity", "No data in repository. Initializing empty list.");
            cageList = new ArrayList<>();
        }

        // RecyclerView 설정
        rvCageList = findViewById(R.id.rvCageList);
        rvCageList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CageListAdapter(cageList);
        rvCageList.setAdapter(adapter);

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
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);
        btnEdit = findViewById(R.id.btnEdit);

        btnDelete.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                cageList.remove(selectedPosition);
                adapter.notifyItemRemoved(selectedPosition);
                repository.saveCages(cageList);
                hideButtons();
            }
        });

        btnEdit.setOnClickListener(v -> {
            // 편집 버튼 클릭 시 CageListEditActivity로 이동
            if (selectedPosition != -1) {
                Intent intent = new Intent(CageListActivity.this, CageListEditActivity.class);
                startActivity(intent);
                hideButtons();
            }
        });

        btnCancel.setOnClickListener(v -> {
            hideButtons();
        });

        hideButtons();
    }

    private void showButtons() {
        btnDelete.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
    }

    private void hideButtons() {
        btnDelete.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }

    public void onAddClick(View view) {
        Intent intent = new Intent(this, CageListEditActivity.class);
        addCageLauncher.launch(intent);
    }
}
