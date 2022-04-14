package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dmsdk.model.Folder;
import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.ui.activity.text.TextViewerActivity;
import com.f0x1d.dogbin.utils.BinServiceUtils;
import com.f0x1d.dogbin.utils.Event;
import com.f0x1d.dogbin.utils.Utils;
import com.f0x1d.dogbin.viewmodel.base.BaseViewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainViewModel extends BaseViewModel {

    public static final String EVENT_VIEW_TEXT = "view_text";
    public static final String EVENT_TOASTER_DIALOG = "toaster_dialog";

    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Boolean> mLoggedInData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShowFoldersItemData = new MutableLiveData<>();
    private final MutableLiveData<Folder> mDefaultFolderData = new MutableLiveData<>();
    private final MutableLiveData<String> mModuleNameData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mPublishButtonVisibleData = new MutableLiveData<>(true);

    public MainViewModel(@NonNull Application application) {
        super(application);
        load();
    }

    private void load() {
        Utils.getExecutor().execute(() -> {
            mModuleNameData.postValue(BinServiceUtils.getCurrentActiveService().getServiceShortName());
            mLoggedInData.postValue(BinServiceUtils.getCurrentActiveService().auth().loggedIn());
            mShowFoldersItemData.postValue(BinServiceUtils.getCurrentActiveService().folders().showFoldersItem());
            mDefaultFolderData.postValue(BinServiceUtils.getCurrentActiveService().folders().getDefaultFolder());

            if (!App.getPreferencesUtil().toasterShowed() && damnVTostersIsInstalled()) {
                mEventsData.postValue(new Event(EVENT_TOASTER_DIALOG, true));
            }
        });
    }

    public void processIntent(Intent intent, boolean recreate) {
        mExecutor.execute(() -> {
            if (intent.getAction() != null && intent.getAction().equals(TextViewerActivity.ACTION_TEXT_VIEW)) {
                String modulePackageName = intent.getStringExtra("module_package_name");
                BinServiceUtils.getBinServiceForPackageName(modulePackageName == null ? BinServiceUtils.FOXBIN_SERVICE : modulePackageName);

                mEventsData.postValue(buildNewActivityEvent(Uri.parse(intent.getStringExtra("url")), recreate));
            } else if (intent.getData() != null) {
                App.getPreferencesUtil().setSelectedService(BinServiceUtils.getInbuiltServiceForUrl(intent.getData().toString()));
                BinServiceUtils.refreshCurrentService();

                mEventsData.postValue(buildNewActivityEvent(intent.getData(), recreate));
            }
        });
    }

    public void setCurrentTab(@IdRes int itemId) {
        mPublishButtonVisibleData.setValue(itemId != R.id.settings_navigation);
    }

    private Event buildNewActivityEvent(Uri uri, boolean recreate) {
        return new Event(EVENT_VIEW_TEXT, new Pair<>(uri, recreate));
    }

    private boolean damnVTostersIsInstalled() {
        return Utils.isPackageInstalled("com.vtosters.android", getApplication().getPackageManager()) ||
                Utils.isPackageInstalled("com.vtosters.lite", getApplication().getPackageManager());
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

    public LiveData<String> getModuleIconData() {
        return mModuleNameData;
    }

    public LiveData<Boolean> getPublishButtonVisibleData() {
        return mPublishButtonVisibleData;
    }
}
