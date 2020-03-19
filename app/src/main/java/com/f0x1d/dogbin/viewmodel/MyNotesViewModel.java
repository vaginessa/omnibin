package com.f0x1d.dogbin.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.db.entity.MyNote;
import com.f0x1d.dogbin.network.parser.MyNotesParser;
import com.f0x1d.dogbin.network.retrofit.DogBinApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyNotesViewModel extends AndroidViewModel {

    private MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    private MutableLiveData<List<MyNote>> mMyNotesListData = new MutableLiveData<>();

    public MyNotesViewModel(@NonNull Application application) {
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
                processError(t);
            }
        });
    }

    private void processError(Throwable t) {
        t.printStackTrace();

        mLoadingStateData.setValue(LoadingState.LOADED);
        Toast.makeText(getApplication(), getApplication().getString(R.string.error, t.getLocalizedMessage()), Toast.LENGTH_LONG).show();
    }

    public LiveData<LoadingState> getLoadingStateData() {
        return mLoadingStateData;
    }

    public LiveData<List<MyNote>> getMyNotesListData() {
        return mMyNotesListData;
    }

    public enum LoadingState {
        LOADING, LOADED
    }
}
