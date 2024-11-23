package com.example.prittercare.view;

import com.example.prittercare.view.main.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("accounts/signup")
    Call<Void> signUp(@Body SignUpRequest signUpRequest);

    @POST("accounts/login")
    Call<Boolean> logIn(@Body LoginRequest loginRequest);
}