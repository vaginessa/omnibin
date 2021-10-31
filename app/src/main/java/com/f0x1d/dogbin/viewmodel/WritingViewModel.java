package com.f0x1d.dogbin.viewmodel;

import static com.f0x1d.dogbin.ui.activity.text.TextEditActivity.ACTION_UPLOAD_TO_FOXBIN;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.base.BaseViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

public class WritingViewModel extends BaseViewModel {

    public static class WritingViewModelFactory implements ViewModelProvider.Factory {

        private final Intent mIntent;

        public WritingViewModelFactory(Intent intent) {
            this.mIntent = intent;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new WritingViewModel(App.getInstance(), mIntent);
        }
    }

    private final MutableLiveData<String> mResultUrlData = new MutableLiveData<>();

    private final boolean mInEditingMode;
    private final String mSlug;
    private final String mTextFromIntent;
    private final boolean mIntentToPost;
    private final boolean mIntentToCopy;

    public WritingViewModel(@NonNull Application application, Intent intent) {
        super(application);

        this.mInEditingMode = intent.getBooleanExtra("edit", false);
        this.mSlug = intent.getStringExtra("slug");
        this.mTextFromIntent = intent.getStringExtra(Intent.EXTRA_TEXT);
        this.mIntentToCopy = intent.getBooleanExtra("copy", true);
        this.mIntentToPost = !mInEditingMode && mTextFromIntent != null && intent.getAction() != null &&
                (intent.getAction().equals(ACTION_UPLOAD_TO_FOXBIN) || intent.getAction().equals(Intent.ACTION_SEND));
    }

    public void publish(String text, String slug) {
        if (text == null || text.isEmpty())
            return;

        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                String resultSlug;
                if (isInEditingMode())
                    resultSlug = BinServiceUtils.getCurrentActiveService().editDocument(slug, text);
                else
                    resultSlug = BinServiceUtils.getCurrentActiveService().createDocument(slug, text);

                String resultUrl = resultSlug == null ? null : BinServiceUtils.getCurrentActiveService().getDomain() + resultSlug;

                mLoadingStateData.postValue(LoadingState.LOADED);

                if (isIntentToCopy() && !isInEditingMode() && resultUrl != null) {
                    ClipboardManager clipboard = (ClipboardManager) getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(getApplication().getString(R.string.app_name), resultUrl);
                    clipboard.setPrimaryClip(clip);

                    Utils.runOnUiThread(() -> {
                        Toast.makeText(getApplication(), getApplication().getString(R.string.copied_to_clipboard, resultUrl), Toast.LENGTH_SHORT).show();
                    });
                }

                mResultUrlData.postValue(resultUrl);
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    public LiveData<String> getResultUrlData() {
        return mResultUrlData;
    }

    public boolean isInEditingMode() {
        return mInEditingMode;
    }

    public String getSlug() {
        return mSlug;
    }

    public String getTextFromIntent() {
        return mTextFromIntent;
    }

    public boolean isIntentToPost() {
        return mIntentToPost;
    }

    public boolean isIntentToCopy() {
        return mIntentToCopy;
    }
}
