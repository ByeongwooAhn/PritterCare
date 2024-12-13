package com.example.prittercare.controller.callback;

public interface CageRegisterCallback {
    void onSuccess(String response);
    void onFailure(Exception e);
}
