package com.example.prittercare.view;

import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivityMainBinding;
import com.example.prittercare.model.MQTTHelper;
import com.example.prittercare.view.Activities.MainActivity;
import com.example.prittercare.view.Activities.ReservationActivity;
import com.example.prittercare.view.fragments.FoodFragment;
import com.example.prittercare.view.fragments.LightFragment;
import com.example.prittercare.view.fragments.TemperatureAndHumidtyFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTabManager {

    private final MainActivity activity;
    private final ActivityMainBinding binding;
    private final MQTTHelper mqttHelper;

    private int selectedTab = -1;
    private final List<MainTabView> tabs = new ArrayList<>();
    private final Map<Integer, Fragment> tabFragments = new HashMap<>();

    private static final int TAB_TEMPERATURE = 0;
    private static final int TAB_FOOD = 1;
    private static final int TAB_LIGHT = 2;
    private static final int TAB_RESERVATION = 3;

    public MainTabManager(MainActivity activity, ActivityMainBinding binding, MQTTHelper mqttHelper) {
        this.activity = activity;
        this.binding = binding;
        this.mqttHelper = mqttHelper;
    }

    public void initializeTabs() {
        // 탭 초기화
        tabs.add(new MainTabView(activity, "온도 / 습도", R.drawable.ic_control_temperature_and_humidity));
        tabs.add(new MainTabView(activity, "먹이 / 물", R.drawable.ic_control_food_and_water));
        tabs.add(new MainTabView(activity, "조명", R.drawable.ic_control_light));
        tabs.add(new MainTabView(activity, "예약", R.drawable.ic_control_reservation));

        // Fragment 매핑
        tabFragments.put(TAB_TEMPERATURE, TemperatureAndHumidtyFragment.newInstance(mqttHelper));
        tabFragments.put(TAB_FOOD, FoodFragment.newInstance(mqttHelper));
        tabFragments.put(TAB_LIGHT, LightFragment.newInstance(mqttHelper));
        tabFragments.put(TAB_RESERVATION, null); // 예약은 별도 Activity

        // 탭 컨테이너에 추가
        for (int i = 0; i < tabs.size(); i++) {
            final int tabIndex = i;
            MainTabView tab = tabs.get(i);
            tab.setOnClickListener(v -> selectTab(tabIndex));
            binding.tabContainer.addView(tab);
        }

        // 초기 탭 선택
        selectTab(TAB_TEMPERATURE);
    }

    private void selectTab(int tabIndex) {
        if (selectedTab == tabIndex) return;

        if (tabIndex == TAB_RESERVATION) {
            Intent intent = new Intent(activity, ReservationActivity.class);
            activity.startActivity(intent);
            return;
        }

        Fragment fragment = tabFragments.get(tabIndex);
        if (fragment != null) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(binding.mainFragmentContainer.getId(), fragment);
            transaction.commit();
        } else {
            showToast("프레그먼트가 null 입니다.");
        }

        // 탭 스타일 업데이트
        updateTabStyle(tabIndex);
        selectedTab = tabIndex;
    }

    private void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private void updateTabStyle(int selectedTabIndex) {
        for (int i = 0; i < tabs.size(); i++) {
            if (i == selectedTabIndex) {
                tabs.get(i).selectTab(activity);
            } else {
                tabs.get(i).unSelectTab(activity);
            }
        }
    }
}
