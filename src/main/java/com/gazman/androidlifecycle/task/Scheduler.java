// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle.task;

import android.os.Handler;
import android.os.Looper;

import com.gazman.androidlifecycle.Factory;
import com.gazman.androidlifecycle.log.Logger;
import com.gazman.androidlifecycle.signal.Signal;
import com.gazman.androidlifecycle.signal.SignalsBag;
import com.gazman.androidlifecycle.task.signals.TasksCompleteSignal;
import com.gazman.androidlifecycle.task.signals.TimeOutSignal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ilya Gazman on 3/4/2015.
 */
public class Scheduler {

    private Handler handler = new Handler(Looper.getMainLooper());
    private final Object blocker = new Object();
    private ArrayList<Signal> signals = new ArrayList<>();
    private TasksCompleteSignal tasksCompleteSignal;
    private long waitForMilliseconds;
    private Logger logger = Factory.injectWithParams(Logger.class, getClass());

    /**
     * Will prefix the tag to Scheduler for its logs
     *
     * @param tag the tag to prefix
     * @return this Scheduler
     */
    public Scheduler setLogTag(String tag) {
        logger.setPrefix(tag);
        return this;
    }

    /**
     * Will wait for those signals to be dispatched
     *
     * @param signals to wait for
     * @return this Scheduler
     */
    public Scheduler waitFor(Signal... signals) {
        Collections.addAll(this.signals, signals);
        return this;
    }

    /**
     * Create and wait for that signal to be dispatched.
     *
     * @param type Signal class
     * @param <T>  Signal type
     * @return The newly created signal
     */
    public <T> T create(Class<T> type) {
        Signal<T> signal = SignalsBag.create(type);
        waitFor(signal);
        return signal.dispatcher;
    }

    /**
     * Injects and wait for that signal to be dispatched.
     *
     * @param type Signal class
     * @param <T>  Signal type
     * @return The newly injected signal
     */
    public <T> T inject(Class<T> type) {
        Signal<T> signal = SignalsBag.inject(type);
        waitFor(signal);
        return signal.dispatcher;
    }

    /**
     * Will inject those signals and wait for them to be dispatched
     *
     * @param signals to wait for
     * @return this Scheduler
     */
    public Scheduler waitFor(Class... signals) {
        for (Class signal : signals) {
            this.signals.add(SignalsBag.inject(signal));
        }
        return this;
    }

    /**
     * Adds a time task for the given amount of milliseconds.
     * You can call this method multiple times but only the last one counts.
     *
     * @param milliseconds to wait
     * @return this Scheduler
     */
    public Scheduler waitFor(long milliseconds) {
        waitFor(SignalsBag.inject(TimeOutSignal.class));
        waitForMilliseconds = milliseconds;
        return this;
    }

    /**
     * Will block this thread until all signals are dispatched
     */
    public void block() {
        blockAfter(null);
    }

    /**
     * Will block this thread, right after the execution of the runnable
     * and until all the signals been dispatched
     */
    public void blockAfter(Runnable runnable) {
        if (signals.size() == 0) {
            if (runnable != null) {
                runnable.run();
            }
            return;
        }
        Class<?>[] interfaces = buildCallBack();
        if (runnable != null) {
            runnable.run();
        }
        start(interfaces);
        synchronized (blocker) {
            try {
                blocker.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Will use callBack to notify when all the signals been dispatched
     */
    public void start(TasksCompleteSignal callback) {
        if (signals.size() == 0) {
            callback.onTasksComplete();
        } else {
            this.tasksCompleteSignal = callback;
            Class<?>[] interfaces = buildCallBack();
            start(interfaces);
        }
    }

    private void logSignals() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < signals.size(); i++) {
            Signal signal = signals.get(i);
            stringBuilder.append("\n - ");
            stringBuilder.append(signal.originalType.getSimpleName());
        }
        logger.log("Starting with", stringBuilder);
    }

    private Class<?>[] buildCallBack() {
        ArrayList<Class> interfaces = new ArrayList<>();
        for (Signal signal : signals) {
            Class<?>[] interfacesArray = signal.dispatcher.getClass().getInterfaces();
            if (interfacesArray.length > 1) {
                throw new IllegalStateException("Can't handle signals with more than one interface - " + signal.dispatcher.getClass().getName());
            }
            for (Class<?> aClass : interfacesArray) {
                if (!interfaces.contains(aClass)) {
                    interfaces.add(aClass);
                }
            }
        }

        return interfaces.toArray(new Class[interfaces.size()]);
    }

    private void start(Class<?>[] interfaces) {
        logSignals();
        startTimeTask();
        Callback callback = new Callback();
        Object proxy = Proxy.newProxyInstance(callback.getClass().getClassLoader(), interfaces, callback);
        for (Signal signal : signals) {
            //noinspection unchecked
            signal.addListener(proxy);
        }
    }

    private void startTimeTask() {
        if (waitForMilliseconds > 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SignalsBag.inject(TimeOutSignal.class).dispatcher.onTimeOut();
                }
            }, waitForMilliseconds);
        }
    }

    private class Callback implements InvocationHandler {

        private AtomicInteger count = new AtomicInteger(signals.size());

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (count.decrementAndGet() <= 0) {
                synchronized (blocker) {
                    blocker.notifyAll();
                }
                if (tasksCompleteSignal != null) {
                    for (Signal signal : signals) {
                        //noinspection unchecked
                        signal.removeListener(this);
                    }
                    tasksCompleteSignal.onTasksComplete();
                }
            }
            logger.log("Completed", method.getName(), count);
            return null;
        }
    }
}
