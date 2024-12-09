package com.example.prittercare.controller;

import android.content.Context;

public class StyleManager {
    Context context;
    String animalType;

    public StyleManager(Context context, String animalType) {
        this.context = context;
        this.animalType = animalType;
    }


    /**
     * 01. color resources
     */
    public int getBasicColor01Id() {
        String resourceName = "basicColor01_" + animalType; // 리소스 이름 생성
        return context.getResources().getIdentifier(resourceName, "color", context.getPackageName());
    }

    public int getBasicColor02Id() {
        String resourceName = "basicColor02_" + animalType; // 리소스 이름 생성
        return context.getResources().getIdentifier(resourceName, "color", context.getPackageName());
    }

    public int getBasicColor03Id() {
        String resourceName = "basicColor03_" + animalType; // 리소스 이름 생성
        return context.getResources().getIdentifier(resourceName, "color", context.getPackageName());
    }


    /**
     * 02. drawable resources
     */

    // Button
    public int getButton01ShapeId() {
        String resourceName = "shape_button01_" + animalType;
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    public int getButton02ShapeId() {
        String resourceName = "shape_button02_" + animalType;
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    // Card Shape
    public int getCardShapeId() {
        String resourceName = "shape_card_" + animalType;
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    // Tab
    public int getSelectedTabId() {
        String resourceName = "shape_tab_selected_" + animalType;
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    public int getUnselectedTabId() {
        String resourceName = "shape_tab_unselected_" + animalType;
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    // BackGround
    public int getBackgroundMainId() {
        String resourceName = "background_main_" + animalType;
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }

    // Icon
    public int getSeekBarThumbIconId() {
        String resourceName = "ic_thumb_" + animalType;
        return context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
    }


    /**
     * 03. drawable resources
     */
    public int getButtonStyle01() {
        String resourceName = "ButtonStyle01_" + animalType;
        return context.getResources().getIdentifier(resourceName, "style", context.getPackageName());
    }

    public int getButtonStyle02() {
        String resourceName = "ButtonStyle02_" + animalType;
        return context.getResources().getIdentifier(resourceName, "style", context.getPackageName());
    }

    public String getResourceName(int resourceId) {
        return context.getResources().getResourceEntryName(resourceId); // 리소스 이름 반환
    }
}