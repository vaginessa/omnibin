package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
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

public class WritingViewModel extends AndroidViewModel {

    public static class WritingViewModelFactory implements ViewModelProvider.Factory {

        private boolean editingMode;

        public WritingViewModelFactory(boolean editingMode) {
            this.editingMode = editingMode;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new WritingViewModel(App.getInstance(), editingMode);
        }
    }

    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<String> mResultSlugData = new MutableLiveData<>();

    private final boolean mInEditingMode;

    public WritingViewModel(@NonNull Application application, boolean editingMode) {
        super(application);
        mInEditingMode = editingMode;
    }

    public void publish(String text, String slug) {
        if (text == null || text.isEmpty())
            return;

        mLoadingStateData.setValue(LoadingState.LOADING);

        Utils.getExecutor().execute(() -> {
            try {
                String resultSlug;
                if (mInEditingMode)
                    resultSlug = BinServiceUtils.getCurrentActiveService().editDocument(slug, text);
                else
                    resultSlug = BinServiceUtils.getCurrentActiveService().createDocument(slug, text);

                mLoadingStateData.postValue(LoadingState.LOADED);
                mResultSlugData.postValue(resultSlug);
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

    public LiveData<LoadingState> getLoadingStateData() {
        return mLoadingStateData;
    }

    public LiveData<String> getResultSlugData() {
        return mResultSlugData;
    }

    public enum LoadingState {
        LOADING, LOADED
    }
}
