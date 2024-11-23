package com.example.prittercare.view.findaccount;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.prittercare.databinding.FragmentFindaccountPasswordBinding;

public class PasswordFragment extends Fragment {

    // binding 객체 선언 : 이 객체를 통해 XML 레이아웃에 있는 UI 요소에 접근할 수 있음.
    private FragmentFindaccountPasswordBinding binding;

    // Fragment 기본 생성자
    public PasswordFragment() {
    }

    /**
     * Fragment의 뷰를 생성하고 설정하는 onCreateView 메서드.
     * onCreate에서 초기 설정 작업을 하고, onCreateView에서 UI를 구성하는 View를 생성함.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 'binding' 객체를 초기화 -> XML 레이아웃을 Fragment와 연결함.
        binding = FragmentFindaccountPasswordBinding.inflate(inflater, container, false);

        // 'btnFindPassword' 버튼에 클릭 이벤트 리스너 설정
        binding.btnFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast 메시지: 안드로이드에서 간단한 텍스트 메시지를 잠시 보여주는 UI 컴포넌트.
                // 여기서는 새 비밀번호가 이메일로 전송되었음을 알리는 메시지를 길게(LONG) 표시함.
                Toast toast = Toast.makeText(getContext(), "입력한 이메일로\n새 비밀번호가 전송되었습니다.", Toast.LENGTH_LONG);
                toast.show(); // 메시지를 화면에 표시
            }
        });

        // 'binding.getRoot()'를 통해 Fragment의 최상위 View를 반환함.
        // 이 View는 Fragment가 Activity에 표시될 때 사용됨.
        return binding.getRoot();
    }

    /**
     * onDestroyView 메서드는 Fragment의 뷰가 제거될 때 호출됨.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 메모리 누수를 방지하기 위해 binding 해제
    }
}
