package com.example.prittercare.view;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("signup")
    Call<Void> signUp(@Body SignUpRequest signUpRequest);
}