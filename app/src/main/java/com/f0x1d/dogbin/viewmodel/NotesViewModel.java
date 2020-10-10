package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dmsdk.model.UserDocument;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;

import java.util.Collections;
import java.util.List;

public class NotesViewModel extends AndroidViewModel {

    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<List<UserDocument>> mNotesListData = new MutableLiveData<>();
    private MutableLiveData<String> mFolderKeyData = new MutableLiveData<>("");

    public NotesViewModel(@NonNull Application application) {
        super(application);
    }

    public void load(String folderKey) {
        mFolderKeyData.setValue(folderKey);
        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                List<UserDocument> userDocuments = BinServiceUtils.getCurrentActiveService().getUserDocumentsForFolder(folderKey);

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

    public void load() {
        load(mFolderKeyData.getValue());
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
