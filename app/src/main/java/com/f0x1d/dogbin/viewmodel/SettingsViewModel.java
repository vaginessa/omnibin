package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dogbin.utils.ThreadingUtils;
import com.f0x1d.dogbin.viewmodel.base.BaseBinServiceViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

public class SettingsViewModel extends BaseBinServiceViewModel {

    private final MutableLiveData<Boolean> mLoggedInData = new MutableLiveData<>();
    private final MutableLiveData<String> mUsernameData = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onServiceChanged(BinService service) {
        load();
    }

    private void load() {
        mLoadingStateData.setValue(LoadingState.LOADING);

        ThreadingUtils.getExecutor().execute(() -> {
            try {
                boolean loggedIn = mCurrentService.auth().loggedIn();
                mLoggedInData.postValue(loggedIn);

                if (loggedIn) {
                    mUsernameData.postValue(mCurrentService.auth().getUsername());
                }

                mLoadingStateData.postValue(LoadingState.LOADED);
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    public void logout() {
        if (isServiceUnloaded()) return;

        if (mCurrentService.auth().loggedIn())
            mCurrentService.auth().logout();
    }

    public LiveData<Boolean> getLoggedInData() {
        return mLoggedInData;
    }

    public LiveData<String> getUsernameData() {
        return mUsernameData;
    }
}
