package com.example.prittercare.controller;

public interface LoginCallback {
    void onSuccess(String token);
    void onFailure(Throwable error);
}
