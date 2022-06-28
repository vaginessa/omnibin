package com.f0x1d.dogbin.viewmodel.base;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import com.f0x1d.dmsdk.BinService;
import com.f0x1d.dogbin.utils.ThreadingUtils;
import com.f0x1d.dogbin.utils.services.BinServiceUtils;

public abstract class BaseBinServiceViewModel extends BaseViewModel implements Observer<BinService> {

    protected BinService mCurrentService;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public BaseBinServiceViewModel(@NonNull Application application) {
        super(application);

        mHandler.post(() -> {
            BinServiceUtils.getInstanceData().observeForever(this);

            if (BinServiceUtils.loadingNeeded()) {
                ThreadingUtils.getExecutor().execute(BinServiceUtils::loadActiveServiceIfNeeded);
            }
        });
    }

    protected boolean isServiceUnloaded() {
        return mCurrentService == null;
    }

    protected void onServiceChanged(BinService service) {
    }

    @Override
    public void onChanged(BinService binService) {
        if (binService != null) {
            mCurrentService = binService;
            onServiceChanged(binService);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        BinServiceUtils.getInstanceData().removeObserver(this);
    }
}