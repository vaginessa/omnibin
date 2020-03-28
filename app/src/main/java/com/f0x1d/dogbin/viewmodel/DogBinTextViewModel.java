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
import com.f0x1d.dogbin.db.entity.SavedNote;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;
import com.f0x1d.dogbin.utils.Utils;
import com.pddstudio.highlightjs.HighlightJsView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogBinTextViewModel extends AndroidViewModel implements DogBinApi.NetworkEventsListener, HighlightJsView.OnContentHighlightedListener {

    private MutableLiveData<String> mTextResponseData = new MutableLiveData<>();
    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsEditableData = new MutableLiveData<>();
    private MutableLiveData<String> mRedirectURLData = new MutableLiveData<>();

    private String mSlug;
    private boolean mMyNote;

    private Executor mExecutor = Executors.newCachedThreadPool();
    private boolean mNoteCached;

    public DogBinTextViewModel(@NonNull Application application) {
        super(application);

        DogBinApi.getInstance().registerListener(this);
    }

    public void checkMyNote(Intent intent) {
        mMyNote = intent.getBooleanExtra("my_note", false);
    }

    public void load(String slug) {
        this.mSlug = slug;
        mLoadingStateData.setValue(LoadingState.LOADING);

        mExecutor.execute(() -> {
            SavedNote savedNote = App.getMyDatabase().getSavedNoteDao().getBySlugSync(mSlug);
            mNoteCached = savedNote != null;
            if (!mNoteCached) {
                updateText(slug);
                return;
            }

            mTextResponseData.postValue(savedNote.getContent());
            updateText(slug);
        });
    }

    private void updateText(String slug) {
        DogBinApi.getInstance().getService().getDocumentText(slug).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mTextResponseData.setValue(response.body());

                mExecutor.execute(DogBinTextViewModel.this::cacheNote);
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

    private void cacheNote() {
        if (!mMyNote && App.getPrefsUtil().cacheOnlyMy())
            return;

        SavedNote savedNote = App.getMyDatabase().getSavedNoteDao().getBySlugSync(mSlug);
        if (savedNote == null) {
            App.getMyDatabase().getSavedNoteDao().insert(SavedNote.createNote(mTextResponseData.getValue(), mSlug, Utils.currentTimeToString()));
            mNoteCached = true;
        } else if (!savedNote.getContent().equals(mTextResponseData.getValue())) {
            App.getMyDatabase().getSavedNoteDao().updateContentBySlug(mSlug, mTextResponseData.getValue(), Utils.currentTimeToString());
        }
    }

    private void processError(Throwable t) {
        if (mNoteCached) {
            Toast.makeText(getApplication(), R.string.loaded_cache_note, Toast.LENGTH_SHORT).show();
            return;
        }

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
