package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.network.okhttp.NetworkUtils;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogBinLoginViewModel extends AndroidViewModel {

    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mLoggedInData = new MutableLiveData<>();

    public DogBinLoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void login(String login, String password) {
        mLoadingStateData.setValue(LoadingState.LOADING);

        DogBinApi.getInstance().getService().login(NetworkUtils.getAuthBody(login, password)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mLoadingStateData.setValue(LoadingState.LOADED);

                mLoggedInData.setValue(response.body() != null);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                processError(t);
            }
        });
    }

    private void processError(Throwable t) {
        t.printStackTrace();

        mLoadingStateData.setValue(LoadingState.LOADED);
        Toast.makeText(getApplication(), getApplication().getString(R.string.error, t.getLocalizedMessage()), Toast.LENGTH_LONG).show();
    }

    public LiveData<LoadingState> getLoadingStateData() {
        return mLoadingStateData;
    }

    public LiveData<Boolean> getLoggedInData() {
        return mLoggedInData;
    }

    public enum LoadingState {
        LOADING, LOADED
    }
}
