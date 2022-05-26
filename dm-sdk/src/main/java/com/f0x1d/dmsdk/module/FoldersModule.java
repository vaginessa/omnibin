package com.f0x1d.dmsdk.module;

import androidx.annotation.Keep;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.base.BaseModule;

import java.util.Collections;
import java.util.List;

@Keep
public abstract class FoldersModule extends BaseModule {

    public FoldersModule(BinService binService) {
        super(binService);
    }

    /**
     * @return true if should show item in bottom navigation, false if not
     */
    public abstract boolean showFoldersItem();

    /**
     * @return default folder, which is shown near folders item
     */
    public abstract Folder getDefaultFolder();

    /**
     * @return list of available folders, empty list if there is no such
     * @throws Exception
     */
    public List<Folder> getAvailableFolders() throws Exception {
        return Collections.emptyList();
    }

    /**
     * @param key
     * @return list of documents in folder with key
     * @throws Exception
     */
    public abstract List<UserDocument> getUserDocumentsForFolder(String key) throws Exception;
}
