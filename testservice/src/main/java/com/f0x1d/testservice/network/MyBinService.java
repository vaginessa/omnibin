package com.f0x1d.testservice.network;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Keep;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.CachedNote;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserNote;
import com.f0x1d.testservice.R;
import com.f0x1d.testservice.network.parser.DarkNetParser;
import com.f0x1d.testservice.network.retrofit.PasteBinApi;

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

@Keep
public class MyBinService implements BinService {

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    @Override
    public void init(Context applicationContext, Context dogbinMobileContext, SharedPreferences modulePreferences) {
        mContext = applicationContext;
        mSharedPreferences = modulePreferences;
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
        return "Pastebin module example";
    }

    @Override
    public boolean login(String username, String password) throws Exception {
        String token = PasteBinApi.getInstance().getService().login(new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_dev_key", "0ae8754d0ef14a23233caec62cd3055a")
                .addFormDataPart("api_user_name", username)
                .addFormDataPart("api_user_password", password)
                .build()).execute().body();

        if (token.contains("Bad API request"))
            throw new Exception(token);

        setToken(token);
        return true;
    }

    @Override
    public boolean register(String username, String password) throws Exception {
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
    public String getDocumentText(String slug) throws Exception {
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

        return text;
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
    public boolean isEditableNote(String slug) throws Exception {
        return false;
    }

    @Override
    public List<CachedNote> getNoteListFromCache() {
        return Collections.emptyList();
    }

    @Override
    public String getContentFromCache(String slug) {
        return null;
    }

    @Override
    public void cacheNote(String slug, String content, boolean myNote) {

    }

    @Override
    public boolean showFoldersItem() {
        return true;
    }

    @Override
    public Folder getDefaultFolder() {
        if (loggedIn())
            return Folder.create("My pastes", mContext.getDrawable(R.drawable.account_details), "my_notes");
        else
            return Folder.create("Go login lul", mContext.getDrawable(R.drawable.account_details), "nothing");
    }

    @Override
    public List<Folder> getAvailableFolders() throws Exception {
        List<Folder> folders = new ArrayList<>();
        if (loggedIn())
            folders.add(getDefaultFolder());
        folders.add(Folder.create("DarkNet", mContext.getDrawable(R.drawable.incognito), "darknet"));
        return folders;
    }

    @Override
    public List<UserNote> getUserNotesForFolder(String key) throws Exception {
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

                    List<UserNote> userNotes = new ArrayList<>();

                    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(pastesXml.getBytes()));
                    document.normalizeDocument();

                    NodeList pastes = document.getChildNodes().item(0).getChildNodes();
                    for (int i = 0; i < pastes.getLength(); i++) {
                        Node paste = pastes.item(i);

                        if (paste.hasChildNodes()) {
                            UserNote userNote = new UserNote();

                            NodeList pasteChildren = paste.getChildNodes();
                            for (int j = 0; j < pasteChildren.getLength(); j++) {
                                Node pasteChild = pasteChildren.item(j);

                                if (pasteChild.getNodeName().equals("paste_key"))
                                    userNote.setSlug(pasteChild.getTextContent());

                                if (pasteChild.getNodeName().equals("paste_date"))
                                    userNote.setTime(simpleDateFormat.format(new Date(Long.parseLong(pasteChild.getTextContent()) * 1000)));
                            }

                            userNotes.add(userNote);
                        }
                    }

                    return userNotes;
                } catch (Exception e) {
                    return Collections.emptyList();
                }

            default:
                return Collections.emptyList();
        }
    }

    private String getToken() {
        return mSharedPreferences.getString("pastebin_token", null);
    }

    private void setToken(String token) {
        mSharedPreferences.edit().putString("pastebin_token", token).apply();
    }
}
