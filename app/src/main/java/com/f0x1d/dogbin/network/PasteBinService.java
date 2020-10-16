package com.f0x1d.dogbin.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.PastebinSavedNote;
import com.f0x1d.dogbin.network.parser.DarkNetParser;
import com.f0x1d.dogbin.network.retrofit.pastebin.PasteBinApi;
import com.f0x1d.dogbin.utils.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.MultipartBody;

/* Please don't read this code, i don't know under what i've written it */
public class PasteBinService implements BinService {

    private static PasteBinService sInstance;

    public static PasteBinService getInstance() {
        return sInstance == null ? sInstance = new PasteBinService() : sInstance;
    }

    @Override
    public void init(Context applicationContext, Context dogbinMobileContext, SharedPreferences modulePreferences) {

    }

    @Override
    public String getDomain() {
        return "https://pastebin.com/";
    }

    @Override
    public String getSlugFromLink(String link) {
        return link.split("/")[3];
    }

    @Override
    public String getUsername() throws Exception {
        return "pastebin";
    }

    @Override
    public void login(String username, String password) throws Exception {
        String token = PasteBinApi.getInstance().getService().login(new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_dev_key", "0ae8754d0ef14a23233caec62cd3055a")
                .addFormDataPart("api_user_name", username)
                .addFormDataPart("api_user_password", password)
                .build()).execute().body();

        if (token.contains("Bad API request"))
            throw new Exception(token);

        setToken(token);
    }

    @Override
    public void register(String username, String password) throws Exception {
        throw new Exception("Registration is not supported");
    }

    @Override
    public boolean loggedIn() {
        return getToken() != null;
    }

    @Override
    public void logout() {
        setToken(null);
    }

    @Override
    public DocumentContent getDocumentContent(String slug) throws Exception {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("api_dev_key", "0ae8754d0ef14a23233caec62cd3055a");
        if (getToken() != null)
            builder.addFormDataPart("api_user_key", getToken());
        builder.addFormDataPart("api_paste_key", slug);
        builder.addFormDataPart("api_option", "show_paste");

        String text = PasteBinApi.getInstance().getService().getText(builder.build()).execute().body();

        if (text.contains("Bad API request")) {
            text = PasteBinApi.getInstance().getRawService().getContent(slug).execute().body();

            if (text.contains("Bad API request"))
                throw new Exception(text);
        }

        return DocumentContent.create(text, slug, false, false);
    }

    @Override
    public String createDocument(String slug, String content) throws Exception {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("api_dev_key", "0ae8754d0ef14a23233caec62cd3055a");
        if (getToken() != null)
            builder.addFormDataPart("api_user_key", getToken());
        builder.addFormDataPart("api_paste_code", content);
        if (!slug.isEmpty())
            builder.addFormDataPart("api_paste_name", slug);
        builder.addFormDataPart("api_paste_expire_date", "N");
        builder.addFormDataPart("api_option", "paste");

        String url = PasteBinApi.getInstance().getService().paste(builder.build()).execute().body();

        if (url.contains("Bad API request"))
            throw new Exception(url);

        return url.replace("https://pastebin.com/", "");
    }

    @Override
    public String editDocument(String slug, String content) throws Exception {
        return null;
    }

    @Override
    public Boolean isEditableDocument(String slug) throws Exception {
        return null;
    }

    @Override
    public List<UserDocument> getDocumentListFromCache() {
        return Utils.toUserNotesPastebin(App.getMyDatabase().getPastebinSavedNoteDao().getAllSync());
    }

    @Override
    public DocumentContent getContentFromCache(String slug) {
        PastebinSavedNote savedNote = App.getMyDatabase().getPastebinSavedNoteDao().getBySlugSync(slug);
        if (savedNote == null)
            return null;
        else
            return DocumentContent.create(savedNote.getContent(), slug, false, false);
    }

    @Override
    public void cacheDocument(String slug, String content, boolean myDocument) {
        if (!myDocument && App.getPreferencesUtil().cacheOnlyMy())
            return;

        App.getMyDatabase().getPastebinSavedNoteDao().addToCache(PastebinSavedNote.createNote(content, slug, Utils.currentTimeToString()));
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
        if (loggedIn())
            folders.add(getDefaultFolder());
        folders.add(Folder.create("DarkNet", App.getInstance().getDrawable(R.drawable.ic_incognito), "darknet"));
        folders.add(Folder.create(App.getInstance().getString(R.string.cache), App.getInstance().getDrawable(R.drawable.ic_history), "cache"));
        return folders;
    }

    @Override
    public List<UserDocument> getUserDocumentsForFolder(String key) throws Exception {
        switch (key) {
            case "darknet":
                String html = PasteBinApi.getInstance().getRawService().getArchive().execute().body();
                return DarkNetParser.parse(html);

            case "my_notes":
                String pastesXml = PasteBinApi.getInstance().getService().getPastes(new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("api_dev_key", "0ae8754d0ef14a23233caec62cd3055a")
                        .addFormDataPart("api_user_key", getToken())
                        .addFormDataPart("api_option", "list")
                        .build()).execute().body();

                if (pastesXml.contains("Bad API request"))
                    throw new Exception(pastesXml);

                try {
                    pastesXml = "<root>" + pastesXml + "</root>";

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

                    List<UserDocument> userDocuments = new ArrayList<>();

                    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(pastesXml.getBytes()));
                    document.normalizeDocument();

                    NodeList pastes = document.getChildNodes().item(0).getChildNodes();
                    for (int i = 0; i < pastes.getLength(); i++) {
                        Node paste = pastes.item(i);

                        if (paste.hasChildNodes()) {
                            UserDocument userDocument = new UserDocument();

                            NodeList pasteChildren = paste.getChildNodes();
                            for (int j = 0; j < pasteChildren.getLength(); j++) {
                                Node pasteChild = pasteChildren.item(j);

                                if (pasteChild.getNodeName().equals("paste_key"))
                                    userDocument.setSlug(pasteChild.getTextContent());

                                if (pasteChild.getNodeName().equals("paste_date"))
                                    userDocument.setTime(simpleDateFormat.format(new Date(Long.parseLong(pasteChild.getTextContent()) * 1000)));
                            }

                            userDocuments.add(userDocument);
                        }
                    }

                    return userDocuments;
                } catch (Exception e) {
                    return Collections.emptyList();
                }

            case "history":
            case "cache":
                return getDocumentListFromCache();

            default:
                return Collections.emptyList();
        }
    }

    private String getToken() {
        return App.getPreferencesUtil().getPastebinToken();
    }

    private void setToken(String token) {
        App.getPreferencesUtil().setPastebinToken(token);
    }
}
