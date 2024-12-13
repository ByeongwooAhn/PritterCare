package com.example.prittercare.model;

import com.example.prittercare.model.data.CageData;
import com.example.prittercare.model.request.DeleteCageRequest;
import com.example.prittercare.model.request.LoadCageSettingsRequest;
import com.example.prittercare.model.request.LoginRequest;
import com.example.prittercare.model.request.SignUpRequest;
import com.example.prittercare.model.request.UpdateCageNameRequest;
import com.example.prittercare.model.request.UpdateHumidityRequest;
import com.example.prittercare.model.request.UpdateLightingRequest;
import com.example.prittercare.model.request.UpdateTemperatureRequest;
import com.example.prittercare.model.request.UpdateWaterLevelRequest;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // 사용자 로그인
    @POST("/accounts/login")
    Call<String> logIn(@Body LoginRequest loginRequest);

    // 사용자 회원가입
    @POST("/accounts/signup")
    Call<Void> signUp(@Body SignUpRequest signUpRequest);

    @PATCH("/cages/serial")
    Call<String> serialCheck(@Header("Authorization") String token, @Body CageData request);

    @PATCH("cages/add")
    Call<String> addCage(
            @Header("Authorization") String token,
            @Body CageData request
    );

    // 사육장 리스트 가져오기
    @GET("/cages/list")
    Call<List<CageData>> getCageList(@Header("Authorization") String token);

    @GET("/cages/lastdata")
    Call<List<CageData>> getCageSettings(
            @Header("Authorization") String token,
            @Query("cage_serial_number") String cageSerialNumber
    );


    @DELETE("cages")
    Call<String> deleteCage(
            @Header("Authorization") String authorization,
            @Query("cage_serial_number") String cageSerialNumber
    );


    /**
     * 케이지 업데이트
     */
// 사육장 이름 수정
    @PATCH("/cages/set/name")
    Call<Void> updateCageName(@Header("Authorization") String token, @Body UpdateCageNameRequest request);

    @PATCH("/cages/set/temperature")
    Call<Void> updateTemperature(@Header("Authorization") String token, @Body UpdateTemperatureRequest request);

    @PATCH("/cages/set/humidity")
    Call<Void> updateHumidity(@Header("Authorization") String token, @Body UpdateHumidityRequest request);

    @PATCH("/cages/set/light")
    Call<Void> updateLighting(@Header("Authorization") String token, @Body UpdateLightingRequest request);

    @PATCH("/cages/set/water-level")
    Call<Void> updateWaterLevel(@Header("Authorization") String token, @Body UpdateWaterLevelRequest request);
}
