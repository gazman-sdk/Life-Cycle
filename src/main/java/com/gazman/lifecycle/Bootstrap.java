package com.gazman.lifecycle;

import android.os.Handler;
import android.os.Looper;

import com.gazman.lifecycle.signals.BootstrapTimeSignal;
import com.gazman.lifecycle.signals.PostBootstrapTime;
import com.gazman.lifecycle.signals.RegistrationCompleteSignal;
import com.gazman.lifecycle.task.Scheduler;
import com.gazman.signals.Signals;
import com.gazman.signals.SignalsHelper;
import com.gazman.signals.context.G;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Ilya Gazman on 2/17/2015.
 */
public abstract class Bootstrap extends Registrar {

    private static final Object synObject = new Object();
    private static final AtomicBoolean bootstrapCompleted = new AtomicBoolean(false);
    private static final AtomicBoolean registrationCompleted = new AtomicBoolean(false);
<<<<<<< HEAD
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SignalsHelper signalsHelper = new SignalsHelper();
    private final RegistrationCompleteSignal registrationCompleteSignal = Signals.signal(RegistrationCompleteSignal.class).dispatcher;
    private final BootstrapTimeSignal bootstrapTimeSignal = Signals.signal(BootstrapTimeSignal.class).dispatcher;
    private final PostBootstrapTime postBootstrapTime = Signals.signal(PostBootstrapTime.class).dispatcher;
    private boolean coreInitialization;
=======
    protected static boolean killProcessOnExit = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SignalsHelper signalsHelper = new SignalsHelper();
    private final RegistrationCompleteSignal registrationCompleteSignal = SignalsBag.inject(RegistrationCompleteSignal.class).dispatcher;
    private final BootstrapTimeSignal bootstrapTimeSignal = SignalsBag.inject(BootstrapTimeSignal.class).dispatcher;
    private final PostBootstrapTime postBootstrapTime = SignalsBag.inject(PostBootstrapTime.class).dispatcher;
    private boolean coreInitialization;

    public Bootstrap(Context context) {
        G.init(context);
    }
>>>>>>> tmp

    public static boolean isBootstrapComplete() {
        return bootstrapCompleted.get();
    }

    public static boolean isRegistrationComplete() {
        return registrationCompleted.get();
    }

<<<<<<< HEAD
=======
    /**
     * Will dispatch DisposableSignal and then:<br>
     * - Will unregister all the signals in the system<br>
     * - Will remove all the singletons in the system, so GC will be able to destroy them
     */
    public static void exit(final Runnable callback) {
        Scheduler scheduler = new Scheduler();
        SignalsBag.inject(DisposableSignal.class).dispatcher.onDispose(scheduler);
        scheduler.start(() -> {
            G.IO.shutdown();
            G.main.removeCallbacksAndMessages(null);
            synchronized (synObject) {
                Registrar.buildersMap.clear();
                Registrar.classesMap.clear();
                Registrar.registrars.clear();
            }
            $SignalsTerminator.exit();
            ClassConstructor.singletons.clear();
            registrationCompleted.set(false);
            bootstrapCompleted.set(false);

            if (callback != null) {
                callback.run();
            }
            if (killProcessOnExit) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }
>>>>>>> tmp

    /**
     * Start the initialization process
     */
    public void start() {
        new Thread(() -> {
            Looper.prepare();
            initialize();
        }, "Registration Thread").start();
    }

    /**
     * Start the initialization process
     */
    public void initializeOnly(final Runnable completeCallback) {
        new Thread(() -> {
            Looper.prepare();
            synchronized (synObject) {
                if (!coreInitialization) {
                    coreInitialize();
                }
                G.main.post(completeCallback);
            }
        }, "Registration Thread").start();
    }

    /**
     * The entry point to the initialization process
     */
    private void initialize() {
        synchronized (synObject) {
            coreInitialize();
        }

        Scheduler scheduler = Factory.inject(Scheduler.class);
        scheduler.setLogTag("Bootstrap");
        bootstrapTimeSignal.onBootstrap(scheduler);
        postBootstrapTime.onPostBootstrapTime();
        scheduler.block();
        handler.post(() -> {
            registrationCompleteSignal.registrationCompleteHandler();
            bootstrapCompleted.set(true);
        });
    }

    private void coreInitialize() {
        if (coreInitialization) {
            throw new IllegalStateException(
                    "Initialization process has already been executed.");
        }
        coreInitialization = true;
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
}
