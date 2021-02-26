package com.f0x1d.dogbin.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.DogbinSavedNote;
import com.f0x1d.dogbin.network.model.dogbin.DogBinApiKeyResponse;
import com.f0x1d.dogbin.network.model.dogbin.DogBinAuthRequest;
import com.f0x1d.dogbin.network.model.dogbin.DogBinDocumentInfoResponse;
import com.f0x1d.dogbin.network.model.dogbin.DogBinDocumentLinkResponse;
import com.f0x1d.dogbin.network.model.dogbin.DogBinDocumentResponse;
import com.f0x1d.dogbin.network.model.dogbin.DogBinErrorResponse;
import com.f0x1d.dogbin.network.okhttp.NetworkUtils;
import com.f0x1d.dogbin.network.retrofit.dogbin.DogBinApi;
import com.f0x1d.dogbin.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Response;

public class DogBinService implements BinService {

    private static final SimpleDateFormat sDogbinDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());

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
        return App.getPreferencesUtil().getUsername();
    }

    @Override
    public void login(String username, String password) throws Exception {
        DogBinAuthRequest dogBinAuthRequest = DogBinAuthRequest.create(username, password, Arrays.asList("list", "create", "update", "delete"), "omnibin");
        doAuth(DogBinApi.getInstance().getService().login(NetworkUtils.getBody(dogBinAuthRequest)).execute());
    }

    @Override
    public void register(String username, String password) throws Exception {
        DogBinAuthRequest dogBinAuthRequest = DogBinAuthRequest.create(username, password, Arrays.asList("list", "create", "update", "delete"), "omnibin");
        doAuth(DogBinApi.getInstance().getService().register(NetworkUtils.getBody(dogBinAuthRequest)).execute());
    }

    private void doAuth(Response<DogBinApiKeyResponse> response) throws Exception {
        checkResponseForError(response);

        DogBinApiKeyResponse dogBinApiKeyResponse = response.body();

        App.getPreferencesUtil().setApiKey(dogBinApiKeyResponse.getApiKey());
        App.getPreferencesUtil().setUsername(dogBinApiKeyResponse.getUsername());
    }

    @Override
    public boolean loggedIn() {
        return App.getPreferencesUtil().getApiKey() != null;
    }

    @Override
    public void logout() {
        App.getPreferencesUtil().setApiKey(null);
    }

    @Override
    public DocumentContent getDocumentContent(String slug) throws Exception {
        Response<DogBinDocumentInfoResponse> response = DogBinApi.getInstance().getService().getDocument(App.getPreferencesUtil().getApiKey(), slug).execute();
        checkResponseForError(response);

        DogBinDocumentInfoResponse dogBinDocumentInfoResponse = response.body();
        return DocumentContent.create(dogBinDocumentInfoResponse.getContent(), dogBinDocumentInfoResponse.getKey(), dogBinDocumentInfoResponse.isEditable(),
                !dogBinDocumentInfoResponse.getType().equals("PASTE"));
    }

    @Override
    public String createDocument(String slug, String content) throws Exception {
        Response<DogBinDocumentLinkResponse> response = DogBinApi.getInstance().getService().createDocument(App.getPreferencesUtil().getApiKey(),
                NetworkUtils.getDocumentBody(content, slug)).execute();
        checkResponseForError(response);

        return response.body().getSlug();
    }

    @Override
    public String editDocument(String slug, String content) throws Exception {
        return createDocument(slug, content);
    }

    @Override
    public Boolean isEditableDocument(String slug) throws Exception {
        return null;
    }

    @Override
    public List<UserDocument> getDocumentListFromCache() {
        return Utils.toUserNotes(App.getMyDatabase().getDogbinSavedNoteDao().getAllSync());
    }

    @Override
    public DocumentContent getContentFromCache(String slug) {
        DogbinSavedNote savedNote = App.getMyDatabase().getDogbinSavedNoteDao().getBySlugSync(slug);
        if (savedNote == null)
            return null;
        else
            return DocumentContent.create(savedNote.getContent(), slug, false, false);
    }

    @Override
    public void cacheDocument(String slug, String content, boolean myDocument) {
        if (!myDocument && App.getPreferencesUtil().cacheOnlyMy())
            return;

        App.getMyDatabase().getDogbinSavedNoteDao().addToCache(DogbinSavedNote.createNote(content, slug, Utils.currentTimeToString()));
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
    public List<UserDocument> getUserDocumentsForFolder(String key) throws Exception {
        switch (key) {
            case "my_notes":
                Response<List<DogBinDocumentResponse>> response = DogBinApi.getInstance().getService().getMyNotes(App.getPreferencesUtil().getApiKey()).execute();
                checkResponseForError(response);

                List<DogBinDocumentResponse> documents = response.body();
                List<UserDocument> userDocuments = new ArrayList<>();
                for (DogBinDocumentResponse document : documents) {
                    userDocuments.add(UserDocument.createDocument(document.getSlug(), sDogbinDateFormat.parse(document.getCreatedTime()).toLocaleString()));
                }

                return userDocuments;
            case "history":
            case "cache":
                return getDocumentListFromCache();

            default:
                return Collections.emptyList();
        }
    }

    private void checkResponseForError(Response response) throws Exception {
        NetworkUtils.checkResponseForError(response, DogBinErrorResponse.class);
    }
}
