package com.example.prittercare.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("signup")
    Call<Void> signUp(@Body SignUpRequest signUpRequest);

    @POST("login")
    Call<Boolean> logIn(@Body LoginRequest loginRequest);
}