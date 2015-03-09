// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/android_life_cycle/blob/master/LICENSE.md
// =================================================================================================
package com.gazman.androidlifecycle;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * A ContextWrapper for application context
 */
public class G extends ContextWrapper {

    public static final Context app = new G();

    private G() {
        super(null);
    }

    static void setApp(Context context) {
        ((G)app).attachBaseContext(context.getApplicationContext());
    }
}
