package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mLoggedInData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mRegisteredData = new MutableLiveData<>();

    private MutableLiveData<Boolean> mIsInLoginModeData = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);

        mIsInLoginModeData.setValue(true);
    }

    public void switchMode() {
        mIsInLoginModeData.setValue(!mIsInLoginModeData.getValue());
    }

    public void login(String login, String password) {
        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                boolean logged = BinServiceUtils.getCurrentActiveService().login(login, password);

                mLoadingStateData.postValue(LoadingState.LOADED);
                mLoggedInData.postValue(logged);
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    public void register(String login, String password) {
        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                boolean registered = BinServiceUtils.getCurrentActiveService().register(login, password);

                mLoadingStateData.postValue(LoadingState.LOADED);
                mRegisteredData.postValue(registered);
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    private void processError(Throwable t) {
        t.printStackTrace();

        mLoadingStateData.postValue(LoadingState.LOADED);
        Utils.runOnUiThread(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.error, t.getLocalizedMessage()), Toast.LENGTH_LONG).show());
    }

    public LiveData<LoadingState> getLoadingStateData() {
        return mLoadingStateData;
    }

    public LiveData<Boolean> getLoggedInData() {
        return mLoggedInData;
    }

    public LiveData<Boolean> getRegisteredData() {
        return mRegisteredData;
    }

    public LiveData<Boolean> getIsInLoginModeData() {
        return mIsInLoginModeData;
    }

    public enum LoadingState {
        LOADING, LOADED
    }
}
