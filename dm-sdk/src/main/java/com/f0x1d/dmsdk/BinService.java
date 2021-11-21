package com.f0x1d.dmsdk;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Keep;

import com.f0x1d.dmsdk.module.AuthModule;
import com.f0x1d.dmsdk.module.CacheModule;
import com.f0x1d.dmsdk.module.DocumentsModule;
import com.f0x1d.dmsdk.module.FoldersModule;
import com.f0x1d.dmsdk.module.UIModule;

@Keep
public abstract class BinService {

    protected Context mApplicationContext;
    protected Context mOmnibinContext;

    /**
     * Called when module is loaded
     * @param applicationContext
     * @param omnibinContext
     * @param modulePreferences
     */
    public void init(Context applicationContext, Context omnibinContext, SharedPreferences modulePreferences) {
        this.mApplicationContext = applicationContext;
        this.mOmnibinContext = omnibinContext;
    }

    /**
     * @return service domain
     */
    public abstract String getDomain();

    /**
     * Parses link in order to get slug
     * @param link
     * @return slug
     */
    public abstract String getSlugFromLink(String link);

    /**
     * @return your implementation of auth
     */
    public abstract AuthModule auth();

    /**
     * @return your implementation of working with documents
     */
    public abstract DocumentsModule documents();

    /**
     * @return your implementation of cache
     */
    public abstract CacheModule cache();

    /**
     * @return your implementation of folders
     */
    public abstract FoldersModule folders();

    /**
     * @return your implementation of ui
     */
    public abstract UIModule ui();

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public Context getOmnibinContext() {
        return mOmnibinContext;
    }

    public int getSDKVersion() {
        return Constants.LATEST_VERSION;
    }
}
