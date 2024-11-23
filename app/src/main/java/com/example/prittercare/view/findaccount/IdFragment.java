// IdFragment.java
package com.example.prittercare.view.findaccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.prittercare.databinding.FragmentFindaccuntIdBinding;

public class IdFragment extends Fragment {

    private FragmentFindaccuntIdBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFindaccuntIdBinding.inflate(inflater, container, false);

        binding.btnFindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "입력한 이메일로 아이디가 전송되었습니다.", Toast.LENGTH_LONG).show();
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
