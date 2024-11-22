package com.example.prittercare.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prittercare.R;
import com.example.prittercare.view.adapters.CageListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CageListActivity extends AppCompatActivity {

    private RecyclerView rvCageList;
    private CageListAdapter adapter;
    private List<Cage> cageList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private Button btnDelete, btnCancel;
    private int selectedPosition = -1; // 꾹 눌린 항목의 위치 저장

    // ActivityResultLauncher 추가
    private ActivityResultLauncher<Intent> addCageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cage_list);

        rvCageList = findViewById(R.id.rvCageList);
        rvCageList.setLayoutManager(new LinearLayoutManager(this));

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("CageData", Context.MODE_PRIVATE);

        // 저장된 데이터 불러오기
        loadSavedData();

        // Adapter 설정
        adapter = new CageListAdapter(this, cageList, position -> {
            // 아이템 꾹 눌렀을 때
            selectedPosition = position;
            btnDelete.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        });
        rvCageList.setAdapter(adapter);

        // 삭제/취소 버튼 초기화
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);

        // ActivityResultLauncher 초기화
        addCageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String cageName = data.getStringExtra("cageName");
                        String animalType = data.getStringExtra("animalType");

                        int imageResId;
                        switch (animalType) {
                            case "햄스터":
                                imageResId = R.drawable.ic_hamster;
                                break;
                            case "물고기":
                                imageResId = R.drawable.ic_fish;
                                break;
                            case "거북이":
                            default:
                                imageResId = R.drawable.ic_turtle;
                                break;
                        }

                        // 새로운 케이지 추가
                        Cage newCage = new Cage(cageName, imageResId);
                        cageList.add(newCage);
                        adapter.notifyItemInserted(cageList.size() - 1);

                        // 데이터 저장
                        saveData();
                    }
                }
        );

        // 삭제 버튼 클릭
        btnDelete.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                cageList.remove(selectedPosition);
                adapter.notifyItemRemoved(selectedPosition);
                selectedPosition = -1;
                saveData(); // 데이터 저장
                hideButtons();
            }
        });

        // 취소 버튼 클릭
        btnCancel.setOnClickListener(v -> {
            selectedPosition = -1;
            hideButtons();
        });
    }

    // 버튼 숨기기
    private void hideButtons() {
        btnDelete.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }

    // 플러스 버튼 클릭 시 등록 화면으로 이동
    public void onAddClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        addCageLauncher.launch(intent); // ActivityResultLauncher로 실행
    }

    // 저장된 데이터 불러오기
    private void loadSavedData() {
        String savedData = sharedPreferences.getString("cageList", "");
        if (!savedData.isEmpty()) {
            String[] items = savedData.split(";");
            for (String item : items) {
                String[] parts = item.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    int imageResId = Integer.parseInt(parts[1]);
                    cageList.add(new Cage(name, imageResId));
                }
            }
        }
    }

    // 데이터 저장
    private void saveData() {
        StringBuilder dataBuilder = new StringBuilder();
        for (Cage cage : cageList) {
            dataBuilder.append(cage.getName())
                    .append(",")
                    .append(cage.getImageResId())
                    .append(";");
        }
        sharedPreferences.edit().putString("cageList", dataBuilder.toString()).apply();
    }

    // 데이터 모델 클래스
    public static class Cage {
        private String name;
        private int imageResId;

        public Cage(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }

        public String getName() {
            return name;
        }

        public int getImageResId() {
            return imageResId;
        }
    }
}
