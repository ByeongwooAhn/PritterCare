package com.example.prittercare.model;

import com.example.prittercare.model.data.CageData;
import com.example.prittercare.model.request.LoginRequest;
import com.example.prittercare.model.request.SignUpRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    // 사용자 로그인
    @POST("/accounts/login")
    Call<String> logIn(@Body LoginRequest loginRequest);

    // 사용자 회원가입
    @POST("/accounts/signup")
    Call<Void> signUp(@Body SignUpRequest signUpRequest);

    // 사육장 리스트 가져오기
    @GET("/cages/list")
    Call<List<CageData>> getCageList(@Header("Authorization") String token);

}
