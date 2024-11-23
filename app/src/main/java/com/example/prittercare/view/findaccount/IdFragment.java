package com.example.prittercare.view.findaccount;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.prittercare.databinding.FragmentFindaccuntIdBinding;

public class IdFragment extends Fragment {

    private FragmentFindaccuntIdBinding binding;

    public IdFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // binding 초기화 및 레이아웃 설정은 onCreateView에서 수행
        binding = FragmentFindaccuntIdBinding.inflate(inflater, container, false);

        binding.btnFindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getContext(), "입력한 이메일로\n아이디가 전송되었습니다.", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수를 방지하기 위해 binding 해제
    }
}