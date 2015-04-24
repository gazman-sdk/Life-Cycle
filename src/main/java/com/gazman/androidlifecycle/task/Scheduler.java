// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/android_life_cycle/blob/master/LICENSE.md
// =================================================================================================
package com.gazman.androidlifecycle.task;

import android.os.Handler;
import android.os.Looper;

import com.gazman.androidlifecycle.signal.Signal;
import com.gazman.androidlifecycle.signal.SignalsBag;
import com.gazman.androidlifecycle.task.signals.TasksCompleteSignal;
import com.gazman.androidlifecycle.task.signals.TimeOutSignal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Gazman on 3/4/2015.
 */
public class Scheduler {

    private Handler handler = new Handler(Looper.getMainLooper());
    private final Object blocker = new Object();
    private ArrayList<Signal> signals = new ArrayList<>();
    private TasksCompleteSignal tasksCompleteSignal;
    private long waitForMilliseconds;

    /**
     * Will wait for this signals to be dispatched
     * @param signals
     * @return
     */
    public Scheduler waitFor(Signal...signals){
        for (Signal signal : signals) {
            this.signals.add(signal);
        }
        return this;
    }

    public <T> T create(Class<T> type){
        Signal<T> signal = SignalsBag.create(type);
        waitFor(signal);
        return signal.dispatcher;
    }

    public <T> T inject(Class<T> type){
        Signal<T> signal = SignalsBag.inject(type);
        waitFor(signal);
        return signal.dispatcher;
    }

    /**
     * Will inject those signals and wait for them to be dispatched
     * @param signals
     * @return
     */
    public Scheduler waitFor(Class...signals){
        for (Class signal : signals) {
            this.signals.add(SignalsBag.inject(signal));
        }
        return this;
    }

    /**
     * Adds a time task for the given amount of milliseconds. You can call this method multiple times but only the last one counts.
     * @param milliseconds
     * @return
     */
    public Scheduler waitFor(long milliseconds){
        waitFor(SignalsBag.inject(TimeOutSignal.class));
        waitForMilliseconds = milliseconds;
        return this;
    }

    /**
     * Will block this thread until all signals is dispatched
     */
    public void block(){
        blockAfter(null);
    }

    /**
     * Will block this thread, right after the execution of the runnable, until  all signals is dispatched
     */
    public void blockAfter(Runnable runnable){
        validate();
        Class<?>[] interfaces = buildCallBack();
        if(runnable != null){
            runnable.run();
        }
        start(interfaces);
        synchronized (blocker){
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
    public void start(TasksCompleteSignal callback){
        this.tasksCompleteSignal = callback;
        validate();
        Class<?>[] interfaces = buildCallBack();
        start(interfaces);
    }

    private void validate() {
        if(signals.size() == 0){
            throw new IllegalStateException("No signals been added");
        }
    }

    private Class<?>[] buildCallBack() {
        ArrayList<Class> interfaces = new ArrayList<>();
        for (Signal signal : signals) {
            Class<?>[] interfacesArray = signal.dispatcher.getClass().getInterfaces();
            for (Class<?> aClass : interfacesArray) {
                if(!interfaces.contains(aClass)){
                    interfaces.add(aClass);
                }
            }
        }

        return interfaces.toArray(new Class[interfaces.size()]);
    }

    private void start(Class<?>[] interfaces) {
        startTimeTask();
        Callback callback = new Callback();
        Object proxy = Proxy.newProxyInstance(callback.getClass().getClassLoader(), interfaces, callback);
        for (Signal signal : signals) {
            signal.addListenerOnce(proxy);
        }
    }

    private void startTimeTask() {
        if(waitForMilliseconds > 0){
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
            if(count.decrementAndGet() <= 0){
                synchronized (blocker){
                    blocker.notifyAll();
                }
                if(tasksCompleteSignal != null){
                    tasksCompleteSignal.onTasksComplete();
                }
            }
            return null;
        }
    }
}
