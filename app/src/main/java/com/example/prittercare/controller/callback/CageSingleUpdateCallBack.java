package com.example.prittercare.controller.callback;

import com.example.prittercare.model.data.CageData;

public interface CageSingleUpdateCallBack {
    void onSuccess(CageData data);

    void onFailure(Throwable t);
}
