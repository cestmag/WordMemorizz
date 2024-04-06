package com.websarva.wings.android.wordmemorizz;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/login") // Replace with your server's login endpoint
    Call<ApiResponse> login(@Body User user);

    @POST("/register") // Replace with your server's registration endpoint
    Call<ApiResponse> register(@Body User user);

    @POST("/add")
    Call<ApiResponse> addwords(@Body List<DatabaseHelper.WordData> wordData);

    @GET("/")
    Call<ApiResponse> getProtectedData();

    @POST("/sync-local-changes")
    Call<ApiResponse> letsSync(@Body User user);//user_id and last stimestamp

    @POST("/sync-global-changes-ok")
    Call<ApiResponse> syncOK(@Body User user);



}

