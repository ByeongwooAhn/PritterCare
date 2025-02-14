package com.example.prittercare.view;

import static android.view.Gravity.CENTER;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.prittercare.R;
import com.example.prittercare.controller.StyleManager;
import com.example.prittercare.model.DataManager;

public class MainTabView extends FrameLayout {

    private StyleManager styleManager;
    private String animalType;

    private ImageView imageView;
    private TextView textView;

    public MainTabView(Context context, String tabLabel, int iconResId) {
        super(context);
        applyAnimalStyle(context);
        initializeLayout(context, tabLabel, iconResId);
    }

    private void applyAnimalStyle(Context context) {
        animalType = DataManager.getInstance().getCurrentAnimalType();
        styleManager = new StyleManager(context, DataManager.getInstance().getCurrentAnimalType());
    }

    private void initializeLayout(Context context, String tabLabel, int iconResId) {
        // 기본 레이아웃 크기와 배경 설정
        this.setLayoutParams(new LayoutParams(convertDpToPx(context, 80), convertDpToPx(context, 80)));
        this.setBackground(ContextCompat.getDrawable(context, styleManager.getButton01ShapeId()));

        // 아이콘 설정
        imageView = new ImageView(context);
        imageView.setLayoutParams(new LayoutParams(convertDpToPx(context, 40), convertDpToPx(context, 40)));
        imageView.setImageResource(iconResId);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LayoutParams imageParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imageParams.gravity = CENTER;
        imageParams.bottomMargin = convertDpToPx(context, 12);
        imageView.setLayoutParams(imageParams);

        // 텍스트 설정
        textView = new TextView(context);
        textView.setText(tabLabel);
        textView.setTextColor(ContextCompat.getColor(context, styleManager.getBasicColor03Id()));
        textView.setTextSize(12);

        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.gravity = CENTER;
        textParams.topMargin = convertDpToPx(context, 22);
        textView.setLayoutParams(textParams);

        // 레이아웃에 아이콘과 텍스트 추가
        this.addView(imageView);
        this.addView(textView);

        // 전체 마진 설정
        LayoutParams params = (LayoutParams) this.getLayoutParams();
        params.topMargin = convertDpToPx(context, 10);
        params.bottomMargin = convertDpToPx(context, 10);
        params.leftMargin = convertDpToPx(context, 5);
        params.rightMargin = convertDpToPx(context, 5);
        this.setLayoutParams(params);
    }

    public void selectTab(Context context) {
        this.setBackground(ContextCompat.getDrawable(context, styleManager.getSelectedTabId()));
        imageView.setColorFilter(ContextCompat.getColor(context, styleManager.getBasicColor03Id()));
        textView.setTextColor(ContextCompat.getColor(context, styleManager.getBasicColor01Id()));
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    public void unSelectTab(Context context) {
        this.setBackground(ContextCompat.getDrawable(context, styleManager.getUnselectedTabId()));
        imageView.setColorFilter(ContextCompat.getColor(context, styleManager.getBasicColor02Id()));
        textView.setTextColor(ContextCompat.getColor(context, styleManager.getBasicColor03Id()));
        textView.setTypeface(null, android.graphics.Typeface.NORMAL);

        Log.d("MainTabView", "UnselectedTabId: " + styleManager.getResourceName(styleManager.getUnselectedTabId()));
    }

    private int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
