package com.f0x1d.testservice.network.retrofit;

import com.f0x1d.testservice.network.retrofit.service.PasteBinApiService;
import com.f0x1d.testservice.network.retrofit.service.PasteBinRawService;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class PasteBinApi {

    private static PasteBinApi sInstance;

    private PasteBinApiService mService;
    private PasteBinRawService mRawService;

    public static PasteBinApi getInstance() {
        return sInstance == null ? sInstance = new PasteBinApi() : sInstance;
    }

    public PasteBinApiService getService() {
        if (mService == null) {
            mService = new Retrofit.Builder()
                    .baseUrl("https://pastebin.com/api/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(PasteBinApiService.class);
        }
        return mService;
    }

    public PasteBinRawService getRawService() {
        if (mRawService == null) {
            mRawService = new Retrofit.Builder()
                    .baseUrl("https://pastebin.com/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
                    .create(PasteBinRawService.class);
        }
        return mRawService;
    }
}
