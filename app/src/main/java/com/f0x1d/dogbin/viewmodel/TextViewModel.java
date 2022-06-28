package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dmsdk.model.DocumentContent;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.utils.Event;
import com.f0x1d.dogbin.utils.ThreadingUtils;
import com.f0x1d.dogbin.viewmodel.base.BaseBinServiceViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

public class TextViewModel extends BaseBinServiceViewModel {

    public static final String EVENT_TYPE_REDIRECT = "redirect";

    private final MutableLiveData<String> mSlugData = new MutableLiveData<>();
    private final MutableLiveData<String> mTextResponseData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsEditableData = new MutableLiveData<>();

    private final Intent mIntent;
    private final boolean mMyNote;

    public TextViewModel(@NonNull Application application, Intent intent) {
        super(application);

        this.mIntent = intent;
        mMyNote = intent.getBooleanExtra("my_note", false);
    }

    @Override
    protected void onServiceChanged(BinService service) {
        load();
    }

    public void load() {
        if (isServiceUnloaded()) return;

        mLoadingStateData.setValue(LoadingState.LOADING);

        ThreadingUtils.getExecutor().execute(() -> {
            String slug = mSlugData.getValue();
            if (slug == null) {
                slug = mCurrentService.getSlugFromLink(mIntent.getData().toString());
                mSlugData.postValue(slug);
            }

            String finalSlug = slug;
            loadEditable(finalSlug); // so hard w/o coroutines

            DocumentContent content = mCurrentService.cache().getContentFromCache(slug);
            if (content == null) {
                updateText(slug);
                return;
            }

            setupLiveData(content);
            updateText(slug);
        });
    }

    private void updateText(String slug) {
        try {
            DocumentContent body = mCurrentService.documents().getDocumentContent(slug);
            if (App.getPreferencesUtil().isRedirectFromNoteEnabled() && body.isUrl())
                mEventsData.postValue(new Event(EVENT_TYPE_REDIRECT, body.getContent()));

            if (mTextResponseData.getValue() != null && mTextResponseData.getValue().equals(body.getContent()) &&
                    mIsEditableData.getValue() != null && mIsEditableData.getValue().equals(body.getEditable()))
                return;

            setupLiveData(body);

            mCurrentService.cache().cacheDocument(slug, body.getContent(), mMyNote);
        } catch (Exception e) {
            processError(e);
        }
    }

    private void setupLiveData(DocumentContent content) {
        mLoadingStateData.postValue(LoadingState.LOADED);
        mTextResponseData.postValue(content.getContent());
        if (content.getEditable() != null) {
            mIsEditableData.postValue(content.getEditable());
        }
    }

    private void loadEditable(String slug) {
        ThreadingUtils.getExecutor().execute(() -> {
            try {
                Boolean editable = mCurrentService.documents().isEditableDocument(slug);
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
}
