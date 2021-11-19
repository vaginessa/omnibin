package com.f0x1d.dmsdk.module;

import androidx.annotation.Keep;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.base.BaseModule;

@Keep
public abstract class DocumentsModule extends BaseModule {

    public DocumentsModule(BinService binService) {
        super(binService);
    }

    /**
     * Get document content on slug
     * @param slug
     * @return document's content
     * @throws Exception
     */
    public abstract DocumentContent getDocumentContent(String slug) throws Exception;

    /**
     * Create document on slug (can be empty)
     * @param slug
     * @param content
     * @return created document's slug
     * @throws Exception
     */
    public abstract String createDocument(String slug, String content) throws Exception;

    /**
     * Edit document on slug
     * @param slug
     * @param content
     * @return edited document's slug
     * @throws Exception
     */
    public abstract String editDocument(String slug, String content) throws Exception;

    /**
     * Delete document on slug, if can be (based on canDelete(UserDocument))
     * @param slug
     * @return true if successfully deleted, false if not
     * @throws Exception
     */
    public abstract boolean deleteDocument(String slug) throws Exception;

    /**
     * Used to determine if this document can be deleted by user
     * @param userDocument
     * @return true if document can be deleted by user, false if not
     */
    public boolean canDelete(UserDocument userDocument) {
        return userDocument.myNote();
    }

    /**
     * @param slug
     * @return true if document can be edited, false if not, null if you set it in DocumentContent
     * @throws Exception
     */
    public abstract Boolean isEditableDocument(String slug) throws Exception;
}
