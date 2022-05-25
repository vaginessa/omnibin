package com.f0x1d.testmodule.module;

import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dmsdk.module.FoldersModule;
import com.f0x1d.testmodule.R;

import java.util.Collections;
import java.util.List;

public class TestFoldersModule extends FoldersModule {

    public TestFoldersModule(BinService binService) {
        super(binService);
    }

    @Override
    public boolean showFoldersItem() {
        return true;
    }

    @Override
    public Folder getDefaultFolder() {
        return Folder.create("Cool folder", service().getApplicationContext().getDrawable(R.drawable.ic_launcher_foreground), "test", true);
    }

    @Override
    public List<Folder> getAvailableFolders() throws Exception {
        return Collections.singletonList(getDefaultFolder());
    }

    @Override
    public List<UserDocument> getUserDocumentsForFolder(String key) throws Exception {
        return Collections.singletonList(
                UserDocument.createDocument("test", "Time here", false)
        );
    }
}
