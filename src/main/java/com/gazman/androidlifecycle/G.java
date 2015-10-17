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
import android.os.Looper;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * A ContextWrapper for application context
 */
public class G extends ContextWrapper {

    public static final Context app = new G();
    public static final Handler main = new Handler(Looper.getMainLooper());
    public static final int version = Build.VERSION.SDK_INT;
    private static String uuid;

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
        final TelephonyManager telephonyManager = (TelephonyManager) app.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + telephonyManager.getDeviceId();
        tmSerial = "" + telephonyManager.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(app.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        uuid = deviceUuid.toString();
    }
}
