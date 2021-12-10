package com.f0x1d.dogbin.network.service.foxbin;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.module.AuthModule;
import com.f0x1d.dmsdk.module.CacheModule;
import com.f0x1d.dmsdk.module.DocumentsModule;
import com.f0x1d.dmsdk.module.FoldersModule;
import com.f0x1d.dmsdk.module.UIModule;

public class FoxBinService extends BinService {

    private static FoxBinService sInstance;

    private final FoxBinAuth mFoxBinAuth = new FoxBinAuth(this);
    private final FoxBinDocuments mFoxBinDocuments = new FoxBinDocuments(this);
    private final FoxBinCache mFoxBinCache = new FoxBinCache(this);
    private final FoxBinFolders mFoxBinFolders = new FoxBinFolders(this);
    private final FoxBinUI mFoxBinUI = new FoxBinUI(this);

    public static FoxBinService getInstance() {
        synchronized (FoxBinService.class) {
            return sInstance == null ? sInstance = new FoxBinService() : sInstance;
        }
    }

    @Override
    public String getServiceShortName() {
        return "fb";
    }

    @Override
    public String getDomain() {
        return "https://foxbin.f0x1d.com/";
    }

    @Override
    public String getSlugFromLink(String link) {
        return link.contains("foxbin.f0x1d.com") ? link.split("/")[3] : link.split("/")[4];
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

    @Override
    public UIModule ui() {
        return mFoxBinUI;
    }
}
