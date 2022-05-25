package com.f0x1d.testmodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.module.AuthModule;
import com.f0x1d.dmsdk.module.CacheModule;
import com.f0x1d.dmsdk.module.DocumentsModule;
import com.f0x1d.dmsdk.module.FoldersModule;
import com.f0x1d.dmsdk.module.UIModule;
import com.f0x1d.testmodule.module.TestAuthModule;
import com.f0x1d.testmodule.module.TestCacheModule;
import com.f0x1d.testmodule.module.TestDocumentsModule;
import com.f0x1d.testmodule.module.TestFoldersModule;
import com.f0x1d.testmodule.module.TestUIModule;

public class TestBinService extends BinService {

    private final TestAuthModule testAuthModule = new TestAuthModule(this);
    private final TestDocumentsModule testDocumentsModule = new TestDocumentsModule(this);
    private final TestCacheModule testCacheModule = new TestCacheModule(this);
    private final TestFoldersModule testFoldersModule = new TestFoldersModule(this);
    private final TestUIModule testUIModule = new TestUIModule(this);

    @Override
    public String getServiceShortName() {
        return "ts";
    }

    @Override
    public String getDomain() {
        return "https://f0x1d.com/";
    }

    @Override
    public String getSlugFromLink(String link) {
        return "test";
    }

    @Override
    public AuthModule auth() {
        return testAuthModule;
    }

    @Override
    public DocumentsModule documents() {
        return testDocumentsModule;
    }

    @Override
    public CacheModule cache() {
        return testCacheModule;
    }

    @Override
    public FoldersModule folders() {
        return testFoldersModule;
    }

    @Override
    public UIModule ui() {
        return testUIModule;
    }
}
