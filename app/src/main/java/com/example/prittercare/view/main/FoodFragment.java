package com.example.prittercare.view.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prittercare.R;

/**
 * Main 화면의 식수 공급 Fragment
 */
public class FoodFragment extends Fragment {

    // 기본 생성자: Fragment는 기본적으로 파라미터가 없는 생성자를 요구함
    public FoodFragment() {
        // Required empty public constructor
    }

    /**
     * Fragment의 UI를 구성하는 메서드
     * 해당 메서드는 onCreateView()에서 레이아웃을 inflate하여 View를 반환함.
     * 이 메서드는 Fragment가 화면에 표시될 때 호출됨.
     *
     * @param inflater LayoutInflater 객체 (XML 레이아웃을 View 객체로 변환)
     * @param container Fragment가 속할 ViewGroup (보통 null로 설정됨)
     * @param savedInstanceState 이전 상태 정보 (복원 용도)
     * @return 생성된 View 객체 (UI 구성)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment_main_food 레이아웃을 inflate하여 뷰 객체 생성 후 반환
        return inflater.inflate(R.layout.fragment_main_food, container, false);
    }
}
