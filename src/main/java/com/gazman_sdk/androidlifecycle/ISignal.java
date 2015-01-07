package com.gazman_sdk.androidlifecycle;

/**
 * Created by user on 08-Dec-14.
 */
public interface ISignal<T> {
    void addListener(T listener);

    void removeListener(T listener);
}
