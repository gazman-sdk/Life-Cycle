package com.gazman_sdk.androidlifecycle.signals;

import java.util.ArrayList;

/**
 * Created by Gazman on 2/24/2015.
 */
public class SignalsHelper {

    private ArrayList<Runnable> removables = new ArrayList<>();

    public <T> void addListener(Class<T> type, final T listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        signal.addListener(listener);
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    public <T> void addListener(Class<T> type, final Class<T> listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        signal.addListener(listener);
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    public <T> void addListenerOnce(Class<T> type, final T listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        signal.addListenerOnce(listener);
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    public <T> void addListenerOnce(Class<T> type, final Class<T> listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        signal.addListenerOnce(listener);
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    public <T> void removeListener(Class<T> type, T listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        signal.addListenerOnce(listener);
    }

    public <T> void removeListener(Class<T> type, Class<T> listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        signal.addListenerOnce(listener);
    }

    public void removeAll(){
        for (Runnable removable : removables) {
            removable.run();
        }
        removables.clear();
    }
}
