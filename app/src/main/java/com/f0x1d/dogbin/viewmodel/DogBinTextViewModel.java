package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;
import com.pddstudio.highlightjs.HighlightJsView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogBinTextViewModel extends AndroidViewModel implements DogBinApi.NetworkEventsListener, HighlightJsView.OnContentHighlightedListener {

    private MutableLiveData<String> mTextResponseData = new MutableLiveData<>();
    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsEditableData = new MutableLiveData<>();
    private MutableLiveData<String> mRedirectURLData = new MutableLiveData<>();
    private String mSlug;

    public DogBinTextViewModel(@NonNull Application application) {
        super(application);

        DogBinApi.getInstance().registerListener(this);
    }

    public void load(String slug) {
        this.mSlug = slug;
        mLoadingStateData.setValue(LoadingState.LOADING);

        DogBinApi.getInstance().getService().getDocumentText(slug).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mTextResponseData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                processError(t);
            }
        });
    }

    public void loadEditable(String slug) {
        DogBinApi.getInstance().getService().getDocumentTextHTML(slug).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String responseBody = response.body();
                mIsEditableData.setValue(responseBody != null && responseBody.contains("edit action  enabled"));
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

    public LiveData<String> getTextData() {
        return mTextResponseData;
    }

    public LiveData<LoadingState> getLoadingStateData() {
        return mLoadingStateData;
    }

    public LiveData<Boolean> getIsEditableData() {
        return mIsEditableData;
    }

    public LiveData<String> getIsRedirectData() {
        return mRedirectURLData;
    }

    public String getSlug() {
        return mSlug;
    }

    @Override
    public void onRedirect(String url) {
        mRedirectURLData.postValue(url);
    }

    @Override
    public void onHighlighted() {
        mLoadingStateData.postValue(LoadingState.LOADED);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        DogBinApi.getInstance().unregisterListener(this);
    }

    public enum LoadingState {
        LOADING, LOADED
    }
}
