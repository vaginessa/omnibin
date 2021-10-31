package com.f0x1d.dogbin.viewmodel.base;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.Event;
import com.f0x1d.dogbin.utils.Utils;

public class BaseViewModel extends AndroidViewModel {

    protected final MutableLiveData<LoadingState> mLoadingStateData = new MutableLiveData<>();
    protected final MutableLiveData<Event> mEventsData = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    protected void processError(Throwable t) {
        t.printStackTrace();

        mLoadingStateData.postValue(LoadingState.LOADED);
        Utils.runOnUiThread(() -> Toast.makeText(getApplication(), getApplication().getString(R.string.error, t.getLocalizedMessage()), Toast.LENGTH_LONG).show());
    }

    public LiveData<LoadingState> getLoadingStateData() {
        return mLoadingStateData;
    }

    public LiveData<Event> getEventsData() {
        return mEventsData;
    }
}
