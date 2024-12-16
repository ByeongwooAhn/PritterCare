package com.example.prittercare.model;

import android.util.Log;

import com.example.prittercare.controller.callback.CageDeleteCallBack;
import com.example.prittercare.controller.callback.CageListCallback;
import com.example.prittercare.controller.LogController;
import com.example.prittercare.controller.callback.CageRegisterCallback;
import com.example.prittercare.controller.callback.CageSingleUpdateCallBack;
import com.example.prittercare.controller.callback.CageUpdateCallback;
import com.example.prittercare.controller.callback.LoginCallback;
import com.example.prittercare.model.data.CageData;
import com.example.prittercare.model.request.LoadCageSettingsRequest;
import com.example.prittercare.model.request.LoginRequest;
import com.example.prittercare.model.request.UpdateCageNameRequest;
import com.example.prittercare.model.request.UpdateHumidityRequest;
import com.example.prittercare.model.request.UpdateLightingRequest;
import com.example.prittercare.model.request.UpdateTemperatureRequest;
import com.example.prittercare.model.request.UpdateWaterLevelRequest;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
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

    public void checkSerialNumber(String token, String serialNumber, CageRegisterCallback callback) {
        CageData request = new CageData();
        request.setCageSerialNumber(serialNumber);

        scalarApiService.serialCheck(token, request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        Log.e("DataRepository", "Failed to check serial number: " + response.code());
                        if (response.errorBody() != null) {
                            Log.e("DataRepository", "Error body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onFailure(new Exception("Serial number check failed"));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("DataRepository", "Network error during serial number check", t);
                callback.onFailure((Exception) t);
            }
        });
    }

    public void addCage(String token, CageData cageData, CageRegisterCallback callback) {
        scalarApiService.addCage(token, cageData).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    // 서버 응답이 2xx가 아니거나 body가 비어있는 경우
                    Log.e("DataRepository", "Failed to add cage: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("DataRepository", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onFailure(new Exception("Failed to add cage"));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // 네트워크 오류나 기타 통신 오류 발생
                Log.e("DataRepository", "Network error during adding cage", t);
                callback.onFailure(new Exception(t));
            }
        });
    }



    public void loadCageSettings(String token, String cageSerialNumber, CageSingleUpdateCallBack callback) {
        gsonApiService.getCageSettings(token, cageSerialNumber).enqueue(new Callback<List<CageData>>() {
            @Override
            public void onResponse(Call<List<CageData>> call, Response<List<CageData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CageData> cageList = response.body();
                    if (!cageList.isEmpty()) {
                        callback.onSuccess(cageList.get(0)); // 첫 번째 데이터를 전달
                        Log.d("DataRepository", "Loaded cage settings: " + cageList);
                    } else {
                        callback.onFailure(new Exception("No cage settings found for the given serial number."));
                    }
                } else {
                    Log.e("DataRepository", "Failed to load cage settings: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("DataRepository", "Error body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onFailure(new Exception("Failed to load cage settings."));
                }
            }

            @Override
            public void onFailure(Call<List<CageData>> call, Throwable t) {
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
        // 토큰에 Bearer 접두사 붙이기
        String bearerToken = token;

        // 수정된 ApiService 메서드 사용
        scalarApiService.deleteCage(bearerToken, cageSerialNumber).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // 성공적으로 DELETE 처리된 경우
                    String successMessage = response.body(); // 예: "Delete Cage Successful!"
                    callback.onSuccess(successMessage);
                } else {
                    // 실패한 경우
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
                // 네트워크 에러 등의 이유로 요청 자체가 실패한 경우
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
