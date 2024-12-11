package com.example.prittercare.controller.callback;

public interface CageRegisterCallback {
    void onSuccess(String message);
    void onFailure(Exception e);
}
