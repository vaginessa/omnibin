package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.Event;
import com.f0x1d.dogbin.utils.ThreadingUtils;
import com.f0x1d.dogbin.viewmodel.base.BaseBinServiceViewModel;
import com.f0x1d.dogbin.viewmodel.base.LoadingState;

import static com.f0x1d.dogbin.ui.activity.text.TextEditActivity.ACTION_UPLOAD_TO_FOXBIN;

public class WritingViewModel extends BaseBinServiceViewModel {

    public static final String EVENT_TYPE_POSTED = "posted_document";
    public static final String EVENT_TYPE_SHOW_POSTING_DIALOG = "show_posting_dialog";

    private final boolean mInEditingMode;
    private final String mSlug;
    private final boolean mIntentToPost;
    private final boolean mIntentToCopy;

    public WritingViewModel(@NonNull Application application, Intent intent) {
        super(application);

        this.mInEditingMode = intent.getBooleanExtra("edit", false);
        this.mSlug = intent.getStringExtra("slug");
        this.mIntentToCopy = intent.getBooleanExtra("copy", true);
        this.mIntentToPost = !mInEditingMode && intent.getStringExtra(Intent.EXTRA_TEXT) != null && intent.getAction() != null &&
                (intent.getAction().equals(ACTION_UPLOAD_TO_FOXBIN) || intent.getAction().equals(Intent.ACTION_SEND));
    }

    @Override
    protected void onServiceChanged(BinService service) {
        if (isIntentToPost())
            sendDialogEvent();
    }

    public void sendDialogEvent() {
        if (isServiceUnloaded()) return;

        mEventsData.postValue(new Event(EVENT_TYPE_SHOW_POSTING_DIALOG, Void.TYPE, mCurrentService));
    }

    public void publish(String text, String slug, Bundle settings) {
        if (text == null || text.isEmpty() || isServiceUnloaded())
            return;

        mLoadingStateData.setValue(LoadingState.LOADING);

        ThreadingUtils.getExecutor().execute(() -> {
            try {
                String resultSlug;
                if (isInEditingMode())
                    resultSlug = mCurrentService.documents().editDocument(slug, text, settings);
                else
                    resultSlug = mCurrentService.documents().createDocument(slug, text, settings);

                String resultUrl = resultSlug == null ? null : mCurrentService.getDomain() + resultSlug;

                mLoadingStateData.postValue(LoadingState.LOADED);

                if (isIntentToCopy() && !isInEditingMode() && resultUrl != null) {
                    ClipboardManager clipboard = (ClipboardManager) getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(getApplication().getString(R.string.app_name), resultUrl);
                    clipboard.setPrimaryClip(clip);

                    ThreadingUtils.runOnUiThread(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.copied_to_clipboard, resultUrl), Toast.LENGTH_SHORT).show());
                }

                mEventsData.postValue(new Event(EVENT_TYPE_POSTED, resultUrl));
            } catch (Exception e) {
                processError(e);
            }
        });
    }

    public boolean isInEditingMode() {
        return mInEditingMode;
    }

    public String getSlug() {
        return mSlug;
    }

    public boolean isIntentToPost() {
        return mIntentToPost;
    }

    public boolean isIntentToCopy() {
        return mIntentToCopy;
    }
}
