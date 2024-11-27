package com.example.prittercare.model;

import com.example.prittercare.model.request.LoginRequest;
import com.example.prittercare.model.request.SignUpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // 사용자 로그인
    @POST("/accounts/login")
    Call<ApiResponse> logIn(@Body LoginRequest loginRequest);

    // 사용자 회원가입
    @POST("/accounts/signup")
    Call<Void> signUp(@Body SignUpRequest signUpRequest);
}
