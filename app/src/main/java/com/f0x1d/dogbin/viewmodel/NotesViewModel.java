package com.f0x1d.dogbin.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.base.BaseViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotesViewModel extends BaseViewModel {

    public static class NotesViewModelFactory implements ViewModelProvider.Factory {

        private final String mFolderKey;

        public NotesViewModelFactory(String folderKey) {
            this.mFolderKey = folderKey;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new NotesViewModel(App.getInstance(), mFolderKey);
        }
    }

    private final MutableLiveData<List<UserDocument>> mNotesListData = new MutableLiveData<>();
    private final String mFolderKey;

    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    public NotesViewModel(@NonNull Application application, String folderKey) {
        super(application);
        this.mFolderKey = folderKey;
        load();
    }

    public void load() {
        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                List<UserDocument> userDocuments = BinServiceUtils.getCurrentActiveService().getUserDocumentsForFolder(mFolderKey);

                mLoadingStateData.postValue(LoadingState.LOADED);
                mNotesListData.postValue(userDocuments);
            } catch (Exception e) {
                processError(e);
                mNotesListData.postValue(Collections.emptyList());

                List<UserDocument> userDocuments = BinServiceUtils.getCurrentActiveService().getDocumentListFromCache();
                if (userDocuments.isEmpty()) {
                    return;
                }

                mNotesListData.postValue(userDocuments);
            }
        });
    }

    public void deleteNote(UserDocument userDocument) {
        mExecutor.execute(() -> {
            try {
                boolean deleted = BinServiceUtils.getCurrentActiveService().deleteDocument(userDocument.getSlug());

                List<UserDocument> userDocuments = mNotesListData.getValue();
                if (deleted)
                    userDocuments.remove(userDocument);

                mNotesListData.postValue(userDocuments);
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    public LiveData<List<UserDocument>> getNotesListData() {
        return mNotesListData;
    }
}
