package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.pddstudio.highlightjs.HighlightJsView;

public class TextViewModel extends AndroidViewModel implements DogBinApi.NetworkEventsListener, HighlightJsView.OnContentHighlightedListener {

    private MutableLiveData<String> mTextResponseData = new MutableLiveData<>();
    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsEditableData = new MutableLiveData<>();
    private MutableLiveData<String> mRedirectURLData = new MutableLiveData<>();

    private String mSlug;
    private boolean mMyNote;

    public TextViewModel(@NonNull Application application) {
        super(application);

        DogBinApi.getInstance().registerListener(this);
    }

    public void checkMyNote(Intent intent) {
        mMyNote = intent.getBooleanExtra("my_note", false);
    }

    public void load(String slug) {
        this.mSlug = slug;
        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            String content = BinServiceUtils.getCurrentActiveService().getContentFromCache(mSlug);
            if (content == null) {
                updateText(slug);
                return;
            }

            mTextResponseData.postValue(content);
            updateText(slug);
        });
    }

    private void updateText(String slug) {
        try {
            String body = BinServiceUtils.getCurrentActiveService().getDocumentText(slug);
            if (mTextResponseData.getValue() != null && mTextResponseData.getValue().equals(body))
                return;

            mTextResponseData.postValue(body);

            BinServiceUtils.getCurrentActiveService().cacheNote(slug, body, mMyNote);
        } catch (Exception e) {
            processError(e);
        }
    }

    public void loadEditable(String slug) {
        Utils.getExecutor().execute(() -> {
            try {
                mIsEditableData.postValue(BinServiceUtils.getCurrentActiveService().isEditableNote(slug));
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

    public void setLoading() {
        mLoadingStateData.setValue(LoadingState.LOADING);
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
        if (App.getPrefsUtil().isRedirectFromNoteEnabled() && mRedirectURLData.getValue() == null)
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
