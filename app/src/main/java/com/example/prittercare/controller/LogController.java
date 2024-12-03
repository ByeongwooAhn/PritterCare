package com.example.prittercare.controller;

import android.util.Log;

public class LogController {
    private final String tag;

    public LogController(String tag) {
        this.tag = tag;
    }

    public void logInfo(String message) {
        Log.i(tag, message);
    }

    public void logError(String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }
}
