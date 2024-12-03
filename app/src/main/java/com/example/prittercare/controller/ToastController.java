package com.example.prittercare.controller;

import android.content.Context;
import android.widget.Toast;

public class ToastController {
    private final Context context;

    public ToastController(Context context) {
        this.context = context;
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
