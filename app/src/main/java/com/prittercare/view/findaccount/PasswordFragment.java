package com.prittercare.view.findaccount;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.prittercare.R;
import com.example.prittercare.databinding.FragmentFindaccountPasswordBinding;

public class PasswordFragment extends Fragment {

    private FragmentFindaccountPasswordBinding binding;

    public PasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFindaccountPasswordBinding.inflate(inflater, container, false);

        binding.btnFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getContext(), "입력한 이메일로\n비밀번호가 전송되었습니다.", Toast.LENGTH_LONG);
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