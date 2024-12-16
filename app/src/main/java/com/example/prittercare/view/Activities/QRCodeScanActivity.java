package com.example.prittercare.view.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.prittercare.controller.callback.CageRegisterCallback;
import com.example.prittercare.databinding.ActivityQrcodeScanBinding;
import com.example.prittercare.model.DataManager;
import com.example.prittercare.model.DataRepository;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;


public class QRCodeScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;

    private ActivityQrcodeScanBinding binding;

    private boolean isScanning = false; // 스캔 상태를 추적
    private boolean isScanComplete = false; // 스캔 완료 여부

    private boolean isBackPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 3000;

    private boolean isFirstResister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrcodeScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        isFirstResister = intent.getBooleanExtra("isFirstResister", false);
        Log.d("QRCodeScanActivity", "(getIntent) isFristResister : " + isFirstResister);

        // 카메라 권한 체크
        if (checkCameraPermission()) {
            // 카메라 권한이 이미 허용된 경우 스캐너 초기화
            setupScanButtonListener();
            setupBackButtonListener();
        } else {
            // 권한이 없으면 요청
            requestCameraPermission();
        }

        startQRScan();
    }

    // 카메라 권한 체크 메서드
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    // 카메라 권한 요청 메서드
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    // 권한 요청 결과 콜백
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용 시 스캐너 초기화
                setupScanButtonListener();
                setupBackButtonListener();
            } else {
                // 권한 거부 시 사용자에게 알림 후 Activity 종료 or 적절한 처리
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
        if (isFirstResister) {
            Log.d("QRCodeScanActivity", "setupBackButton : 뒤로가기 방식으로 설정");
            binding.toolbarQrcodeScan.btnBack.setOnClickListener(view -> finish());
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    finish();
                }
            });
        } else {
            Log.d("QRCodeScanActivity", "setupBackButton : 로그아웃 방식으로 설정");
            binding.toolbarQrcodeScan.btnBack.setOnClickListener(view -> confirmLogout());
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    handleBackAction();
                }
            });
        }
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
        Log.d("QR CODE SCAN SUCCESS", "SerialNumber : " + scanResult);
        binding.tvQRcodeInfo.setText("QR코드를 확인합니다.");

        // DataManager에 임시로 스캔한 시리얼 넘버를 저장
        DataManager.getInstance().setCurrentCageSerialNumber(scanResult);

        checkSerialNumber();
    }

    private void checkSerialNumber() {
        String token = DataManager.getInstance().getUserToken(); // 저장된 JWT 토큰
        String serialNumber = DataManager.getInstance().getCurrentCageSerialNumber(); // 스캔된 시리얼 넘버

        if (serialNumber == null || token == null) {
            binding.tvQRcodeInfo.setText("QR 코드를 다시 스캔해주세요.");
            Toast.makeText(this, "확인 실패, 다시 스캔해주세요.", Toast.LENGTH_SHORT).show();
            binding.btnScanQRcode.setText("QR 스캔 재개");
            return;
        }

        DataRepository dataRepository = new DataRepository();
        dataRepository.checkSerialNumber(token, serialNumber, new CageRegisterCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(QRCodeScanActivity.this, "시리얼 넘버 확인, 케이지를 등록합니다.", Toast.LENGTH_SHORT).show();
                resisterNewCage(serialNumber);
            }

            @Override
            public void onFailure(Exception e) {
                binding.tvQRcodeInfo.setText("QR 코드를 다시 스캔해주세요.");
                Toast.makeText(QRCodeScanActivity.this, "확인 실패, 다시 스캔해주세요.", Toast.LENGTH_SHORT).show();
                binding.btnScanQRcode.setText("QR 스캔 재개");
            }
        });
    }

    private void resisterNewCage(String newSerialNumber) {
        DataManager.getInstance().setCurrentCageSerialNumber(newSerialNumber);
        moveToCageAddActivity();
    }

    private void startQRScan() {
        isScanning = true;
        binding.btnScanQRcode.setText("QR 스캔 중지");
        binding.scannerQRcode.resume(); // 카메라 활성화
        Toast.makeText(this, "스캔을 시작합니다.", Toast.LENGTH_SHORT).show();
    }

    private void restartQRScan() {
        binding.scannerQRcode.resume(); // 카메라 재개
        binding.tvQRcodeInfo.setText("QR 코드를 스캔합니다.");
        Toast.makeText(this, "다시 스캔합니다.", Toast.LENGTH_SHORT).show();
    }

    private void stopQRScan() {
        isScanning = false;
        binding.btnScanQRcode.setText("QR 스캔 재개");
        binding.scannerQRcode.pause(); // 스캔 멈춤
        Toast.makeText(this, "스캔이 중지되었습니다.", Toast.LENGTH_SHORT).show();
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

    private void moveToCageListActivity() {
        Intent intent = new Intent(QRCodeScanActivity.this, CageListActivity.class);
        startActivity(intent);
        finish();
    }
}
