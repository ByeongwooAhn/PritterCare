package com.example.prittercare.controller.callback;

import java.util.List;

public interface CageListCallback<T> {
    void onSuccess(List<T> cageList);
    void onFailure(Throwable error);
}
