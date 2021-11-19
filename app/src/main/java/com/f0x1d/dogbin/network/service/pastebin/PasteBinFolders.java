package com.f0x1d.dogbin.network.service.pastebin;

import static com.f0x1d.dogbin.network.service.pastebin.PasteBinService.isResponseOk;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.FoldersModule;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.BuildConfig;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.network.parser.DarkNetParser;
import com.f0x1d.dogbin.network.retrofit.pastebin.PasteBinApi;

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

public class PasteBinFolders extends FoldersModule {

    public PasteBinFolders(BinService binService) {
        super(binService);
    }

    @Override
    public boolean showFoldersItem() {
        return true;
    }

    @Override
    public Folder getDefaultFolder() {
        if (service().auth().loggedIn())
            return Folder.create(App.getInstance().getString(R.string.my_notes), App.getInstance().getDrawable(R.drawable.ic_saved), "my_notes");
        else
            return Folder.create(App.getInstance().getString(R.string.history), App.getInstance().getDrawable(R.drawable.ic_history), "history");
    }

    @Override
    public List<Folder> getAvailableFolders() throws Exception {
        List<Folder> folders = new ArrayList<>();
        if (service().auth().loggedIn())
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
                        .addFormDataPart("api_dev_key", BuildConfig.PASTEBIN_API_KEY)
                        .addFormDataPart("api_user_key", App.getPreferencesUtil().getPastebinToken())
                        .addFormDataPart("api_option", "list")
                        .build()).execute().body();

                if (!isResponseOk(pastesXml))
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
                return service().cache().getDocumentListFromCache();

            default:
                return Collections.emptyList();
        }
    }
}
