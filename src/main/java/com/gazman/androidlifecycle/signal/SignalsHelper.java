// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle.signal;

import com.gazman.androidlifecycle.Factory;
import com.gazman.androidlifecycle.SingleTon;

import java.util.ArrayList;

/**
 * Created by Ilya Gazman on 2/24/2015.
 */
public class SignalsHelper {

    private ArrayList<Runnable> removables = new ArrayList<>();


    /**
     * Will call to signal.addListener(listener)
     */
    public <T> void addListener(final Signal<T> signal, final T listener) {
        signal.addListener(listener);
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    /**
     * Will call to signal.addListener(listener)
     */
    public <T> void addListener(final Signal<T> signal, final Class<? extends T> listener) {
        if (listener.isAssignableFrom(SingleTon.class)) {
            signal.addListener(Factory.inject(listener));
        } else {
            signal.addListener(listener);
        }
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    /**
     * Will call to SignalsBag.inject(signal).addListener(listener)
     */
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

    /**
     * Will call to SignalsBag.inject(signal).addListener(listener)
     */
    public <T> void addListener(Class<T> type, final Class<? extends T> listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        if (SingleTon.class.isAssignableFrom(listener)) {
            signal.addListener(Factory.inject(listener));
        } else {
            signal.addListener(listener);
        }
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    /**
     * Will call to signal.addListenerOnce(listener)
     */
    public <T> void addListenerOnce(final Signal<T> signal, final T listener) {
        signal.addListenerOnce(listener);
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    /**
     * Will call to signal.addListenerOnce(listener)
     */
    public <T> void addListenerOnce(final Signal<T> signal, final Class<T> listener) {
        signal.addListenerOnce(listener);
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    /**
     * Will call to SignalsBag.inject(signal).addListenerOnce(listener)
     */
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

    /**
     * Will call to SignalsBag.inject(signal).addListenerOnce(listener)
     */
    public <T> void addListenerOnce(Class<T> type, final Class<? extends T> listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        signal.addListenerOnce(listener);
        removables.add(new Runnable() {
            @Override
            public void run() {
                signal.removeListener(listener);
            }
        });
    }

    /**
     * Will call to SignalsBag.inject(signal).removeListener(listener)
     */
    public <T> void removeListener(Class<T> type, T listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        signal.removeListener(listener);
    }

    /**
     * Will call to SignalsBag.inject(signal).removeListener(listener)
     */
    public <T> void removeListener(Class<T> type, Class<T> listener) {
        final Signal<T> signal = SignalsBag.inject(type);
        if (listener.isAssignableFrom(SingleTon.class)) {
            signal.removeListener(Factory.inject(listener));
        } else {
            signal.removeListener(listener);
        }
    }

    /**
     * Will call to signal.removeListener(listener)
     */
    public <T> void removeListener(Signal<T> signal, T listener) {
        signal.removeListener(listener);
    }

    /**
     * Will remove all signals that been added using this SignalsHelper.
     */
    public void removeAll() {
        for (Runnable removable : removables) {
            removable.run();
        }
        removables.clear();
    }

}
