package com.gazman_sdk.androidlifecycle.utils;

/**
 * Created by user on 04-Dec-14.
 */
public final class UnhandledExceptionHandler {

    public static Callback callback;

    public static interface Callback {
        void onApplicationError(Throwable throwable);
    }
}
