package com.example.prittercare.controller.callback;

public interface LoginCallback {
    void onSuccess(String token);
    void onFailure(Throwable error);
}
