package com.f0x1d.dmsdk;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Keep;

import com.f0x1d.dmsdk.module.AuthModule;
import com.f0x1d.dmsdk.module.CacheModule;
import com.f0x1d.dmsdk.module.DocumentsModule;
import com.f0x1d.dmsdk.module.FoldersModule;

@Keep
public interface BinService {

    /**
     * Called when module is loaded
     *
     * @param applicationContext
     * @param dogbinMobileContext
     * @param modulePreferences
     */
    void init(Context applicationContext, Context dogbinMobileContext, SharedPreferences modulePreferences);

    /**
     * @return service domain
     */
    String getDomain();

    /**
     * Parses link in order to get slug
     *
     * @param link
     * @return slug
     */
    String getSlugFromLink(String link);

    /**
     * @return your implementation of auth
     */
    AuthModule auth();

    /**
     * @return your implementation of working with documents
     */
    DocumentsModule documents();

    /**
     * @return your implementation of cache
     */
    CacheModule cache();

    /**
     * @return your implementation of folders
     */
    FoldersModule folders();

    default int getSDKVersion() {
        return Constants.LATEST_VERSION;
    }
}
