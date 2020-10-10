package com.f0x1d.dogbin.network.retrofit;

import android.content.SharedPreferences;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.utils.PreferencesUtils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DogBinApi implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static DogBinApi sInstance;
    private DogBinApiService mService;

    private DogBinApi() {
        App.getPreferencesUtil().getDefaultPreferences().registerOnSharedPreferenceChangeListener(this);
        App.getPreferencesUtil().getAppPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    public static DogBinApi getInstance() {
        synchronized (DogBinApi.class) {
            return sInstance == null ? sInstance = new DogBinApi() : sInstance;
        }
    }

    public DogBinApiService getService() {
        if (mService == null)
            setupService();

        return mService;
    }

    private void setupService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.getPreferencesUtil().getDogbinDomain())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();

        mService = retrofit.create(DogBinApiService.class);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesUtils.DOGBIN_DOMAIN_NAME)) {
            setupService();
        }
    }
}
