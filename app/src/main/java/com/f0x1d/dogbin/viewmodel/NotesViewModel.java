package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;

import java.util.Collections;
import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    public static class NotesViewModelFactory implements ViewModelProvider.Factory {

        private String folderKey;

        public NotesViewModelFactory(String folderKey) {
            this.folderKey = folderKey;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new NotesViewModel(App.getInstance(), folderKey);
        }
    }

    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<List<UserDocument>> mNotesListData = new MutableLiveData<>();
    private String mFolderKey;

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

                List<UserDocument> userDocuments = BinServiceUtils.getCurrentActiveService().getDocumentListFromCache();
                if (userDocuments.isEmpty()) {
                    return;
                }

                mLoadingStateData.postValue(LoadingState.LOADED);
                mNotesListData.postValue(userDocuments);
            }
        });
    }

    private void processError(Throwable t) {
        t.printStackTrace();

        mLoadingStateData.postValue(LoadingState.LOADED);
        mNotesListData.postValue(Collections.emptyList());
        Utils.runOnUiThread(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.error, t.getLocalizedMessage()), Toast.LENGTH_LONG).show());
    }

    public LiveData<LoadingState> getLoadingStateData() {
        return mLoadingStateData;
    }

    public LiveData<List<UserDocument>> getNotesListData() {
        return mNotesListData;
    }

    public enum LoadingState {
        LOADING, LOADED
    }
}
