package com.f0x1d.dogbin.network.service.foxbin;

import android.content.Context;
import android.content.SharedPreferences;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.module.AuthModule;
import com.f0x1d.dmsdk.module.CacheModule;
import com.f0x1d.dmsdk.module.DocumentsModule;
import com.f0x1d.dmsdk.module.FoldersModule;

public class FoxBinService implements BinService {

    private static FoxBinService sInstance;

    private final FoxBinAuth mFoxBinAuth = new FoxBinAuth(this);
    private final FoxBinDocuments mFoxBinDocuments = new FoxBinDocuments(this);
    private final FoxBinCache mFoxBinCache = new FoxBinCache(this);
    private final FoxBinFolders mFoxBinFolders = new FoxBinFolders(this);

    public static FoxBinService getInstance() {
        synchronized (FoxBinService.class) {
            return sInstance == null ? sInstance = new FoxBinService() : sInstance;
        }
    }

    @Override
    public void init(Context applicationContext, Context dogbinMobileContext, SharedPreferences modulePreferences) {

    }

    @Override
    public String getDomain() {
        return "https://f0x1d.com/foxbin/";
    }

    @Override
    public String getSlugFromLink(String link) {
        return link.split("/")[4];
    }

    @Override
    public AuthModule auth() {
        return mFoxBinAuth;
    }

    @Override
    public DocumentsModule documents() {
        return mFoxBinDocuments;
    }

    @Override
    public CacheModule cache() {
        return mFoxBinCache;
    }

    @Override
    public FoldersModule folders() {
        return mFoxBinFolders;
    }
}
