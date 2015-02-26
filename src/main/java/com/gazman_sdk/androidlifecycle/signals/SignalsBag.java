package com.gazman_sdk.androidlifecycle.signals;

import java.util.HashMap;

/**
 * Created by Gazman on 2/24/2015.
 */
public final class SignalsBag {

    private static HashMap<Class<?>, Signal> map = new HashMap<>();

    private SignalsBag() {}

    public static <T> Signal<T> inject(Class<T> type) {
        Signal<T> signal = map.get(type);
        if (signal == null) {
            signal = new Signal<T>(type);
            map.put(type, signal);
        }
        return signal;
    }

    public static <T> Signal<? extends T> create(Class<? extends T> type) {
        return new Signal(type);
    }

}
