package com.example.prittercare.model;

import com.example.prittercare.model.request.LoginRequest;
import com.example.prittercare.model.request.SignUpRequest;
import com.example.prittercare.model.response.LoginResponse;
import com.example.prittercare.model.CageData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // 사용자 로그인
    @POST("/accounts/login")
    Call<Boolean> logIn(
            @Body LoginRequest loginRequest
    );

    // 사용자 회원가입
    @POST("/accounts/signup")
    Call<Void> signUp(
            @Body SignUpRequest signUpRequest
    );

    // 사용자별 케이지 데이터 가져오기
    @POST("/accounts/cages")
    Call<List<CageData>> getCageData(
            @Body String username // 요청 본문으로 사용자 이름 전달
    );
}
