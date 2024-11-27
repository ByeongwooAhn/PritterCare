package com.example.prittercare.model;

import android.content.Context;
import android.graphics.Color;

import com.example.prittercare.R;

public class AnimalColorManager {

    private int basicColor01;
    private int basicColor02;
    private int basicColor03;

    public AnimalColorManager(String animalType) {
        setColorsByAnimalType(animalType);
    }

    // 동물 타입에 따라 색상 설정
    private void setColorsByAnimalType(String animalType) {
        switch (animalType.toLowerCase()) {
            case "fish":
                // 물고기: 푸른 계열
                basicColor01 = Color.parseColor("#ADD8E6"); // 연한 하늘색
                basicColor02 = Color.parseColor("#4682B4"); // 스틸 블루
                basicColor03 = Color.parseColor("#0000CD"); // 중간 파랑
                break;
            case "hamster":
                // 햄스터: 주황 계열
                basicColor01 = Color.parseColor("#FFA07A"); // 연한 살구색
                basicColor02 = Color.parseColor("#FF8C00"); // 다크 오렌지
                basicColor03 = Color.parseColor("#8B4513"); // 새들 브라운
                break;
            case "turtle":
                // 거북이: 초록 계열
                basicColor01 = Color.parseColor("#90EE90"); // 연한 초록
                basicColor02 = Color.parseColor("#32CD32"); // 라임 그린
                basicColor03 = Color.parseColor("#006400"); // 다크 그린
                break;
            default:
                // 기본 색상: 회색 계열
                basicColor01 = Color.parseColor("#D3D3D3"); // 연한 회색
                basicColor02 = Color.parseColor("#A9A9A9"); // 어두운 회색
                basicColor03 = Color.parseColor("#808080"); // 중간 회색
                break;
        }
    }

    // 각 색상을 개별적으로 가져오는 메서드
    public int getBasicColor01() {
        return basicColor01;
    }

    public int getBasicColor02() {
        return basicColor02;
    }

    public int getBasicColor03() {
        return basicColor03;
    }
}