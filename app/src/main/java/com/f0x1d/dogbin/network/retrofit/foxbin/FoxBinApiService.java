package com.f0x1d.dogbin.network.retrofit.foxbin;

import com.f0x1d.dogbin.network.model.foxbin.FoxBinCreatedDocumentResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinGetNoteResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinGetNotesResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinLoginRegisterResponse;
import com.f0x1d.dogbin.network.model.foxbin.base.BaseFoxBinResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FoxBinApiService {

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("users/login")
    Call<FoxBinLoginRegisterResponse> login(@Body RequestBody body);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("users/register")
    Call<FoxBinLoginRegisterResponse> register(@Body RequestBody body);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("create")
    Call<FoxBinCreatedDocumentResponse> createDocument(@Body RequestBody body);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("edit")
    Call<FoxBinCreatedDocumentResponse> editDocument(@Body RequestBody body);

    @GET("get/{slug}")
    Call<FoxBinGetNoteResponse> getNote(@Path("slug") String slug, @Query("accessToken") String token);

    @GET("delete/{slug}")
    Call<BaseFoxBinResponse> deleteNote(@Path("slug") String slug, @Query("accessToken") String token);

    @GET("getAll")
    Call<FoxBinGetNotesResponse> getAllNotes(@Query("accessToken") String token);
}
