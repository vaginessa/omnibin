package com.f0x1d.dmsdk;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Keep;

import com.f0x1d.dmsdk.model.CachedNote;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserNote;

import java.util.List;

@Keep
public interface BinService {

    void init(Context applicationContext, SharedPreferences modulePreferences);

    String getDomain();

    String getSlugFromLink(String link);

    String getUsername() throws Exception;

    boolean login(String username, String password) throws Exception;

    boolean register(String username, String password) throws Exception;

    boolean loggedIn();

    void logout();

    String getDocumentText(String slug) throws Exception;

    String createDocument(String slug, String content) throws Exception;

    String editDocument(String slug, String content) throws Exception;

    boolean isEditableNote(String slug) throws Exception;

    List<CachedNote> getNoteListFromCache();

    String getContentFromCache(String slug);

    void cacheNote(String slug, String content, boolean myNote);

    boolean showFoldersItem();

    Folder getDefaultFolder();

    List<Folder> getAvailableFolders() throws Exception;

    List<UserNote> getUserNotesForFolder(String key) throws Exception;
}
