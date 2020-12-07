package com.gazman.lifecycle.utils;

/**
 * Created by Ilya Gazman on 04-Dec-14.
 */
public final class UnhandledExceptionHandler {

    public static Callback callback;

    public interface Callback {
        void onApplicationError(Throwable throwable);
    }
}
