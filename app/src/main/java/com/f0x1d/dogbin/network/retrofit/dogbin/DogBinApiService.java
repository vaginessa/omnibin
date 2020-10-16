package com.f0x1d.dogbin.network.retrofit.dogbin;

import com.f0x1d.dogbin.network.model.ApiKeyResponse;
import com.f0x1d.dogbin.network.model.DocumentInfoResponse;
import com.f0x1d.dogbin.network.model.DocumentLinkResponse;
import com.f0x1d.dogbin.network.model.DocumentResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DogBinApiService {

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("api/v1/auth/login")
    Call<ApiKeyResponse> login(@Body RequestBody body);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @POST("api/v1/auth/register")
    Call<ApiKeyResponse> register(@Body RequestBody body);

    @GET("api/v1/docs")
    Call<List<DocumentResponse>> getMyNotes(@Header("X-Api-Key") String apiKey);

    @POST("documents")
    Call<DocumentLinkResponse> createDocument(@Header("X-Api-Key") String apiKey, @Body RequestBody body);

    // TODO: wait for fix
    @GET("documents/{slug}")
    Call<DocumentInfoResponse> getDocument(@Header("XX-Api-Key") String apiKey, @Path("slug") String slug);
}
