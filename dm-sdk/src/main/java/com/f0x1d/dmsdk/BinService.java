package com.f0x1d.dmsdk;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Keep;

import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;

import java.util.List;

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
     * Called only when user is logged in
     *
     * @return user's username
     * @throws Exception
     */
    String getUsername() throws Exception;

    /**
     * Login button clicked
     * @param username
     * @param password
     * @throws Exception
     */
    void login(String username, String password) throws Exception;

    /**
     * Register button clicked
     * @param username
     * @param password
     * @throws Exception
     */
    void register(String username, String password) throws Exception;

    /**
     * @return true if user is logged in, false if not
     */
    boolean loggedIn();

    /**
     * Log out of account
     */
    void logout();

    /**
     * Get document content on slug
     * @param slug
     * @return document's content
     * @throws Exception
     */
    DocumentContent getDocumentContent(String slug) throws Exception;

    /**
     * Create document on slug (can be empty)
     * @param slug
     * @param content
     * @return created document's slug
     * @throws Exception
     */
    String createDocument(String slug, String content) throws Exception;

    /**
     * Edit document on slug
     * @param slug
     * @param content
     * @return edited document's slug
     * @throws Exception
     */
    String editDocument(String slug, String content) throws Exception;

    /**
     * @param slug
     * @return true if document can be edited, false if not, null if you set it in DocumentContent
     * @throws Exception
     */
    Boolean isEditableDocument(String slug) throws Exception;

    /**
     * @return list of cached documents, empty list if there are no such
     */
    List<UserDocument> getDocumentListFromCache();

    /**
     * @param slug
     * @return document's content from cache, null if there is no such
     */
    DocumentContent getContentFromCache(String slug);

    /**
     * Caches a document
     * @param slug
     * @param content
     * @param myDocument
     */
    void cacheDocument(String slug, String content, boolean myDocument);

    /**
     * @return true if should show item in bottom navigation, false if not
     */
    boolean showFoldersItem();

    /**
     * @return default folder, which is shown near folders item
     */
    Folder getDefaultFolder();

    /**
     * @return list of available folders, empty list if there is no such
     * @throws Exception
     */
    List<Folder> getAvailableFolders() throws Exception;

    /**
     * @param key
     * @return list of documents in folder with key
     * @throws Exception
     */
    List<UserDocument> getUserDocumentsForFolder(String key) throws Exception;

    default int getSDKVersion() {
        return Constants.LATEST_VERSION;
    }
}
