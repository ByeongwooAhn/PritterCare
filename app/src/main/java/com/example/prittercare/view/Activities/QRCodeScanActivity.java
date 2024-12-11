package com.example.prittercare.view.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prittercare.databinding.ActivityQrcodeScanBinding;
import com.example.prittercare.model.DataManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

public class QRCodeScanActivity extends AppCompatActivity {

    private ActivityQrcodeScanBinding binding;

    private boolean isScanning = false; // 스캔 상태를 추적
    private boolean isScanComplete = false; // 스캔 완료 여부

    private boolean isBackPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrcodeScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupScanButtonListener();
        setupBackButtonListener();
    }

    private void setupScanButtonListener() {
        // 버튼 초기 설정
        binding.btnScanQRcode.setText("QR 코드 스캔");
        binding.btnScanQRcode.setOnClickListener(v -> handleScanButtonClick());

        // DecoratedBarcodeView 초기화
        binding.scannerQRcode.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                handleQRScanSuccess(result.getText());
            }

            @Override
            public void possibleResultPoints(java.util.List<com.google.zxing.ResultPoint> resultPoints) {
                // 필요 시 QR 코드 예상 위치 처리
            }
        });
    }

    private void setupBackButtonListener() {
        binding.toolbarQrcodeScan.btnBack.setOnClickListener(v -> handleBackAction());
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

    private void handleScanButtonClick() {
        if (!isScanning && !isScanComplete) {
            startQRScan();
        } else if (isScanning) {
            stopQRScan();
        } else if (isScanComplete) {
            restartQRScan();
        }
    }

    private void handleQRScanSuccess(String scanResult) {
        isScanning = false; // 스캔 상태 초기화
        binding.scannerQRcode.pause(); // 스캔 멈춤
        Toast.makeText(this, "스캔 성공. 케이지를 등록합니다.", Toast.LENGTH_SHORT).show();
        Log.d("QR CODE SCAN SUCCESS", "SerialNumber : " + scanResult);
        binding.tvQRcodeInfo.setText("시리얼 넘버 등록에 성공했습니다.");

        checkSerialNumber();
        resisterNewCage(scanResult);
    }

    private void checkSerialNumber() {

    }

    private void resisterNewCage(String newSerialNumber) {
        DataManager.getInstance().setCurrentCageSerialNumber(newSerialNumber);
        moveToCageAddActivity();
    }

    private void startQRScan() {
        isScanning = true;
        binding.btnScanQRcode.setText("QR 스캔 멈추기");
        binding.scannerQRcode.resume(); // 카메라 활성화
        Toast.makeText(this, "QR 코드 스캔을 시작합니다.", Toast.LENGTH_SHORT).show();
    }

    private void restartQRScan() {
        binding.scannerQRcode.resume(); // 카메라 재개
        Toast.makeText(this, "QR 코드를 다시 스캔합니다.", Toast.LENGTH_SHORT).show();
    }

    private void stopQRScan() {
        isScanning = false;
        binding.btnScanQRcode.setText("QR 스캔 재시작");
        binding.scannerQRcode.pause(); // 스캔 멈춤
        Toast.makeText(this, "QR 코드 스캔이 중지되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isScanning) {
            binding.scannerQRcode.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.scannerQRcode.pause();
    }

    private void Logout() {
        DataManager.getInstance().clearData();
        moveToLoginActivity();
    }

    private void moveToLoginActivity() {
        Intent intent = new Intent(QRCodeScanActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToCageAddActivity() {
        Intent intent = new Intent(QRCodeScanActivity.this, CageAddActivity.class);
        startActivity(intent);
        finish();
    }
}
