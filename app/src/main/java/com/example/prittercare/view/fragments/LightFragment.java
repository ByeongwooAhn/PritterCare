package com.example.prittercare.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.prittercare.R;

/**
 * Main 화면의 조명 관리 Fragment
 * 사용자가 조명을 켜고 끄며, 조명 레벨을 조절할 수 있는 기능을 제공
 */
public class LightFragment extends Fragment {

    // 뷰 선언
    private TextView lightLevelText;  // 현재 조명 레벨 텍스트
    private ImageButton btnLeft;  // 왼쪽 버튼 (조명 레벨 감소)
    private ImageButton btnRight;  // 오른쪽 버튼 (조명 레벨 증가)
    private Button btnOn;  // ON 버튼
    private Button btnOff;  // OFF 버튼

    // 조명 레벨 관련 변수
    private int currentLightLevel = 1;  // 초기 조명 레벨 (1부터 5까지)
    private int maxLightLevel = 5;  // 최대 조명 레벨

    // 조명 레벨 컨테이너
    private View containerLightLevel;

    // 조명 상태 (켜짐/꺼짐)
    private boolean isLightOn = true;

    public LightFragment() {
        // 기본 생성자
    }

    public static LightFragment newInstance(String param1, String param2) {
        LightFragment fragment = new LightFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 전달된 인자 처리 (현재는 사용되지 않음)
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment의 레이아웃을 인플레이트하여 rootView 반환
        View rootView = inflater.inflate(R.layout.fragment_main_light, container, false);

        // 뷰 초기화
        lightLevelText = rootView.findViewById(R.id.tv_light_level);
        btnLeft = rootView.findViewById(R.id.btn_left);
        btnRight = rootView.findViewById(R.id.btn_right);
        btnOn = rootView.findViewById(R.id.btn_light_on);
        btnOff = rootView.findViewById(R.id.btn_light_off);
        containerLightLevel = rootView.findViewById(R.id.container_light_level);

        // 초기 조명 레벨 업데이트
        updateLightLevel();

        // ON 버튼 클릭 시 조명 켜기
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonState(btnOn, btnOff);
                turnLightOn();  // 조명 켜기
            }
        });

        // OFF 버튼 클릭 시 조명 끄기
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonState(btnOff, btnOn);
                turnLightOff();  // 조명 끄기
            }
        });

        // 왼쪽 버튼 클릭 시 조명 레벨 감소
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLightOn && currentLightLevel > 1) {
                    currentLightLevel--;
                    updateLightLevel();
                }
            }
        });

        // 오른쪽 버튼 클릭 시 조명 레벨 증가
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLightOn && currentLightLevel < maxLightLevel) {
                    currentLightLevel++;
                    updateLightLevel();
                }
            }
        });

        return rootView;
    }

    // 조명 레벨을 UI에 업데이트
    private void updateLightLevel() {
        if (isLightOn) {
            lightLevelText.setText(currentLightLevel + " 단계");  // 현재 조명 레벨 표시
            lightLevelText.setTextColor(getResources().getColor(getColorForLightLevel(currentLightLevel)));  // 레벨에 맞는 색상 변경
            containerLightLevel.setBackgroundResource(getContainerColorForLightLevel(currentLightLevel));  // 배경 색상 변경
        } else {
            lightLevelText.setText("조명 꺼짐");  // 조명이 꺼지면 텍스트 변경
            lightLevelText.setTextColor(getResources().getColor(R.color.white));  // 꺼짐 상태 텍스트 색상
            containerLightLevel.setBackgroundResource(R.drawable.shape_card_main_light_off);  // 꺼짐 상태 배경
        }
    }

    // 조명 레벨에 맞는 색상 리소스 ID 반환
    private int getColorForLightLevel(int level) {
        switch (level) {
            case 1: return R.color.white;
            case 2: return R.color.white;
            case 3: return R.color.white;
            case 4: return R.color.black;
            case 5: return R.color.black;
            default: return R.color.white;  // 기본 색상
        }
    }

    // 조명 레벨에 맞는 컨테이너 배경 색상 리소스 ID 반환
    private int getContainerColorForLightLevel(int level) {
        switch (level) {
            case 1: return R.drawable.shape_card_main_light_level1;
            case 2: return R.drawable.shape_card_main_light_level2;
            case 3: return R.drawable.shape_card_main_light_level3;
            case 4: return R.drawable.shape_card_main_light_level4;
            case 5: return R.drawable.shape_card_main_light_level5;
            default: return R.drawable.shape_card_main_light_level1;  // 기본 배경
        }
    }

    // 조명을 켜는 메서드
    private void turnLightOn() {
        isLightOn = true;
        updateLightLevel();
    }

    // 조명을 끄는 메서드
    private void turnLightOff() {
        isLightOn = false;
        updateLightLevel();
    }

    // 버튼 상태를 활성화/비활성화 상태로 전환
    private void toggleButtonState(Button activeButton, Button inactiveButton) {
        // 활성화 버튼 스타일
        activeButton.setBackgroundResource(R.drawable.shape_button_main_light_active);
        // 활성화 텍스트 색상
        activeButton.setTextColor(getResources().getColor(R.color.basicColor03));

        // 비활성화 버튼 스타일
        inactiveButton.setBackgroundResource(R.drawable.shape_button_main_primary);
        // 비활성화 텍스트 색상
        inactiveButton.setTextColor(getResources().getColor(R.color.white));
    }
}
