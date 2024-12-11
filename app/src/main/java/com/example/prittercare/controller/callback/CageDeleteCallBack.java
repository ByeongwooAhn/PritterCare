package com.example.prittercare.controller.callback;

public interface CageDeleteCallBack {
    void onSuccess(String response);
    void onFailure(Throwable t);
}
