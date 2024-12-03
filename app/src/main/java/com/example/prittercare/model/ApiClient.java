package com.example.prittercare.model;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://medicine.p-e.kr";
    private static Retrofit gsonRetrofit;
    private static Retrofit scalarRetrofit;

    public static Retrofit getGsonClient() {
        if(gsonRetrofit == null) {
            gsonRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return gsonRetrofit;
    }

    public static Retrofit getScalarRetrofit() {
        if(scalarRetrofit == null) {
            scalarRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  scalarRetrofit;
    }
}
