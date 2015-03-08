// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/android_life_cycle/blob/master/LICENSE.md
// =================================================================================================
package com.gazman.androidlifecycle.utils;

/**
 * Created by Ilya Gazman on 04-Dec-14.
 */
public final class UnhandledExceptionHandler {

    public static Callback callback;

    public static interface Callback {
        void onApplicationError(Throwable throwable);
    }
}
