package com.example.prittercare.view.main;

import static android.view.Gravity.CENTER;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.prittercare.R;

public class CustomTab extends FrameLayout {

    private ImageView imageView;
    private TextView textView;

    public CustomTab(Context context, String tabLabel, int iconResId) {
        super(context);
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        init(context, tabLabel, iconResId);
    }

    // 초기화 및 스타일 적용
    private void init(Context context, String tabLabel, int iconResId) {
        // FrameLayout 속성 설정
        this.setLayoutParams(new LayoutParams(
                dpToPx(context, 80), dpToPx(context, 80)));
        this.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_control));

        // 내부의 ImageView 생성 및 속성 설정
        imageView = new ImageView(context);
        imageView.setLayoutParams(new LayoutParams(
                dpToPx(context, 40), dpToPx(context, 40)));
        imageView.setImageResource(iconResId);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams imageParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imageParams.gravity = CENTER;
        imageParams.bottomMargin = dpToPx(context, 12);
        imageView.setLayoutParams(imageParams);

        // 내부의 TextView 생성 및 속성 설정
        textView = new TextView(context);
        textView.setText(tabLabel);
        textView.setTextColor(ContextCompat.getColor(context, R.color.controlButtonTextColor));
        textView.setTextSize(12);
        textView.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        LayoutParams textParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.gravity = CENTER;
        textParams.topMargin = dpToPx(context, 22);
        textView.setLayoutParams(textParams);

        // FrameLayout에 ImageView와 TextView 추가
        this.addView(imageView);
        this.addView(textView);

        // 여백 설정
        LayoutParams params = (LayoutParams) this.getLayoutParams();
        params.topMargin = dpToPx(context, 10);
        params.bottomMargin = dpToPx(context, 10);
        params.leftMargin = dpToPx(context, 5);
        params.rightMargin = dpToPx(context, 5);
        this.setLayoutParams(params);
    }

    // 탭 선택 시 스타일 변경 메서드
    public void setSelectedStyle(Context context) {
        if (context == null) {
            return;
        }
        // 선택된 상태 배경으로 변경
        this.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_control_selected));

        // 아이콘 색상 변경
        imageView.setColorFilter(ContextCompat.getColor(context, R.color.controlButtonSelectedIconColor));

        // 텍스트 색상과 스타일 변경
        textView.setTextColor(ContextCompat.getColor(context, R.color.controlButtonSelectedTextColor));
        textView.setTextSize(12); // 텍스트 크기 유지 (필요시 변경)
        textView.setTypeface(null, android.graphics.Typeface.BOLD);  // 텍스트 굵게
    }

    // 탭 선택 해제 시 스타일 변경 메서드
    public void setBasicStyle(Context context) {
        if (context == null) {
            return;
        }
        // 기본 상태 배경으로 변경
        this.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button_control));

        // 아이콘 색상 초기화
        imageView.setColorFilter(null);

        // 텍스트 색상 및 스타일 초기화
        textView.setTextColor(ContextCompat.getColor(context, R.color.controlButtonTextColor));
        textView.setTextSize(12); // 텍스트 크기 유지 (필요시 변경)
        textView.setTypeface(null, android.graphics.Typeface.NORMAL);  // 텍스트 스타일 기본으로
    }

    // dp를 px로 변환하는 유틸리티 메서드
    private int dpToPx(Context context, float dp) {
        if (context == null) {
            return 0; // Context가 없을 때 안전하게 0 반환
        }
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
