package com.example.prittercare.controller.callback;

public interface CageUpdateCallback {
    void onSuccess(String message);
    void onFailure(Throwable error);
}
