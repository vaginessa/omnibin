package com.f0x1d.dogbin.network.service.pastebin;

import android.content.Context;
import android.content.SharedPreferences;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.module.AuthModule;
import com.f0x1d.dmsdk.module.CacheModule;
import com.f0x1d.dmsdk.module.DocumentsModule;
import com.f0x1d.dmsdk.module.FoldersModule;

/* Please don't read this code, i don't know under what i've written it */
public class PasteBinService implements BinService {

    private static PasteBinService sInstance;

    private final PasteBinAuth mPasteBinAuth = new PasteBinAuth(this);
    private final PasteBinDocuments mPasteBinDocuments = new PasteBinDocuments(this);
    private final PasteBinCache mPasteBinCache = new PasteBinCache(this);
    private final PasteBinFolders mPasteBinFolders = new PasteBinFolders(this);

    public static PasteBinService getInstance() {
        synchronized (PasteBinService.class) {
            return sInstance == null ? sInstance = new PasteBinService() : sInstance;
        }
    }

    @Override
    public void init(Context applicationContext, Context dogbinMobileContext, SharedPreferences modulePreferences) {

    }

    @Override
    public String getDomain() {
        return "https://pastebin.com/";
    }

    @Override
    public String getSlugFromLink(String link) {
        return link.split("/")[3];
    }

    @Override
    public AuthModule auth() {
        return mPasteBinAuth;
    }

    @Override
    public DocumentsModule documents() {
        return mPasteBinDocuments;
    }

    @Override
    public CacheModule cache() {
        return mPasteBinCache;
    }

    @Override
    public FoldersModule folders() {
        return mPasteBinFolders;
    }

    public static boolean isResponseOk(String text) {
        return text != null && !text.contains("Bad API request");
    }
}
