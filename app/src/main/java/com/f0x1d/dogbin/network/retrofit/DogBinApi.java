package com.f0x1d.dogbin.network.retrofit;

import android.content.SharedPreferences;

import com.badmanners.okhttp.ModifiablePersistentCookieJar;
import com.f0x1d.dogbin.App;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DogBinApi implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static DogBinApi sInstance;

    private DogBinApiService sService;
    private ModifiablePersistentCookieJar sCookieJar;

    private DogBinApi() {
        App.getPrefsUtil().getDefaultPreferences().registerOnSharedPreferenceChangeListener(this);
        App.getPrefsUtil().getAppPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    public static DogBinApi getInstance() {
        return sInstance == null ? sInstance = new DogBinApi() : sInstance;
    }

    public DogBinApiService getService() {
        if (sService == null)
            setupService();

        return sService;
    }

    private void setupService() {
        if (sCookieJar == null)
            sCookieJar = new ModifiablePersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.getInstance()));

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(sCookieJar);

        if (App.getPrefsUtil().isProxyEnabled()) {
            builder.proxy(new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(App.getPrefsUtil().getProxyHost(), App.getPrefsUtil().getProxyPort())));
            builder.authenticator((route, response) -> {
                String credential = Credentials.basic(App.getPrefsUtil().getProxyLogin(), App.getPrefsUtil().getProxyPassword());
                return response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build();
            });
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://del.dog/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(builder.build())
                .build();

        sService = retrofit.create(DogBinApiService.class);
    }

    public ModifiablePersistentCookieJar getCookieJar() {
        if (sCookieJar == null)
            setupService();

        return sCookieJar;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.contains("proxy")) {
            setupService();
        }
    }
}
