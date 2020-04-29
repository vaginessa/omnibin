package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;

import java.util.Collections;
import java.util.List;

public class FoldersViewModel extends AndroidViewModel {

    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<List<Folder>> mFoldersData = new MutableLiveData<>();

    public FoldersViewModel(@NonNull Application application) {
        super(application);
    }

    public void load() {
        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                List<Folder> folders = BinServiceUtils.getCurrentActiveService().getAvailableFolders();

                mLoadingStateData.postValue(LoadingState.LOADED);
                mFoldersData.postValue(folders);
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    private void processError(Throwable t) {
        t.printStackTrace();

        mLoadingStateData.postValue(LoadingState.LOADED);
        mFoldersData.postValue(Collections.emptyList());
        Utils.runOnUiThread(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.error, t.getLocalizedMessage()), Toast.LENGTH_LONG).show());
    }

    public LiveData<LoadingState> getLoadingStateData() {
        return mLoadingStateData;
    }

    public LiveData<List<Folder>> getFoldersData() {
        return mFoldersData;
    }

    public enum LoadingState {
        LOADING, LOADED
    }
}
