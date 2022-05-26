package com.f0x1d.dmsdk.module.base;

import androidx.annotation.Keep;
import com.f0x1d.dmsdk.BinService;

@Keep
public class BaseModule {

    private final BinService mBinService;

    public BaseModule(BinService binService) {
        this.mBinService = binService;
    }

    public BinService service() {
        return mBinService;
    }
}
