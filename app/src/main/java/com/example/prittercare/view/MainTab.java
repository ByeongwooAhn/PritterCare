package com.example.prittercare.view;

import static android.view.Gravity.CENTER;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.prittercare.R;

/**
 * 해당 부분은 Main 화면의 Button Tap 화면을 구성하는 레이아웃으로
 * 백엔드에서는 건드릴 것이 없을 것으로 예상됨.
 */

public class MainTab extends FrameLayout {

    // 탭 아이콘과 텍스트를 보여줄 ImageView와 TextView 선언
    private ImageView imageView;
    private TextView textView;

    /**
     * CustomTab 생성자 : 각 탭의 초기 텍스트와 아이콘 리소스를 받아 탭을 초기화
     * @param context Context는 UI 작업에 필요한 필수 객체로, 여기서는 Activity나 Application을 의미함
     * @param tabLabel 탭에 표시할 텍스트 (탭 라벨)
     * @param iconResId 탭에 사용할 아이콘 리소스 ID
     */
    public MainTab(Context context, String tabLabel, int iconResId) {
        super(context);
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        init(context, tabLabel, iconResId); // 탭 초기화 메서드 호출
    }

    /**
     * init 메서드 : 탭의 UI 요소를 초기화하고 스타일을 설정
     * @param context Context 객체
     * @param tabLabel 탭에 표시할 텍스트
     * @param iconResId 탭에 사용할 아이콘 리소스 ID
     */
    private void init(Context context, String tabLabel, int iconResId) {
        // FrameLayout 기본 크기와 배경 설정
        this.setLayoutParams(new LayoutParams(
                dpToPx(context, 80), dpToPx(context, 80)));
        this.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button01));

        // ImageView 초기화 : 아이콘 설정 및 크기, 스타일 설정
        imageView = new ImageView(context);
        imageView.setLayoutParams(new LayoutParams(
                dpToPx(context, 40), dpToPx(context, 40)));
        imageView.setImageResource(iconResId);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // ImageView 레이아웃 설정 (정렬 위치와 마진)
        LayoutParams imageParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imageParams.gravity = CENTER;
        imageParams.bottomMargin = dpToPx(context, 12);
        imageView.setLayoutParams(imageParams);

        // TextView 초기화 : 탭 라벨 설정 및 텍스트 크기, 색상 설정
        textView = new TextView(context);
        textView.setText(tabLabel);
        textView.setTextColor(ContextCompat.getColor(context, R.color.basicColor03));
        textView.setTextSize(12);

        // TextView 레이아웃 설정 (정렬 위치와 마진)
        LayoutParams textParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.gravity = CENTER;
        textParams.topMargin = dpToPx(context, 22);
        textView.setLayoutParams(textParams);

        // FrameLayout에 ImageView와 TextView 추가
        this.addView(imageView);
        this.addView(textView);

        // 전체 FrameLayout의 마진 설정
        LayoutParams params = (LayoutParams) this.getLayoutParams();
        params.topMargin = dpToPx(context, 10);
        params.bottomMargin = dpToPx(context, 10);
        params.leftMargin = dpToPx(context, 5);
        params.rightMargin = dpToPx(context, 5);
        this.setLayoutParams(params);
    }

    /**
     * setSelectedStyle 메서드 : 탭이 선택되었을 때 스타일을 변경
     * @param context Context 객체
     */
    public void setSelectedStyle(Context context) {
        if (context == null) {
            return;
        }
        // 배경을 선택된 상태로 변경
        this.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button02));

        // 아이콘 색상 변경
        imageView.setColorFilter(ContextCompat.getColor(context, R.color.basicColor03));

        // 텍스트 색상과 굵기 변경
        textView.setTextColor(ContextCompat.getColor(context, R.color.basicColor01));
        textView.setTextSize(12); // 텍스트 크기 유지
        textView.setTypeface(null, android.graphics.Typeface.BOLD);  // 텍스트를 굵게 설정
    }

    /**
     * setBasicStyle 메서드 : 탭의 선택이 해제되었을 때 기본 스타일로 변경
     * @param context Context 객체
     */
    public void setBasicStyle(Context context) {
        if (context == null) {
            return;
        }
        // 기본 배경으로 설정
        this.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button01));

        // 아이콘 색상 초기화
        imageView.setColorFilter(null);

        // 텍스트 색상과 스타일 초기화
        textView.setTextColor(ContextCompat.getColor(context, R.color.basicColor03));
        textView.setTextSize(12); // 텍스트 크기 유지
        textView.setTypeface(null, android.graphics.Typeface.NORMAL);  // 텍스트 스타일을 기본으로
    }

    /**
     * dpToPx 메서드 : dp 단위를 px 단위로 변환
     * @param context Context 객체
     * @param dp 변환할 dp 값
     * @return 변환된 px 값
     */
    private int dpToPx(Context context, float dp) {
        if (context == null) {
            return 0; // Context가 없을 때는 안전하게 0 반환
        }
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
