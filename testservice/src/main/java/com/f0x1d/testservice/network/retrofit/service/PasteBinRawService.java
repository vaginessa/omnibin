package com.f0x1d.testservice.network.retrofit.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PasteBinRawService {

    @GET("raw/{slug}")
    Call<String> getContent(@Path("slug") String slug);

    @GET("archive")
    Call<String> getArchive();
}
