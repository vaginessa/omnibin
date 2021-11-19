package com.f0x1d.dogbin.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.base.BaseViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

public class SettingsViewModel extends BaseViewModel {

    private MutableLiveData<String> mUsernameData = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        load();
    }

    public void load() {
        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                String username = BinServiceUtils.getCurrentActiveService().auth().getUsername();

                mLoadingStateData.postValue(LoadingState.LOADED);
                mUsernameData.postValue(username);
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    public LiveData<String> getUsernameData() {
        return mUsernameData;
    }
}
