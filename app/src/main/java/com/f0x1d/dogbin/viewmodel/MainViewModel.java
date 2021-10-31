package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.ui.activity.MainActivity;
import com.f0x1d.dogbin.ui.activity.text.TextViewerActivity;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Event;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.base.BaseViewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainViewModel extends BaseViewModel {

    public static final String EVENT_VIEW_TEXT = "view_text";

    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Boolean> mLoggedInData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShowFoldersItemData = new MutableLiveData<>();
    private final MutableLiveData<Folder> mDefaultFolderData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        load();
    }

    private void load() {
        Utils.getExecutor().execute(() -> {
            mLoggedInData.postValue(BinServiceUtils.getCurrentActiveService().loggedIn()); // synchronized anyway
            mShowFoldersItemData.postValue(BinServiceUtils.getCurrentActiveService().showFoldersItem());
            mDefaultFolderData.postValue(BinServiceUtils.getCurrentActiveService().getDefaultFolder());
        });
    }

    public void processIntent(Intent intent) {
        mExecutor.execute(() -> {
            if (intent.getAction() != null && intent.getAction().equals(TextViewerActivity.ACTION_TEXT_VIEW)) {
                String modulePackageName = intent.getStringExtra("module_package_name");
                BinServiceUtils.getBinServiceForPackageName(modulePackageName == null ? BinServiceUtils.FOXBIN_SERVICE : modulePackageName);

                mEventsData.postValue(buildNewActivityEvent(Uri.parse(intent.getStringExtra("url"))));
            } else if (intent.getData() != null) {
                App.getPreferencesUtil().setSelectedService(BinServiceUtils.getInbuiltServiceForUrl(intent.getData().toString()));
                BinServiceUtils.refreshCurrentService();

                mEventsData.postValue(buildNewActivityEvent(intent.getData()));
            }
        });
    }

    private Event buildNewActivityEvent(Uri uri) {
        return new Event(EVENT_VIEW_TEXT, uri);
    }

    public LiveData<Boolean> getLoggedInData() {
        return mLoggedInData;
    }

    public LiveData<Boolean> getShowFoldersItemData() {
        return mShowFoldersItemData;
    }

    public LiveData<Folder> getDefaultFolderData() {
        return mDefaultFolderData;
    }
}
