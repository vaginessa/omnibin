package com.f0x1d.dogbin.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.FoxBinSavedNote;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinCreateDocumentRequest;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinCreateDocumentWithSlugRequest;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinCreatedDocumentResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinErrorResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinGetNoteResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinGetNotesResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinLoginRegisterRequest;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinLoginRegisterResponse;
import com.f0x1d.dogbin.network.model.foxbin.base.BaseFoxBinResponse;
import com.f0x1d.dogbin.network.okhttp.NetworkUtils;
import com.f0x1d.dogbin.network.retrofit.foxbin.FoxBinApi;
import com.f0x1d.dogbin.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class FoxBinService implements BinService {

    private static FoxBinService sInstance;

    public static FoxBinService getInstance() {
        return sInstance == null ? sInstance = new FoxBinService() : sInstance;
    }

    @Override
    public void init(Context applicationContext, Context dogbinMobileContext, SharedPreferences modulePreferences) {

    }

    @Override
    public String getDomain() {
        return "https://f0x1d.com/foxbin/";
    }

    @Override
    public String getSlugFromLink(String link) {
        return link.split("/")[4];
    }

    @Override
    public String getUsername() throws Exception {
        return App.getPreferencesUtil().getFoxBinUsername();
    }

    @Override
    public void login(String username, String password) throws Exception {
        FoxBinLoginRegisterRequest foxBinLoginRegisterRequest = FoxBinLoginRegisterRequest.create(username, password);
        doAuth(FoxBinApi.getInstance().getService().login(NetworkUtils.getBody(foxBinLoginRegisterRequest)).execute(), username);
    }

    @Override
    public void register(String username, String password) throws Exception {
        FoxBinLoginRegisterRequest foxBinLoginRegisterRequest = FoxBinLoginRegisterRequest.create(username, password);
        doAuth(FoxBinApi.getInstance().getService().register(NetworkUtils.getBody(foxBinLoginRegisterRequest)).execute(), username);
    }

    private void doAuth(Response<FoxBinLoginRegisterResponse> response, String username) throws Exception {
        checkResponseForError(response);

        FoxBinLoginRegisterResponse foxBinLoginRegisterResponse = response.body();
        App.getPreferencesUtil().setFoxBinToken(foxBinLoginRegisterResponse.getAccessToken());
        App.getPreferencesUtil().setFoxBinUsername(username);
    }

    @Override
    public boolean loggedIn() {
        return App.getPreferencesUtil().getFoxBinToken() != null;
    }

    @Override
    public void logout() {
        App.getPreferencesUtil().setFoxBinToken(null);
    }

    @Override
    public DocumentContent getDocumentContent(String slug) throws Exception {
        Response<FoxBinGetNoteResponse> response = FoxBinApi.getInstance().getService().getNote(slug, App.getPreferencesUtil().getFoxBinToken()).execute();
        checkResponseForError(response);

        FoxBinGetNoteResponse.FoxBinNote foxBinNote = response.body().getFoxBinNote();
        return DocumentContent.create(foxBinNote.getContent(), foxBinNote.getSlug(), foxBinNote.getEditable(), false /* Unsupported */);
    }

    @Override
    public String createDocument(String slug, String content) throws Exception {
        FoxBinCreateDocumentRequest foxBinCreateDocumentRequest = slug.isEmpty() ? new FoxBinCreateDocumentRequest() : new FoxBinCreateDocumentWithSlugRequest();
        foxBinCreateDocumentRequest.setContent(content);
        foxBinCreateDocumentRequest.setAccessToken(App.getPreferencesUtil().getFoxBinToken());
        if (!slug.isEmpty()) ((FoxBinCreateDocumentWithSlugRequest) foxBinCreateDocumentRequest).setSlug(slug);

        Response<FoxBinCreatedDocumentResponse> response =
                FoxBinApi.getInstance().getService().createDocument(NetworkUtils.getBody(foxBinCreateDocumentRequest)).execute();
        checkResponseForError(response);
        return response.body().getSlug();
    }

    @Override
    public String editDocument(String slug, String content) throws Exception {
        FoxBinCreateDocumentWithSlugRequest foxBinCreateDocumentRequest = new FoxBinCreateDocumentWithSlugRequest();
        foxBinCreateDocumentRequest.setContent(content);
        foxBinCreateDocumentRequest.setAccessToken(App.getPreferencesUtil().getFoxBinToken());
        foxBinCreateDocumentRequest.setSlug(slug);

        Response<FoxBinCreatedDocumentResponse> response =
                FoxBinApi.getInstance().getService().editDocument(NetworkUtils.getBody(foxBinCreateDocumentRequest)).execute();
        checkResponseForError(response);
        return response.body().getSlug();
    }

    @Override
    public boolean deleteDocument(String slug) throws Exception {
        Response<BaseFoxBinResponse> response = FoxBinApi.getInstance().getService().deleteNote(slug, App.getPreferencesUtil().getFoxBinToken()).execute();
        checkResponseForError(response);
        return response.body().isOk();
    }

    @Override
    public Boolean isEditableDocument(String slug) throws Exception {
        return null;
    }

    @Override
    public List<UserDocument> getDocumentListFromCache() {
        return Utils.toUserNotesFoxBin(App.getMyDatabase().getFoxBinSavedNoteDao().getAllSync(), false);
    }

    @Override
    public DocumentContent getContentFromCache(String slug) {
        FoxBinSavedNote savedNote = App.getMyDatabase().getFoxBinSavedNoteDao().getBySlugSync(slug);
        if (savedNote == null)
            return null;
        else
            return DocumentContent.create(savedNote.getContent(), slug, false, false);
    }

    @Override
    public void cacheDocument(String slug, String content, boolean myDocument) {
        if (!myDocument && App.getPreferencesUtil().cacheOnlyMy())
            return;

        App.getMyDatabase().getFoxBinSavedNoteDao().addToCache(FoxBinSavedNote.createNote(content, slug, Utils.currentTimeToString()));
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
                Response<FoxBinGetNotesResponse> response = FoxBinApi.getInstance().getService().getAllNotes(App.getPreferencesUtil().getFoxBinToken()).execute();
                checkResponseForError(response);

                List<FoxBinGetNoteResponse.FoxBinNote> documents = response.body().getNotes();
                List<UserDocument> userDocuments = new ArrayList<>();
                for (FoxBinGetNoteResponse.FoxBinNote document : documents) {
                    userDocuments.add(UserDocument.createDocument(document.getSlug(), new Date(document.getDate()).toLocaleString(), true));
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
        NetworkUtils.checkResponseForError(response, FoxBinErrorResponse.class);
    }
}
