package com.exp.narang.api.model.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://api.imgbb.com/";

    private static class Holder{
        private static final Retrofit instance = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private RetrofitClient(){}

    public static Retrofit getRetrofitInstance(){
        return Holder.instance;
    }
}
