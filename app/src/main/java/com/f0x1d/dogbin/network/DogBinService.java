package com.f0x1d.dogbin.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.CachedNote;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserNote;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.SavedNote;
import com.f0x1d.dogbin.network.okhttp.NetworkUtils;
import com.f0x1d.dogbin.network.parser.MyNotesParser;
import com.f0x1d.dogbin.network.parser.UsernameParser;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;
import com.f0x1d.dogbin.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DogBinService implements BinService {

    private static DogBinService sInstance;

    public static DogBinService getInstance() {
        return sInstance == null ? sInstance = new DogBinService() : sInstance;
    }

    @Override
    public void init(Context applicationContext, Context dogbinMobileContext, SharedPreferences modulePreferences) {

    }

    @Override
    public String getDomain() {
        return App.getPreferencesUtil().getDogbinDomain();
    }

    @Override
    public String getSlugFromLink(String link) {
        // https://del.dog/test
        return link.split("/")[3];
    }

    @Override
    public String getUsername() throws Exception {
        return UsernameParser.parse(DogBinApi.getInstance().getService().me().execute().body());
    }

    @Override
    public boolean login(String username, String password) throws Exception {
        String responsePage = DogBinApi.getInstance().getService().login(NetworkUtils.getAuthBody(username, password)).execute().body();
        return responsePage != null;
    }

    @Override
    public boolean register(String username, String password) throws Exception {
        String responsePage = DogBinApi.getInstance().getService().register(NetworkUtils.getAuthBody(username, password)).execute().body();
        return responsePage != null;
    }

    @Override
    public boolean loggedIn() {
        return DogBinApi.getInstance().getCookieJar().isDoggyClientCookieSaved();
    }

    @Override
    public void logout() {
        DogBinApi.getInstance().getCookieJar().clear();
        DogBinApi.getInstance().getCookieJar().clearPrefs();
    }

    @Override
    public String getDocumentText(String slug) throws Exception {
        return DogBinApi.getInstance().getService().getDocumentText(slug).execute().body();
    }

    @Override
    public String createDocument(String slug, String content) throws Exception {
        return Objects.requireNonNull(DogBinApi.getInstance().getService().createDocument(NetworkUtils.getDocumentBody(content, slug)).execute().body()).getSlug();
    }

    @Override
    public String editDocument(String slug, String content) throws Exception {
        return Objects.requireNonNull(DogBinApi.getInstance().getService().createDocument(NetworkUtils.getDocumentBody(content, slug)).execute().body()).getSlug();
    }

    @Override
    public boolean isEditableNote(String slug) throws Exception {
        String responsePage = DogBinApi.getInstance().getService().getDocumentTextHTML(slug).execute().body();
        return responsePage != null && responsePage.contains("edit action  enabled");
    }

    @Override
    public List<CachedNote> getNoteListFromCache() {
        return Utils.toCachedNotes(App.getMyDatabase().getSavedNoteDao().getAllSync());
    }

    @Override
    public String getContentFromCache(String slug) {
        SavedNote savedNote = App.getMyDatabase().getSavedNoteDao().getBySlugSync(slug);
        if (savedNote == null)
            return null;
        else
            return savedNote.getContent();
    }

    @Override
    public void cacheNote(String slug, String content, boolean myNote) {
        if (!myNote && App.getPreferencesUtil().cacheOnlyMy())
            return;

        App.getMyDatabase().getSavedNoteDao().addToCache(SavedNote.createNote(content, slug, Utils.currentTimeToString()));
    }

    @Override
    public boolean showFoldersItem() {
        return true;
    }

    @Override
    public Folder getDefaultFolder() {
        if (loggedIn())
            return Folder.create(App.getInstance().getString(R.string.my_notes), App.getInstance().getDrawable(R.drawable.ic_saved), "my_notes");
        else
            return Folder.create(App.getInstance().getString(R.string.history), App.getInstance().getDrawable(R.drawable.ic_history), "history");
    }

    @Override
    public List<Folder> getAvailableFolders() throws Exception {
        List<Folder> folders = new ArrayList<>();
        folders.add(getDefaultFolder());
        folders.add(Folder.create(App.getInstance().getString(R.string.cache), App.getInstance().getDrawable(R.drawable.ic_history), "cache"));
        return folders;
    }

    @Override
    public List<UserNote> getUserNotesForFolder(String key) throws Exception {
        switch (key) {
            case "my_notes":
                return Utils.toUserNotes(MyNotesParser.parse(DogBinApi.getInstance().getService().me().execute().body()));
            case "history":
            case "cache":
                return Utils.toUserNotes(App.getMyDatabase().getSavedNoteDao().getAllSync());

            default:
                return Collections.emptyList();
        }
    }
}
