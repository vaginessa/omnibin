package com.f0x1d.dmsdk.module;

import androidx.annotation.Keep;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.base.BaseModule;

import java.util.Collections;
import java.util.List;

@Keep
public abstract class CacheModule extends BaseModule {

    public CacheModule(BinService binService) {
        super(binService);
    }

    /**
     * @return list of cached documents, empty list if there are no such
     */
    public List<UserDocument> getDocumentListFromCache() {
        return Collections.emptyList();
    }

    /**
     * @param slug
     * @return document's content from cache, null if there is no such
     */
    public DocumentContent getContentFromCache(String slug) {
        return null;
    }

    /**
     * Caches a document
     * @param slug
     * @param content
     * @param myDocument
     */
    public void cacheDocument(String slug, String content, boolean myDocument) {}
}
