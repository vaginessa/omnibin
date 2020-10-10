package com.f0x1d.dogbin.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.SavedNote;
import com.f0x1d.dogbin.network.model.ApiKeyResponse;
import com.f0x1d.dogbin.network.model.AuthRequest;
import com.f0x1d.dogbin.network.model.DocumentInfoResponse;
import com.f0x1d.dogbin.network.model.DocumentLinkResponse;
import com.f0x1d.dogbin.network.model.DocumentResponse;
import com.f0x1d.dogbin.network.model.ErrorResponse;
import com.f0x1d.dogbin.network.okhttp.NetworkUtils;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;
import com.f0x1d.dogbin.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class DogBinService implements BinService {

    private static SimpleDateFormat sDogbinDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());

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
        AuthRequest authRequest = AuthRequest.create(username, password, Arrays.asList("list", "create", "update", "delete"), "dogbin mobile");
        doAuth(DogBinApi.getInstance().getService().login(NetworkUtils.getAuthBody(authRequest)).execute());
    }

    @Override
    public void register(String username, String password) throws Exception {
        AuthRequest authRequest = AuthRequest.create(username, password, Arrays.asList("list", "create", "update", "delete"), "dogbin mobile");
        doAuth(DogBinApi.getInstance().getService().register(NetworkUtils.getAuthBody(authRequest)).execute());
    }

    private void doAuth(Response<ApiKeyResponse> response) throws Exception {
        checkResponseForError(response);

        ApiKeyResponse apiKeyResponse = response.body();

        App.getPreferencesUtil().setApiKey(apiKeyResponse.getApiKey());
        App.getPreferencesUtil().setUsername(apiKeyResponse.getUsername());
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
        Response<DocumentInfoResponse> response = DogBinApi.getInstance().getService().getDocument(App.getPreferencesUtil().getApiKey(), slug).execute();
        checkResponseForError(response);

        DocumentInfoResponse documentInfoResponse = response.body();
        return DocumentContent.create(documentInfoResponse.getContent(), documentInfoResponse.getKey(), documentInfoResponse.isEditable(),
                !documentInfoResponse.getType().equals("PASTE"));
    }

    @Override
    public String createDocument(String slug, String content) throws Exception {
        Response<DocumentLinkResponse> response = DogBinApi.getInstance().getService().createDocument(App.getPreferencesUtil().getApiKey(),
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
        return Utils.toUserNotes(App.getMyDatabase().getSavedNoteDao().getAllSync());
    }

    @Override
    public DocumentContent getContentFromCache(String slug) {
        SavedNote savedNote = App.getMyDatabase().getSavedNoteDao().getBySlugSync(slug);
        if (savedNote == null)
            return null;
        else
            return DocumentContent.create(savedNote.getContent(), slug, false, false);
    }

    @Override
    public void cacheDocument(String slug, String content, boolean myDocument) {
        if (!myDocument && App.getPreferencesUtil().cacheOnlyMy())
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
    public List<UserDocument> getUserDocumentsForFolder(String key) throws Exception {
        switch (key) {
            case "my_notes":
                Response<List<DocumentResponse>> response = DogBinApi.getInstance().getService().getMyNotes(App.getPreferencesUtil().getApiKey()).execute();
                checkResponseForError(response);

                List<DocumentResponse> documents = response.body();
                List<UserDocument> userDocuments = new ArrayList<>();
                for (DocumentResponse document : documents) {
                    userDocuments.add(UserDocument.createDocument(document.getSlug(), sDogbinDateFormat.parse(document.getCreatedTime()).toLocaleString()));
                }

                return userDocuments;
            case "history":
            case "cache":
                return Utils.toUserNotes(App.getMyDatabase().getSavedNoteDao().getAllSync());

            default:
                return Collections.emptyList();
        }
    }

    private void checkResponseForError(Response response) throws Exception {
        ResponseBody responseBody = response.errorBody();
        if (responseBody == null)
            return;

        ErrorResponse errorResponse = Utils.getGson().fromJson(responseBody.string(), ErrorResponse.class);
        if (errorResponse == null) {
            throw new Exception(response.toString());
        }
        throw new Exception(errorResponse.getMessage());
    }
}
