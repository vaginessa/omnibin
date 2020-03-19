package com.f0x1d.dogbin.network.retrofit;

import com.f0x1d.dogbin.network.model.DocumentLinkResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DogBinApiService {

    // Somehow vzlom login
    @POST("login")
    Call<String> login(@Body RequestBody body);

    @GET("me")
    Call<String> me();


    // Creating and reading docs
    @POST("documents")
    Call<DocumentLinkResponse> createDocument(@Body RequestBody body);

    @GET("raw/{slug}")
    Call<String> getDocumentText(@Path("slug") String slug);

    @GET("{slug}")
    Call<String> getDocumentTextHTML(@Path("slug") String slug);
}
