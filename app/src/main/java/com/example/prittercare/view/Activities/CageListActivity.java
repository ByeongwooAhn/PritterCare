package com.example.prittercare.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prittercare.controller.callback.CageDeleteCallBack;
import com.example.prittercare.controller.callback.CageListCallback;
import com.example.prittercare.controller.callback.CageUpdateCallback;
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

    private boolean isBackPressedOnce;
    private static final int BACK_PRESS_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityCageListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // RecyclerView 설정
        setupRecyclerView();

        // DataRepository 초기화
        repository = new DataRepository();

        // API 호출 및 데이터 초기화
        loadCageDataFromServer();

        isBackPressedOnce = false;

        binding.layoutCageToolbar.btnCageAdd.setOnClickListener(view ->
                moveToAddNewCage());
        setupBackButtonListener();
    }

    private void setupBackButtonListener() {
        binding.layoutCageToolbar.btnCageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogout();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackAction();
            }
        });
    }

    private void handleBackAction() {
        if (isBackPressedOnce) {
            confirmLogout();
        } else {
            Toast.makeText(getApplicationContext(), "다시 한번 누르면 로그아웃 됩니다.", Toast.LENGTH_SHORT).show();
            isBackPressedOnce = true;
            new android.os.Handler().postDelayed(() -> isBackPressedOnce = false, BACK_PRESS_DELAY);
        }
    }

    private void confirmLogout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("로그아웃 확인")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("아니오", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void performLogout() {
        DataManager.getInstance().clearData();
        moveToLoginActivity();
    }

    private void loadCageDataFromServer() {
        String token = DataManager.getInstance().getUserToken(); // DataManager 에서 토큰 가져오기

        repository.loadCageList(token, new CageListCallback<CageData>() {
            @Override
            public void onSuccess(List<CageData> cageListResponse) {
                if (cageListResponse != null && !cageListResponse.isEmpty()) {
                    for (CageData cage : cageListResponse) {
                        Log.d("CageListActivity", "CageData: " + cage.toString());
                    }
                    updateCageList(cageListResponse);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                resisterFirstCage();
            }
        });
    }

    private void resisterFirstCage() {
        Intent intent = new Intent(CageListActivity.this, QRCodeScanActivity.class);
        intent.putExtra("isFirstResister", true);
        startActivity(intent);
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
                        updateCageName(cage, newName, position);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void updateCageName(CageData cage, String newName, int position) {
        String token = DataManager.getInstance().getUserToken();
        DataRepository repository = new DataRepository();

        repository.updateCageName(token, cage.getCageSerialNumber(), newName, new CageUpdateCallback() {
            @Override
            public void onSuccess(String message) {
                cage.setCageName(newName); // 데이터 업데이트
                DataManager.getInstance().updateCageData(cage);
                adapter.notifyItemChanged(position);
                runOnUiThread(() -> {
                    Log.d("CageListActivity", message);
                    Toast.makeText(getApplicationContext(), "이름을 수정 했습니다.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> {
                    Log.e("CageListActivity", "Failed to update cages name: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "케이지 이름 변경 실패", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void moveToLoginActivity() {
        Intent intent = new Intent(CageListActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void deleteCage(CageData cage, int position) {
        String token = DataManager.getInstance().getUserToken();
        String cageSerialNumber = cage.getCageSerialNumber();

        repository.deleteCage(token, cageSerialNumber, new CageDeleteCallBack() {
            @Override
            public void onSuccess(String response) {
                cageList.remove(position); // 리스트에서 제거
                DataManager.getInstance().removeCageData(cage); // DataManager에서 제거
                adapter.notifyItemRemoved(position); // RecyclerView 갱신

                Log.d("CageListActivity", "Delete Successful: " + response);
            }

            @Override
            public void onFailure(Throwable t) {

                Log.e("CageListActivity", "Failed to delete cage", t);
            }
        });
    }

    private void moveToMainActivity(CageData cage) {
        Intent intent = new Intent(this, MainActivity.class);
        DataManager.getInstance().setCurrentCageSerialNumber(cage.getCageSerialNumber());
        startActivity(intent);
    }

    private void moveToAddNewCage() {
        Intent intent = new Intent(CageListActivity.this, QRCodeScanActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 메모리 누수 방지
    }
}