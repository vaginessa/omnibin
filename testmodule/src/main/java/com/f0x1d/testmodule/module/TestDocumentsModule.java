package com.f0x1d.testmodule.module;

import android.os.Bundle;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.DocumentsModule;

public class TestDocumentsModule extends DocumentsModule {

    public TestDocumentsModule(BinService binService) {
        super(binService);
    }

    @Override
    public DocumentContent getDocumentContent(String slug) throws Exception {
        return DocumentContent.create("testing", slug, false, false);
    }

    @Override
    public String createDocument(String slug, String content, Bundle settings) throws Exception {
        return null;
    }

    @Override
    public String editDocument(String slug, String content, Bundle settings) throws Exception {
        return null;
    }

    @Override
    public boolean deleteDocument(String slug) throws Exception {
        return false;
    }

    @Override
    public Boolean isEditableDocument(String slug) throws Exception {
        return null;
    }

    @Override
    public boolean canDelete(UserDocument userDocument) {
        return false;
    }
}
