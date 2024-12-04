package com.example.prittercare.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.view.animation.AnimationUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prittercare.R;
import com.example.prittercare.controller.CageListCallback;
import com.example.prittercare.databinding.ActivityCageListBinding;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.DataRepository;
import com.example.prittercare.model.data.CageData;
import com.example.prittercare.view.adapters.CageListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CageListActivity extends AppCompatActivity {

    private ActivityCageListBinding binding;
    private CageListAdapter adapter;
    private List<CageData> cageList;
    private int selectedPosition = -1;

    private DataRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityCageListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // DataRepository 초기화
        repository = new DataRepository();

        // RecyclerView 설정
        setupRecyclerView();

        // API 호출 및 데이터 초기화
        fetchCageList();

        // 버튼 초기화 및 이벤트 설정
        setupButtons();
    }

    private void fetchCageList() {
        String token = DataManager.getInstance().getUserToken(); // DataManager 에서 토큰 가져오기

        repository.fetchCageList(token, new CageListCallback<CageData>() {
            @Override
            public void onSuccess(List<CageData> cageListResponse) {
                if (cageListResponse == null || cageListResponse.isEmpty()) {
                    moveToNewCageActivity();
                    Intent intent = new Intent(CageListActivity.this, CageListEditActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    for (CageData cage : cageListResponse) {
                        Log.d("CageListActivity", "CageData: " + cage.toString());
                    }
                    updateCageList(cageListResponse);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("CageListActivity", "Cage 리스트를 가져오는 데 실패했습니다.", error);
            }
        });
    }

    private void updateCageList(List<CageData> cageListResponse) {
        if (cageList == null) {
            cageList = new ArrayList<>();
        }
        cageList.clear();
        if (cageListResponse != null) {
            cageList.addAll(cageListResponse);
        }
        DataManager.getInstance().setCageList(cageList);
        adapter.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        if (adapter == null) {
            cageList = new ArrayList<>();
        }
        binding.rvCageList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CageListAdapter(cageList);
        binding.rvCageList.setAdapter(adapter);

        // recyclerView 클릭 리스너 설정
        adapter.setOnItemClickListener(new CageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CageData cage) {
                // 짧게 클릭 -> MainActivity 로 이동
                moveToMainActivity(cage);
            }

            @Override
            public void onItemLongClick(CageData cage, int position) {
                selectedPosition = position;
                showButtons();
            }
        });
    }

    private void setupButtons() {
        binding.btnCageDelete.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                cageList.remove(selectedPosition);
                adapter.notifyItemRemoved(selectedPosition);
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
            startActivity(intent);
        });

        hideButtons();
    }

    private void moveToNewCageActivity() {
        Intent intent = new Intent(this, CageListEditActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToMainActivity(CageData cage) {
        Intent intent = new Intent(this, MainActivity.class);
        DataManager.getInstance().setCurrentCageSerialNumber(cage.getCageSerialNumber());
        startActivity(intent);
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