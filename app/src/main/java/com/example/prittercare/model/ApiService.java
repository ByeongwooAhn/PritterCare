package com.example.prittercare.model;

import com.example.prittercare.model.request.LoginRequest;
import com.example.prittercare.model.request.SignUpRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // 사용자 로그인
    @POST("/accounts/login")
    Call<List<CageData>> logIn(
            @Body LoginRequest loginRequest
    );

    // 사용자 회원가입
    @POST("/accounts/signup")
    Call<Void> signUp(
            @Body SignUpRequest signUpRequest
    );
}
