package com.f0x1d.dogbin.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.base.BaseViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

public class LoginViewModel extends BaseViewModel {

    private final MutableLiveData<Boolean> mLoggedInData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsInLoginModeData = new MutableLiveData<>(true);

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void switchMode() {
        mIsInLoginModeData.setValue(!mIsInLoginModeData.getValue());
    }

    public void login(String login, String password) {
        doAuth(login, password, false);
    }

    public void register(String login, String password) {
        doAuth(login, password, true);
    }

    private void doAuth(String login, String password, boolean register) {
        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                if (register)
                    BinServiceUtils.getCurrentActiveService().auth().register(login, password);
                else
                    BinServiceUtils.getCurrentActiveService().auth().login(login, password);

                mLoadingStateData.postValue(LoadingState.LOADED);
                mLoggedInData.postValue(true);
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    public LiveData<Boolean> getLoggedInData() {
        return mLoggedInData;
    }

    public LiveData<Boolean> getIsInLoginModeData() {
        return mIsInLoginModeData;
    }
}
