package com.f0x1d.dogbin.utils;

import android.os.Handler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadingUtils {

    private static final Handler sUiHandler = new Handler();
    private static final Executor sExecutor = Executors.newCachedThreadPool();

    public static Executor getExecutor() {
        return sExecutor;
    }

    public static void runOnUiThread(Runnable runnable) {
        sUiHandler.post(runnable);
    }
}
