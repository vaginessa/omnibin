package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dogbin.utils.Event;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.base.BaseBinServiceViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

import java.util.Collections;
import java.util.List;

public class FoldersViewModel extends BaseBinServiceViewModel {

    public static final String EVENT_TYPE_CLEAR_BACKSTACK = "clear_backstack";

    private final MutableLiveData<List<Folder>> mFoldersData = new MutableLiveData<>();

    public FoldersViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onServiceChanged(BinService service) {
        load();
    }

    public void load() {
        if (isServiceUnloaded()) return;

        mEventsData.setValue(new Event(EVENT_TYPE_CLEAR_BACKSTACK, Void.TYPE));

        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                List<Folder> folders = mCurrentService.folders().getAvailableFolders();

                mFoldersData.postValue(folders);
                mLoadingStateData.postValue(LoadingState.LOADED);
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
