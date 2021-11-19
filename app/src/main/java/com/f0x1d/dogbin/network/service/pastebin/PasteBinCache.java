package com.f0x1d.dogbin.network.service.pastebin;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.CacheModule;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.db.entity.PastebinSavedNote;
import com.f0x1d.dogbin.utils.Utils;

import java.util.List;

public class PasteBinCache extends CacheModule {

    public PasteBinCache(BinService binService) {
        super(binService);
    }

    @Override
    public List<UserDocument> getDocumentListFromCache() {
        return Utils.toUserNotesPastebin(App.getMyDatabase().getPastebinSavedNoteDao().getAllSync(), false);
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
}
