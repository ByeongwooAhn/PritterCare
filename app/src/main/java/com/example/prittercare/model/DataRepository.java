package com.example.prittercare.model;

import android.util.Log;

import com.example.prittercare.controller.CageListCallback;
import com.example.prittercare.controller.LogController;
import com.example.prittercare.controller.LoginCallback;
import com.example.prittercare.model.data.CageData;
import com.example.prittercare.model.request.LoginRequest;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataRepository {
    private final ApiService scalarApiService;
    private final ApiService gsonApiService;
    LogController logController;

    public DataRepository() {
        this.scalarApiService = ApiClient.getScalarRetrofit().create(ApiService.class);
        this.gsonApiService = ApiClient.getGsonClient().create(ApiService.class);

    }

    public void login(String username, String password, LoginCallback callback) {
        logController = new LogController("LOGIN");
        scalarApiService.logIn(new LoginRequest(username, password)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body();
                    logController.logInfo("Token: " + token);
                    DataManager.getInstance().setToken(token);
                    callback.onSuccess(token);
                } else {
                    // 실패 콜백 호출
                    callback.onFailure(new Exception("로그인 실패"));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // 네트워크 오류 콜백 호출
                callback.onFailure(t);
            }
        });

    }

    public void fetchCageList(String token, CageListCallback<CageData> callback) {
        gsonApiService.getCageList(token).enqueue(new Callback<List<CageData>>() {
            @Override
            public void onResponse(Call<List<CageData>> call, Response<List<CageData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    Log.d("DataRepository", "Fetched cages: " + response.body());
                } else {
                    Log.e("DataRepository", "API 응답 실패: " + response.code());
                    try {
                        Log.e("DataRepository", "에러 메시지: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onFailure(new Exception("Cage list 가져오기 실패"));
                }
            }

            @Override
            public void onFailure(Call<List<CageData>> call, Throwable t) {
                Log.e("DataRepository", "네트워크 요청 실패", t);
                callback.onFailure(t);
            }
        });
    }
}
