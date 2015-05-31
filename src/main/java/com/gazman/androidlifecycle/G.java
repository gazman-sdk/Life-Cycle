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
import android.os.Handler;
import android.os.Looper;

/**
 * A ContextWrapper for application context
 */
public class G extends ContextWrapper {

    public static final Context app = new G();
    public static final Handler main = new Handler(Looper.getMainLooper());

    private G() {
        super(null);
    }


    private static boolean initialized = false;
    static void setApp(Context context) {
        if(initialized){
            return;
        }
        initialized = true;
        ((G)app).attachBaseContext(context.getApplicationContext());
    }
}
