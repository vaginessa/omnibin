package com.f0x1d.testservice.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PasteBinApiService {

    @POST("api_login.php")
    Call<String> login(@Body RequestBody requestBody);

    @POST("api_post.php")
    Call<String> getPastes(@Body RequestBody requestBody);

    @POST("api_post.php")
    Call<String> paste(@Body RequestBody requestBody);

    @POST("api_raw.php")
    Call<String> getText(@Body RequestBody requestBody);
}
