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
import android.os.Handler;
import android.os.Looper;

import com.gazman.androidlifecycle.signal.SignalsBag;
import com.gazman.androidlifecycle.signal.SignalsHelper;
import com.gazman.androidlifecycle.signals.BootstrapTimeSignal;
import com.gazman.androidlifecycle.signals.RegistrationCompleteSignal;

/**
 * Created by Gazman on 2/17/2015.
 */
public abstract class Bootstrap extends Registrar {

    private Handler handler = new Handler(Looper.getMainLooper());
    private static Object synObject = new Object();
    private SignalsHelper signalsHelper = new SignalsHelper();
    private boolean initializationComplete;
    private RegistrationCompleteSignal registrationCompleteSignal = SignalsBag.inject(RegistrationCompleteSignal.class).dispatcher;
    private BootstrapTimeSignal bootstrapTimeSignal = SignalsBag.inject(BootstrapTimeSignal.class).dispatcher;

    public Bootstrap(Context context) {
        G.setContext(context);
    }

    /**
     * Start the initialization process
     */
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        }, "Registration Thread").start();
    }

    /**
     * The entry point to the initialization process
     */
    private void initialize() {
        synchronized (synObject) {
            if (initializationComplete) {
                throw new IllegalStateException(
                        "Initialization process has already been executed.");
            }
            initializationComplete = true;
            initRegistrars();
            for (Registrar registrar : registrars) {
                registrar.initClasses();
            }
            initClasses();
            for (Registrar registrar : registrars) {
                registrar.initSignals(signalsHelper);
            }
            initSignals(signalsHelper);
            registrars = null;
        }

        bootstrapTimeSignal.onBootstrap();
        handler.post(new Runnable() {
            @Override
            public void run() {
                registrationCompleteSignal.registrationCompleteHandler();
            }
        });
    }
}
