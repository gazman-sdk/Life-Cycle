// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;

/**
 * A ContextWrapper for application context
 */
public class G extends ContextWrapper {

    public static final Context app = new G();
    public static final Handler main = new Handler(Looper.getMainLooper());
    public static final Handler IO;
    public static final int version = Build.VERSION.SDK_INT;
    private static String uuid;

    static {
        HandlerThread io = new HandlerThread("io");
        io.start();
        IO = new Handler(io.getLooper());
    }

    public static String getDeviceUniqueID(){
        return uuid;
    }
    public static String getPlayStoreLink(){
        return "https://market.android.com/details?id=" + G.app.getPackageName();
    }

    private G() {
        super(null);
    }

    @Override
    public Object getSystemService(String name) {
        if(getBaseContext() == null){
            return null;
        }
        return super.getSystemService(name);
    }

    private static boolean initialized = false;

    static void setApp(Context context) {
        if (initialized) {
            return;
        }
        initialized = true;
        ((G) app).attachBaseContext(context.getApplicationContext());
        initUUID();
    }

    /**
     * Credits to joe - http://stackoverflow.com/a/2853253/1129332
     */
    private static void initUUID() {
        try {
            uuid = Settings.Secure.getString(app.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        catch (Exception ignore){}
    }
}
