package com.f0x1d.dogbin.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.base.BaseViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

import java.util.Collections;
import java.util.List;

public class FoldersViewModel extends BaseViewModel {

    private final MutableLiveData<List<Folder>> mFoldersData = new MutableLiveData<>();

    public FoldersViewModel(@NonNull Application application) {
        super(application);
        load();
    }

    public void load() {
        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                List<Folder> folders = BinServiceUtils.getCurrentActiveService().folders().getAvailableFolders();

                mLoadingStateData.postValue(LoadingState.LOADED);
                mFoldersData.postValue(folders);
            } catch (Exception e) {
                mFoldersData.postValue(Collections.emptyList());
                processError(e);
            }
        });
    }

    public LiveData<List<Folder>> getFoldersData() {
        return mFoldersData;
    }
}
