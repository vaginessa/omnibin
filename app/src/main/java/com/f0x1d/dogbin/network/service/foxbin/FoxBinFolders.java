package com.f0x1d.dogbin.network.service.foxbin;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.FoldersModule;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinErrorResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinGetNoteResponse;
import com.f0x1d.dogbin.network.model.foxbin.FoxBinGetNotesResponse;
import com.f0x1d.dogbin.network.okhttp.NetworkUtils;
import com.f0x1d.dogbin.network.retrofit.foxbin.FoxBinApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class FoxBinFolders extends FoldersModule {

    public FoxBinFolders(BinService binService) {
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
        folders.add(getDefaultFolder());
        folders.add(Folder.create(App.getInstance().getString(R.string.cache), App.getInstance().getDrawable(R.drawable.ic_history), "cache"));
        return folders;
    }

    @Override
    public List<UserDocument> getUserDocumentsForFolder(String key) throws Exception {
        switch (key) {
            case "my_notes":
                Response<FoxBinGetNotesResponse> response = FoxBinApi.getInstance().getService().getAllNotes(App.getPreferencesUtil().getFoxBinToken()).execute();
                NetworkUtils.checkResponseForError(response, FoxBinErrorResponse.class);

                List<FoxBinGetNoteResponse.FoxBinNote> documents = response.body().getNotes();
                List<UserDocument> userDocuments = new ArrayList<>();
                for (FoxBinGetNoteResponse.FoxBinNote document : documents) {
                    userDocuments.add(UserDocument.createDocument(document.getSlug(), new Date(document.getDate()).toLocaleString(), true));
                }

                return userDocuments;

            case "history":
            case "cache":
                return service().cache().getDocumentListFromCache();

            default:
                return Collections.emptyList();
        }
    }
}
