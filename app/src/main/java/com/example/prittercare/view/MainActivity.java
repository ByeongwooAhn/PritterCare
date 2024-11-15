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

    private ActivityMainBinding binding; // UI 요소를 직접 참조하지 않고 사용할 수 있게 해주는 바인딩 객체

    // 각 탭을 나타내는 CustomTab 인스턴스
    CustomTab tabTemperatureAndHumidity;
    CustomTab tabFoodAndWater;
    CustomTab tabLight;
    CustomTab tabReservation;

    // 탭 상수 (탭 번호를 구분하는 데 사용)
    private static final int TAB_INDEX_TEMPERATURE_AND_HUMIDITY = 0;
    private static final int TAB_INDEX_FOOD_AND_WATER = 1;
    private static final int TAB_INDEX_LIGHT = 2;
    private static final int TAB_INDEX_RESERVATION = 3;

    private int selectedTab; // 현재 선택된 탭을 저장

    // 탭을 구성하는 View 리스트와 Fragment 매핑 정보
    private List<View> tabViews;
    private Map<Integer, Class<? extends Fragment>> tabFragments;

    private int animalType; // 특정 동물 타입에 따른 탭 구성을 결정하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        animalType = 1; // 동물 타입 (예시로 1을 사용)
        selectedTab = TAB_INDEX_TEMPERATURE_AND_HUMIDITY; // 초기 탭 설정 (온도/습도 탭)

        // 각 탭에 대한 정보 초기화
        tabTemperatureAndHumidity = new CustomTab(this, "온도 / 습도", R.drawable.ic_control_temperature_and_humidity);
        tabFoodAndWater = new CustomTab(this, "먹이 / 물", R.drawable.ic_control_food_and_water);
        tabLight = new CustomTab(this, "조명", R.drawable.ic_control_light);
        tabReservation = new CustomTab(this, "예약", R.drawable.ic_control_reservation);

        setupTabs(); // 각 탭과 매핑된 Fragment 초기화

        // 첫 화면에 보일 초기 Fragment 설정
        Fragment initialFragment = new TemperatureAndHumidtyFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.fragmentContainer.getId(), initialFragment);
        transaction.commit();

        updateTabStyle(selectedTab); // 초기 탭 스타일 설정

        // 뒤로가기 버튼 클릭 시 애니메이션과 함께 현재 Activity 종료
        binding.layoutToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.button_scale));
                finish();
            }
        });

        // 전체 화면 버튼 클릭 이벤트
        binding.ivFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 전체 화면 기능 구현 필요
            }
        });
    }

    private void setupTabs() {
        tabFragments = new HashMap<>(); // 탭 번호와 Fragment 클래스 매핑
        tabViews = new ArrayList<>(); // 탭을 담는 리스트

        // 각 탭 번호와 Fragment 클래스를 연결
        addTab(TAB_INDEX_TEMPERATURE_AND_HUMIDITY, tabTemperatureAndHumidity, TemperatureAndHumidtyFragment.class);
        addTab(TAB_INDEX_FOOD_AND_WATER, tabFoodAndWater, FoodFragment.class);
        addTab(TAB_INDEX_LIGHT, tabLight, LightFragment.class);
        addTab(TAB_INDEX_RESERVATION, tabReservation, null); // 예약 탭은 Fragment가 아닌 별도의 Activity로 전환
    }

    private void addTab(int newTabNumber, CustomTab newTab, Class<? extends Fragment> newTabFragmentClass) {
        if (newTab == null) return; // 탭이 null인 경우 추가하지 않음
        newTab.setOnClickListener(view -> selectTab(newTabNumber)); // 탭 클릭 시 선택된 탭으로 전환
        binding.tabContainer.addView(newTab); // 탭 컨테이너에 탭 추가

        if(newTabFragmentClass != null) {
            tabFragments.put(newTabNumber, newTabFragmentClass); // 탭 번호와 Fragment 매핑 추가
        }
        tabViews.add(newTab); // 탭을 리스트에 추가
    }

    private void selectTab(int tabNumber) {
        if (selectedTab == tabNumber) return; // 이미 선택된 탭인 경우 무시

        if (tabNumber == TAB_INDEX_RESERVATION) {
            // 예약 탭 클릭 시, ReservationActivity로 전환
            Intent intent = new Intent(MainActivity.this, ReservationActivity.class);
            startActivity(intent);
            return;
        }

        // 다른 탭 클릭 시 해당 Fragment로 교체
        Class<? extends Fragment> fragmentClass = tabFragments.get(tabNumber);
        if (fragmentClass == null) return; // Fragment 매핑이 없는 경우 무시

        try {
            Fragment selectedFragment = fragmentClass.newInstance();

            // 탭 이동 시 슬라이드 애니메이션 설정
            TranslateAnimation slideAnimation = new TranslateAnimation(
                    tabNumber > selectedTab ? 1000 : -1000, 0, 0, 0);
            slideAnimation.setDuration(300);
            binding.fragmentContainer.startAnimation(slideAnimation);

            // 선택된 Fragment로 교체하고 트랜잭션 커밋
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(binding.fragmentContainer.getId(), selectedFragment);
            transaction.commit();

            updateTabStyle(tabNumber); // 선택된 탭의 스타일 업데이트

            selectedTab = tabNumber;  // 현재 선택된 탭 업데이트
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTabStyle(int tabNumber) {
        // 모든 탭을 기본 스타일로 설정하고 선택된 탭만 스타일 변경
        for (int i = 0; i < tabViews.size(); i++) {
            View tab = tabViews.get(i);
            if (i == tabNumber) {
                // 선택된 탭 스타일 설정
                ((CustomTab) tab).setSelectedStyle(this);
            } else {
                // 기본 탭 스타일 설정
                ((CustomTab) tab).setBasicStyle(this);
            }
        }
    }
}
