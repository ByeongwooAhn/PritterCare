package com.example.prittercare.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

        // API 호출 및 데이터 초기화
        loadCageDataFromServer();

        // RecyclerView 설정
        setupRecyclerView();

        binding.layoutCageToolbar.btnCageAdd.setOnClickListener(view -> moveToNewCageActivity());
    }

    private void loadCageDataFromServer() {
        String token = DataManager.getInstance().getUserToken(); // DataManager 에서 토큰 가져오기

        repository.fetchCageList(token, new CageListCallback<CageData>() {
            @Override
            public void onSuccess(List<CageData> cageListResponse) {
                if (cageListResponse == null || cageListResponse.isEmpty()) {
                    moveToNewCageActivity();
                    Intent intent = new Intent(CageListActivity.this, CageAddActivity.class);
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
                showEditDeleteDialog(cage, position);
            }
        });
    }

    private void showEditDeleteDialog(CageData cage, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(cage.getCageName())
                .setMessage("이름을 수정하거나 케이지를 삭제하시겠습니까?")
                .setPositiveButton("이름 수정", (dialog, which) -> showEditDialog(cage, position))
                .setNegativeButton("삭제", (dialog, which) -> deleteCage(cage, position))
                .setNeutralButton("취소", null)
                .show();
    }

    private void showEditDialog(CageData cage, int position) {
        EditText input = new EditText(this);
        input.setText(cage.getCageName()); // 현재 이름 설정
        input.setSelection(cage.getCageName().length()); // 커서를 끝으로 이동

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("케이지 이름 수정")
                .setView(input)
                .setPositiveButton("저장", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        cage.setCageName(newName);
                        DataManager.getInstance().updateCageData(cage); // DataManager 업데이트
                        adapter.notifyItemChanged(position); // RecyclerView 갱신
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void deleteCage(CageData cage, int position) {
        cageList.remove(position); // 리스트에서 제거
        DataManager.getInstance().removeCageData(cage); // DataManager에서 제거
        adapter.notifyItemRemoved(position); // RecyclerView 갱신
    }

    private void moveToNewCageActivity() {
        Intent intent = new Intent(this, CageAddActivity.class);
        startActivity(intent);
    }

    private void moveToMainActivity(CageData cage) {
        Intent intent = new Intent(this, MainActivity.class);
        DataManager.getInstance().setCurrentCageSerialNumber(cage.getCageSerialNumber());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 메모리 누수 방지
    }
}