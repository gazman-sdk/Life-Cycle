// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A ContextWrapper for application context
 */
public class G extends ContextWrapper {

    @SuppressLint("StaticFieldLeak")
    public static final Context app = new G();
    public static final Handler main = new Handler(Looper.getMainLooper());
    /**
     * Single thread executor
     */
    public static final ExecutorService IO = Executors.newSingleThreadExecutor();
    /**
     * Cached Thread Pool
     */
    public static final ExecutorService CE = Executors.newCachedThreadPool();
    public static final int version = Build.VERSION.SDK_INT;

    @SuppressLint("HardwareIds")
    public static String getDeviceUniqueIDForFraudUsage() {
        try {
            return Settings.Secure.getString(app.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ignore) {
        }
        return null;
    }

    public static String getPlayStoreLink() {
        return "https://market.android.com/details?id=" + G.app.getPackageName();
    }

    private G() {
        super(null);
    }

    @Override
    public Object getSystemService(String name) {
        if (getBaseContext() == null) {
            return null;
        }
        return super.getSystemService(name);
    }

    private static boolean initialized = false;

    public static void init(Context context) {
        if (initialized) {
            return;
        }
        initialized = true;
        ((G) app).attachBaseContext(context.getApplicationContext());
    }
}
