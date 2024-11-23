package com.example.prittercare.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivityMainBinding;
import com.example.prittercare.view.main.CustomTab;
import com.example.prittercare.view.main.FoodFragment;
import com.example.prittercare.view.main.LightFragment;
import com.example.prittercare.view.main.ReservationActivity;
import com.example.prittercare.view.main.TemperatureAndHumidtyFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // 탭 정의
    CustomTab tabTemperatureAndHumidity;
    CustomTab tabFoodAndWater;
    CustomTab tabLight;
    CustomTab tabReservation;

    // 탭 상수 정의
    private static final int TAB_INDEX_TEMPERATURE_AND_HUMIDITY = 0;
    private static final int TAB_INDEX_FOOD_AND_WATER = 1;
    private static final int TAB_INDEX_LIGHT = 2;
    private static final int TAB_INDEX_RESERVATION = 3;

    private int selectedTab;

    // animalType 에 따른 탭 정보와 Fragment 매핑
    private List<View> tabViews;
    private Map<Integer, Class<? extends Fragment>> tabFragments;

    private int animalType; // 이 값은 나중에 동적으로 설정될 수 있음.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animalType = 1; // 동물 타입 설정 (예시로 1을 사용)
        selectedTab = TAB_INDEX_TEMPERATURE_AND_HUMIDITY; // 초기 탭 선택 (온도/습도 탭)

        // 탭 초기화
        tabTemperatureAndHumidity = new CustomTab(this, "온도 / 습도", R.drawable.ic_control_temperature_and_humidity);
        tabFoodAndWater = new CustomTab(this, "먹이 / 물", R.drawable.ic_control_food_and_water);
        tabLight = new CustomTab(this, "조명", R.drawable.ic_control_light);
        tabReservation = new CustomTab(this, "예약", R.drawable.ic_control_reservation);

        setupTabs();

        Fragment initialFragment = new TemperatureAndHumidtyFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.fragmentContainer.getId(), initialFragment);
        transaction.commit();

        // 첫 번째 탭의 스타일을 업데이트
        updateTabStyle(selectedTab);

        // 뒤로가기 버튼
        binding.layoutToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale));
                finish(); // 현재 Activity 종료
            }
        });

        binding.ivFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setupTabs() {
        tabFragments = new HashMap<>();
        tabViews = new ArrayList<>();

        // animalType 에 따라 Tab 과 Fragment 추가
        addTab(TAB_INDEX_TEMPERATURE_AND_HUMIDITY, tabTemperatureAndHumidity, TemperatureAndHumidtyFragment.class);
        addTab(TAB_INDEX_FOOD_AND_WATER, tabFoodAndWater, FoodFragment.class);
        addTab(TAB_INDEX_LIGHT, tabLight, LightFragment.class);
        addTab(TAB_INDEX_RESERVATION, tabReservation, null); // 예약 탭은 Fragment 가 아니라 Activity 로 전환
    }

    private void addTab(int newTabNumber, CustomTab newTab, Class<? extends Fragment> newTabFragmentClass) {
        if (newTab == null) return;  // null 체크 추가
        newTab.setOnClickListener(view -> selectTab(newTabNumber));
        binding.tabContainer.addView(newTab);

        if(newTabFragmentClass != null) {
            tabFragments.put(newTabNumber, newTabFragmentClass);
        }
        tabViews.add(newTab);
    }

    private void selectTab(int tabNumber) {
        if (selectedTab == tabNumber) return;

        if (tabNumber == TAB_INDEX_RESERVATION) {
            // 예약 설정 화면을 위한 Activity로 이동
            Intent intent = new Intent(MainActivity.this, ReservationActivity.class);
            startActivity(intent);
            return;
        }

        // 그 외 탭은 Fragment를 교체
        Class<? extends Fragment> fragmentClass = tabFragments.get(tabNumber);
        if (fragmentClass == null) return;

        try {
            Fragment selectedFragment = fragmentClass.newInstance();

            // 애니메이션 설정
            TranslateAnimation slideAnimation = new TranslateAnimation(
                    tabNumber > selectedTab ? 1000 : -1000, 0, 0, 0);
            slideAnimation.setDuration(300);
            binding.fragmentContainer.startAnimation(slideAnimation);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(binding.fragmentContainer.getId(), selectedFragment);
            transaction.commit();

            // 스타일 변경
            updateTabStyle(tabNumber);

            selectedTab = tabNumber;  // 새로운 탭을 선택 상태로 업데이트
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTabStyle(int tabNumber) {
        // 기본 스타일로 설정
        for (int i = 0; i < tabViews.size(); i++) {
            View tab = tabViews.get(i);
            if (i == tabNumber) {
                // 선택된 탭은 setSelectedStyle 메서드를 사용하여 스타일 설정
                ((CustomTab) tab).setSelectedStyle(this);
            } else {
                // 나머지 탭은 setBasicStyle 메서드를 사용하여 기본 스타일 설정
                ((CustomTab) tab).setBasicStyle(this);
            }
        }
    }
}
