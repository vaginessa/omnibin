package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.Event;
import com.f0x1d.dogbin.utils.ThreadingUtils;
import com.f0x1d.dogbin.viewmodel.base.BaseBinServiceViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotesViewModel extends BaseBinServiceViewModel {

    public static final String EVENT_TYPE_OPEN_NOTE = "open_note";

    private final MutableLiveData<List<UserDocument>> mNotesListData = new MutableLiveData<>();
    private final String mFolderKey;
    private final boolean mAvailableUnauthorized;

    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    public NotesViewModel(@NonNull Application application, String folderKey, boolean availableUnauthorized) {
        super(application);
        this.mFolderKey = folderKey;
        this.mAvailableUnauthorized = availableUnauthorized;
    }

    @Override
    protected void onServiceChanged(BinService service) {
        load();
    }

    public void load() {
        if (isServiceUnloaded()) return;
        if (!mCurrentService.auth().loggedIn() && !mAvailableUnauthorized) return;

        mLoadingStateData.setValue(LoadingState.LOADING);

        ThreadingUtils.getExecutor().execute(() -> {
            try {
                List<UserDocument> userDocuments = mCurrentService.folders().getUserDocumentsForFolder(mFolderKey);

                mNotesListData.postValue(userDocuments);
                mLoadingStateData.postValue(LoadingState.LOADED);
            } catch (Exception e) {
                processError(e);

                List<UserDocument> userDocuments = mCurrentService.cache().getDocumentListFromCache();
                mNotesListData.postValue(userDocuments.isEmpty() ? Collections.emptyList() : userDocuments);
            }
        });
    }

    public void open(UserDocument userDocument) {
        if (isServiceUnloaded()) return;

        mEventsData.setValue(new Event(EVENT_TYPE_OPEN_NOTE, mCurrentService.getDomain() + userDocument.getSlug(), userDocument.myNote()));
    }

    public void copyUrl(UserDocument userDocument) {
        if (isServiceUnloaded()) return;

        String delDogUrl = mCurrentService.getDomain() + userDocument.getSlug();

        ClipboardManager clipboard = (ClipboardManager) getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getApplication().getString(R.string.app_name), delDogUrl);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getApplication(), getApplication().getString(R.string.copied_to_clipboard, delDogUrl), Toast.LENGTH_SHORT).show();
    }

    public void deleteNote(UserDocument userDocument) {
        if (isServiceUnloaded()) return;

        mExecutor.execute(() -> {
            try {
                if (mCurrentService.documents().deleteDocument(userDocument.getSlug())) {
                    List<UserDocument> userDocuments = mNotesListData.getValue();
                    userDocuments.remove(userDocument);

                    mNotesListData.postValue(userDocuments);
                }
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    public boolean noteDeletable(UserDocument userDocument) {
        if (isServiceUnloaded())
            return false;
        else
            return mCurrentService.documents().canDelete(userDocument);
    }

    public LiveData<List<UserDocument>> getNotesListData() {
        return mNotesListData;
    }
}
