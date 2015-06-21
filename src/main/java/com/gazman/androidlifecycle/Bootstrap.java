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
import android.os.*;

import com.gazman.androidlifecycle.signal.$SignalsTerminator;
import com.gazman.androidlifecycle.signal.DisposableSignal;
import com.gazman.androidlifecycle.signal.SignalsBag;
import com.gazman.androidlifecycle.signal.SignalsHelper;
import com.gazman.androidlifecycle.signals.BootstrapTimeSignal;
import com.gazman.androidlifecycle.signals.PostBootstrapTime;
import com.gazman.androidlifecycle.signals.RegistrationCompleteSignal;
import com.gazman.androidlifecycle.task.Scheduler;
import com.gazman.androidlifecycle.task.signals.TasksCompleteSignal;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Ilya Gazman on 2/17/2015.
 */
public abstract class Bootstrap extends Registrar {

    private Handler handler = new Handler(Looper.getMainLooper());
    private static final Object synObject = new Object();
    private SignalsHelper signalsHelper = new SignalsHelper();
    private boolean initializationComplete;
    private RegistrationCompleteSignal registrationCompleteSignal = SignalsBag.inject(RegistrationCompleteSignal.class).dispatcher;
    private BootstrapTimeSignal bootstrapTimeSignal = SignalsBag.inject(BootstrapTimeSignal.class).dispatcher;
    private PostBootstrapTime postBootstrapTime = SignalsBag.inject(PostBootstrapTime.class).dispatcher;

    private static AtomicBoolean bootstrapCompleted = new AtomicBoolean(false);
    private static AtomicBoolean registrationCompleted = new AtomicBoolean(false);

    protected static boolean killProcessOnExit = false;

    public static boolean isBootstrapComplete() {
        return bootstrapCompleted.get();
    }

    public static boolean isRegistrationComplete() {
        return registrationCompleted.get();
    }

    public Bootstrap(Context context) {
        G.setApp(context);
    }

    /**
     * Start the initialization process
     */
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
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

            for (Registrar registrar : registrars) {
                registrar.initSettings();
            }
            initSettings();
            registrationCompleted.set(true);
            registrars.clear();
        }

        Scheduler scheduler = Factory.inject(Scheduler.class);
        scheduler.setLogTag("Bootstrap");
        bootstrapTimeSignal.onBootstrap(scheduler);
        postBootstrapTime.onPostBootstrapTime();
        scheduler.block();
        handler.post(new Runnable() {
            @Override
            public void run() {
                registrationCompleteSignal.registrationCompleteHandler  ();
                bootstrapCompleted.set(true);
            }
        });
    }

    /**
     *  Will dispatch DisposableSignal and then:<br>
     *  - Will unregister all the signals in the system<br>
     *  - Will remove all the singletons in the system, so GC will be able to destroy them
     */
    public static void exit() {
        Scheduler scheduler = new Scheduler();
        SignalsBag.inject(DisposableSignal.class).dispatcher.onDispose(scheduler);
        scheduler.start(new TasksCompleteSignal() {
            @Override
            public void onTasksComplete() {
                Registrar.buildersMap.clear();
                Registrar.classesMap.clear();
                Registrar.registrars.clear();
                $SignalsTerminator.exit();
                ClassConstructor.singletons.clear();
                if (killProcessOnExit) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
    }
}
