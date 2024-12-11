package com.example.prittercare.model;

import android.provider.ContactsContract;
import android.util.Log;

import com.example.prittercare.controller.callback.CageDeleteCallBack;
import com.example.prittercare.controller.callback.CageListCallback;
import com.example.prittercare.controller.LogController;
import com.example.prittercare.controller.callback.CageSingleUpdateCallBack;
import com.example.prittercare.controller.callback.CageUpdateCallback;
import com.example.prittercare.controller.callback.LoginCallback;
import com.example.prittercare.model.data.CageData;
import com.example.prittercare.model.request.DeleteCageRequest;
import com.example.prittercare.model.request.LoadCageSettingsRequest;
import com.example.prittercare.model.request.LoginRequest;
import com.example.prittercare.model.request.UpdateCageNameRequest;
import com.example.prittercare.model.request.UpdateHumidityRequest;
import com.example.prittercare.model.request.UpdateLightingRequest;
import com.example.prittercare.model.request.UpdateTemperatureRequest;
import com.example.prittercare.model.request.UpdateWaterLevelRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void loadCageList(String token, CageListCallback<CageData> callback) {
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

    public void loadCageSettings(String token, String cageSerialNumber, CageSingleUpdateCallBack callback) {
        LoadCageSettingsRequest request = new LoadCageSettingsRequest(cageSerialNumber);

        gsonApiService.getCageSettings(token, request).enqueue(new Callback<CageData>() {
            @Override
            public void onResponse(Call<CageData> call, Response<CageData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    Log.d("DataRepository", "Last cage settings loaded successfully: " + response.body());
                } else {
                    Log.e("DataRepository", "Failed to load cage settings: " + response.code());
                    try {
                        Log.e("DataRepository", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onFailure(new Exception("Failed to load cage settings"));
                }
            }

            @Override
            public void onFailure(Call<CageData> call, Throwable t) {
                Log.e("DataRepository", "Network error while loading cage settings", t);
                callback.onFailure(t);
            }
        });
    }

    public void updateCageName(String token, String cageSerialNumber, String cageName, CageUpdateCallback callback) {
        UpdateCageNameRequest request = new UpdateCageNameRequest(cageSerialNumber, cageName);
        gsonApiService.updateCageName(token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Cage name updated successfully");
                } else {
                    callback.onFailure(new Exception("Failed to update cage name: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void deleteCage(String token, String cageSerialNumber, CageDeleteCallBack callback) {
        gsonApiService.deleteCage(token, cageSerialNumber).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        Log.e("DataRepository", "Failed to delete cage: " + response.code());
                        if (response.errorBody() != null) {
                            Log.e("DataRepository", "Error body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onFailure(new Exception("Failed to delete cage"));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("DataRepository", "Error deleting cage", t);
                callback.onFailure(t);
            }
        });
    }


    public void updateTemperature(String token, String cageSerialNumber, String envTemperature, CageUpdateCallback callback) {
        UpdateTemperatureRequest request = new UpdateTemperatureRequest(cageSerialNumber, envTemperature);
        gsonApiService.updateTemperature(token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Temperature updated successfully.");
                } else {
                    callback.onFailure(new Exception("Failed to update temperature: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void updateHumidity(String token, String cageSerialNumber, String envHumidity, CageUpdateCallback callback) {
        UpdateHumidityRequest request = new UpdateHumidityRequest(cageSerialNumber, envHumidity);
        gsonApiService.updateHumidity(token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Humidity updated successfully");
                } else {
                    callback.onFailure(new Exception("Failed to update humidity: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void updateLighting(String token, String cageSerialNumber, String envLighting, CageUpdateCallback callback) {
        UpdateLightingRequest request = new UpdateLightingRequest(cageSerialNumber, envLighting);
        gsonApiService.updateLighting(token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Lighting updated successfully");
                } else {
                    callback.onFailure(new Exception("Failed to update lighting: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void updateWaterLevel(String token, String cageSerialNumber, String envWaterLevel, CageUpdateCallback callback) {
        UpdateWaterLevelRequest request = new UpdateWaterLevelRequest(cageSerialNumber, envWaterLevel);
        gsonApiService.updateWaterLevel(token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("WaterLevel updated successfully");
                } else {
                    callback.onFailure(new Exception("Failed to update waterLevel: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


}
