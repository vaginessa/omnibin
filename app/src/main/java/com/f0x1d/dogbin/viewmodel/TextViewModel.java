package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.base.BaseViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

public class TextViewModel extends BaseViewModel {

    public static class TextViewModelFactory implements ViewModelProvider.Factory {

        private final Intent mIntent;

        public TextViewModelFactory(Intent intent) {
            this.mIntent = intent;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TextViewModel(App.getInstance(), mIntent);
        }
    }

    private final MutableLiveData<String> mSlugData = new MutableLiveData<>();
    private final MutableLiveData<String> mTextResponseData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsEditableData = new MutableLiveData<>();
    private final MutableLiveData<String> mRedirectURLData = new MutableLiveData<>();

    private final Intent mIntent;
    private final boolean mMyNote;

    public TextViewModel(@NonNull Application application, Intent intent) {
        super(application);

        this.mIntent = intent;
        mMyNote = intent.getBooleanExtra("my_note", false);

        load();
    }

    public void load() {
        mLoadingStateData.setValue(LoadingState.LOADED);

        Utils.getExecutor().execute(() -> {
            String slug = mSlugData.getValue();
            if (slug == null) {
                slug = BinServiceUtils.getCurrentActiveService().getSlugFromLink(mIntent.getData().toString());
                mSlugData.postValue(slug);
            }

            String finalSlug = slug;
            loadEditable(finalSlug); // so hard w/o coroutines

            DocumentContent content = BinServiceUtils.getCurrentActiveService().getContentFromCache(slug);
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

    private void loadEditable(String slug) {
        Utils.getExecutor().execute(() -> {
            try {
                Boolean editable = BinServiceUtils.getCurrentActiveService().isEditableDocument(slug);
                if (editable != null) {
                    mIsEditableData.postValue(editable);
                }
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    public LiveData<String> getSlugData() {
        return mSlugData;
    }

    public LiveData<String> getTextData() {
        return mTextResponseData;
    }

    public LiveData<Boolean> getIsEditableData() {
        return mIsEditableData;
    }

    public LiveData<String> getIsRedirectData() {
        return mRedirectURLData;
    }
}
