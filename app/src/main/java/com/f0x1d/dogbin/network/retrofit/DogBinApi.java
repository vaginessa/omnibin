package com.f0x1d.dogbin.network.retrofit;

import android.content.SharedPreferences;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.network.okhttp.badmanners.ModifiablePersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DogBinApi implements SharedPreferences.OnSharedPreferenceChangeListener, Interceptor {

    public static final String LOCATION_HEADER_NAME = "location";

    private static DogBinApi sInstance;

    private DogBinApiService mService;
    private ModifiablePersistentCookieJar mCookieJar;

    private List<NetworkEventsListener> mNetworkEventsListenersList = new ArrayList<>();

    private DogBinApi() {
        App.getPrefsUtil().getDefaultPreferences().registerOnSharedPreferenceChangeListener(this);
        App.getPrefsUtil().getAppPreferences().registerOnSharedPreferenceChangeListener(this);
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
        if (mCookieJar == null)
            mCookieJar = new ModifiablePersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(App.getInstance()));

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(this)
                .cookieJar(mCookieJar);

        if (App.getPrefsUtil().isProxyEnabled()) {
            builder.proxy(new Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(App.getPrefsUtil().getProxyHost(), App.getPrefsUtil().getProxyPort())));

            if (App.getPrefsUtil().isAuthForProxyRequired()) {
                builder.authenticator((route, response) -> {
                    String credential = Credentials.basic(App.getPrefsUtil().getProxyLogin(), App.getPrefsUtil().getProxyPassword());
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                });
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://del.dog/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(builder.build())
                .build();

        mService = retrofit.create(DogBinApiService.class);
    }

    public ModifiablePersistentCookieJar getCookieJar() {
        if (mCookieJar == null)
            setupService();

        return mCookieJar;
    }

    public void registerListener(NetworkEventsListener networkEventsListener) {
        mNetworkEventsListenersList.add(networkEventsListener);
    }

    public void unregisterListener(NetworkEventsListener networkEventsListener) {
        mNetworkEventsListenersList.remove(networkEventsListener);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 301) {
            for (NetworkEventsListener networkEventsListener : mNetworkEventsListenersList) {
                networkEventsListener.onRedirect(response.header(LOCATION_HEADER_NAME));
            }
        }

        return response;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.contains("proxy")) {
            setupService();
        }
    }

    public interface NetworkEventsListener {
        void onRedirect(String url);
    }
}
