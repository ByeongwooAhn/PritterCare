package com.example.prittercare.view;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prittercare.R;
import com.example.prittercare.databinding.ActivityFindAccountBinding;
import com.example.prittercare.view.findaccount.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FindAccountActivity extends AppCompatActivity {

    private ActivityFindAccountBinding binding;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tabLayout = findViewById(R.id.tab_layout_findaccount);
        viewPager2 = findViewById(R.id.view_pager2_findaccount);

        // ViewPager2 어댑터 설정
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // TabLayout과 ViewPager2 연결
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("아이디 찾기");
                            break;
                        case 1:
                            tab.setText("비밀번호 찾기");
                            break;
                    }
                }).attach();

        binding.layoutToolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(FindAccountActivity.this, R.anim.button_scale));
                finish(); // 현재 Activity 종료
            }
        });
    }
}
