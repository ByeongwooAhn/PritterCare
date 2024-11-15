package com.example.prittercare.view.findaccount;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


/**
 * ViewPagerAdapter : 여러 개의 Fragment를 슬라이드 방식으로 전환하여 보여주는 역할을 함.
 *
 * - Pager : 여러 화면을 하나의 구성 요소처럼 페이지 형식으로 관리하여 스와이프(슬라이드)로 전환할 수 있도록 함.
 * - Adapter : 데이터와 View 간의 다리 역할을 하여, 데이터의 각 항목을 알맞은 View로 연결해줌.
 * - ViewPagerAdapter : Fragment를 ViewPager에 공급하는 Adapter로,
 *   각 페이지에 표시할 Fragment를 생성하고 관리하는 기능을 제공함.
 */

public class ViewPagerAdapter extends FragmentStateAdapter {

    // 생성자 : ViewPagerAdapter의 인스턴스를 생성하고 FragmentActivity를 설정함.
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * createFragment 메서드 : ViewPager의 각 위치(position)에 해당하는 Fragment를 생성하여 반환.
     * position에 따라 IdFragment 또는 PasswordFragment를 반환함.
     *
     * @param position ViewPager의 현재 위치를 나타내는 정수 값
     * @return 생성된 Fragment 객체
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // position 값에 따라 다른 Fragment를 반환
        switch (position) {
            case 0:
                return new IdFragment(); // 0번 위치에 IdFragment 생성
            case 1:
                return new PasswordFragment(); // 1번 위치에 PasswordFragment 생성
            default:
                return new IdFragment(); // 기본으로 IdFragment 반환
        }
    }

    /**
     * getItemCount 메서드 : ViewPager가 표시할 Fragment의 총 개수를 반환.
     * 여기서는 IdFragment와 PasswordFragment 두 개가 있으므로 2를 반환함.
     *
     * @return ViewPager의 페이지 수 (2)
     */
    @Override
    public int getItemCount() {
        return 2; // 총 2개의 페이지가 있음
    }
}
