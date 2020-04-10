package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dogbin.App;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.SavedNote;
import com.f0x1d.dogbin.network.parser.MyNotesParser;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DogBinMyNotesViewModel extends AndroidViewModel {

    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<List<SavedNote>> mMyNotesListData = new MutableLiveData<>();

    private Executor mExecutor = Executors.newCachedThreadPool();
    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    public DogBinMyNotesViewModel(@NonNull Application application) {
        super(application);
    }

    public void load() {
        mLoadingStateData.setValue(LoadingState.LOADING);

        DogBinApi.getInstance().getService().me().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mLoadingStateData.setValue(LoadingState.LOADED);

                mMyNotesListData.setValue(MyNotesParser.parse(response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mExecutor.execute(() -> {
                    List<SavedNote> savedNotes = App.getMyDatabase().getSavedNoteDao().getAllSync();
                    if (savedNotes.isEmpty()) {
                        processError(t);
                        return;
                    }

                    mLoadingStateData.postValue(LoadingState.LOADED);
                    mMyNotesListData.postValue(savedNotes);

                    mUIHandler.post(() -> Toast.makeText(getApplication(), R.string.loaded_cache_list, Toast.LENGTH_SHORT).show());
                });
            }
        });
    }

    private void processError(Throwable t) {
        t.printStackTrace();

        mLoadingStateData.postValue(LoadingState.LOADED);
        mMyNotesListData.postValue(Collections.emptyList());
        mUIHandler.post(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.error, t.getLocalizedMessage()), Toast.LENGTH_LONG).show());
    }

    public LiveData<LoadingState> getLoadingStateData() {
        return mLoadingStateData;
    }

    public LiveData<List<SavedNote>> getMyNotesListData() {
        return mMyNotesListData;
    }

    public enum LoadingState {
        LOADING, LOADED
    }
}
