package com.example.prittercare.view;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivityFindAccountBinding;
import com.google.android.material.tabs.TabLayout;
import com.example.prittercare.view.adapters.FindAccountAdapter;

public class FindAccountActivity extends AppCompatActivity {

    // TabLayout과 ViewPager2를 위한 변수 선언
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FindAccountAdapter findAccountAdapter;

    // ViewBinding을 위한 변수 선언
    private ActivityFindAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityFindAccountBinding.inflate(getLayoutInflater()); // ViewBinding 설정
        setContentView(binding.getRoot()); // View 설정

        // TabLayout, ViewPager2 초기화 및 어댑터 설정
        tabLayout = findViewById(R.id.tab_layout_findaccount);
        viewPager2 = findViewById(R.id.view_pager2_findaccount);
        findAccountAdapter = new FindAccountAdapter(this);
        viewPager2.setAdapter(findAccountAdapter);

        // Tab 선택 시 ViewPager 페이지를 이동시키는 리스너 설정
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition()); // 선택된 탭에 해당하는 페이지로 이동
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 탭이 선택 해제될 때의 동작 (현재는 설정 없음)
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 이미 선택된 탭이 다시 선택될 때의 동작 (현재는 설정 없음)
            }
        });

        // ViewPager 페이지 변경 시 TabLayout 탭을 변경하는 콜백 설정
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select(); // 페이지에 맞는 탭 선택
            }
        });

        // 뒤로 가기 버튼 클릭 시 애니메이션 효과 적용 및 액티비티 종료
        binding.layoutToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(FindAccountActivity.this, R.anim.button_scale)); // 버튼 애니메이션 적용
                finish(); // 현재 Activity 종료
            }
        });
    }
}
