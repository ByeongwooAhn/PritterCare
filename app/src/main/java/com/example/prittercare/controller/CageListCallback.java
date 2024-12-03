package com.example.prittercare.controller;

import com.example.prittercare.model.data.CageData;

import java.util.List;

public interface CageListCallback<T> {
    void onSuccess(List<T> cageList);
    void onFailure(Throwable error);
}
