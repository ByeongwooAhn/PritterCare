package com.example.prittercare.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prittercare.R;

public class TemperatureAndHumidtyFragment extends Fragment {

    // 기본 생성자 (필수)
    public TemperatureAndHumidtyFragment() {
        // Required empty public constructor
    }

    // 새로운 인스턴스를 생성하는 메소드 (매개변수를 받지만 현재는 사용하지 않음)
    public static TemperatureAndHumidtyFragment newInstance(String param1, String param2) {
        TemperatureAndHumidtyFragment fragment = new TemperatureAndHumidtyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args); // 인자 전달
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 해당 프래그먼트의 레이아웃을 inflate하여 반환
        return inflater.inflate(R.layout.fragment_main_temperature_and_humidty, container, false);
    }
}
