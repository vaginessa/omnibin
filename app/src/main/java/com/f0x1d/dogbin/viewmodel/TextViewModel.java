package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.pddstudio.highlightjs.HighlightJsView;

public class TextViewModel extends AndroidViewModel implements HighlightJsView.OnContentHighlightedListener {

    private MutableLiveData<String> mTextResponseData = new MutableLiveData<>();
    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsEditableData = new MutableLiveData<>();
    private MutableLiveData<String> mRedirectURLData = new MutableLiveData<>();

    private String mSlug;
    private boolean mMyNote;

    public TextViewModel(@NonNull Application application) {
        super(application);
    }

    public void checkMyNote(Intent intent) {
        mMyNote = intent.getBooleanExtra("my_note", false);
    }

    public void load(String slug) {
        this.mSlug = slug;

        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            DocumentContent content = BinServiceUtils.getCurrentActiveService().getContentFromCache(mSlug);
            if (content == null) {
                updateText(slug);
                return;
            }

            mTextResponseData.postValue(content.getContent());
            if (content.getEditable() != null) {
                mIsEditableData.postValue(content.getEditable());
            }
            updateText(slug);
        });
    }

    private void updateText(String slug) {
        try {
            DocumentContent body = BinServiceUtils.getCurrentActiveService().getDocumentContent(slug);
            if (App.getPreferencesUtil().isRedirectFromNoteEnabled() && mRedirectURLData.getValue() == null && body.isUrl())
                mRedirectURLData.postValue(body.getContent());

            if (mTextResponseData.getValue() != null && mTextResponseData.getValue().equals(body.getContent()) &&
                    mIsEditableData.getValue() != null && mIsEditableData.getValue().equals(body.getEditable()))
                return;

            mTextResponseData.postValue(body.getContent());
            if (body.getEditable() != null) {
                mIsEditableData.postValue(body.getEditable());
            }

            BinServiceUtils.getCurrentActiveService().cacheDocument(slug, body.getContent(), mMyNote);
        } catch (Exception e) {
            processError(e);
        }
    }

    public void loadEditable(String slug) {
        Utils.getExecutor().execute(() -> {
            try {
                Boolean editable = BinServiceUtils.getCurrentActiveService().isEditableDocument(slug);
                if (editable != null) {
                    mIsEditableData.postValue(BinServiceUtils.getCurrentActiveService().isEditableDocument(slug));
                }
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
    public void onHighlighted() {
        mLoadingStateData.postValue(LoadingState.LOADED);
    }

    public enum LoadingState {
        LOADING, LOADED
    }
}
