package com.f0x1d.dogbin.network.service.foxbin;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.CacheModule;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.db.entity.FoxBinSavedNote;
import com.f0x1d.dogbin.utils.TimeUtils;
import com.f0x1d.dogbin.utils.services.ServicesConvertors;

import java.util.List;

public class FoxBinCache extends CacheModule {

    public FoxBinCache(BinService binService) {
        super(binService);
    }

    @Override
    public List<UserDocument> getDocumentListFromCache() {
        return ServicesConvertors.toUserNotesFoxBin(App.getMyDatabase().getFoxBinSavedNoteDao().getAllSync(), false);
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

        App.getMyDatabase().getFoxBinSavedNoteDao().addToCache(FoxBinSavedNote.createNote(content, slug, TimeUtils.currentTimeToString()));
    }
}
