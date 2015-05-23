// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/android_life_cycle/blob/master/LICENSE.md
// =================================================================================================
package com.gazman.androidlifecycle.signal;

import android.util.Log;

import com.gazman.androidlifecycle.Factory;
import com.gazman.androidlifecycle.utils.UnhandledExceptionHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;

/**
 * Created by Gazman on 2/24/2015.
 */
public final class Signal<T> {

    public final T dispatcher;
    public final Class<T> originalType;
    private final Object synObject = new Object();
    private final LinkedList<T> listeners = new LinkedList<>();
    private final LinkedList<T> listenersTMP = new LinkedList<>();
    private final LinkedList<Class<? extends T>> classListeners = new LinkedList<>();
    private final LinkedList<Class<? extends T>> classListenersTMP = new LinkedList<>();
    private final LinkedList<T> oneTimeListeners = new LinkedList<>();
    private final LinkedList<Class<T>> oneTimeClassListeners = new LinkedList<>();

    Signal(Class<T> type) {
        originalType = type;
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                Signal.this.invoke(method, args);
                return null;
            }
        };
        //noinspection unchecked
        dispatcher = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, invocationHandler);
    }

    public void addListener(T listener) {
        synchronized (synObject) {
            listeners.add(listener);
        }
    }

    public void addListener(Class<? extends T> listener) {
        synchronized (synObject) {
            classListeners.add(listener);
        }
    }

    public void addListenerOnce(T listener){
        synchronized (synObject) {
            oneTimeListeners.add(listener);
        }
    }

    public void addListenerOnce(Class<T> listener){
        synchronized (synObject) {
            oneTimeClassListeners.add(listener);
        }
    }

    public void removeListener(T listener) {
        synchronized (synObject) {
            listeners.remove(listener);
            oneTimeListeners.remove(listener);
        }
    }

    public void removeListener(Class<? extends T> listener) {
        synchronized (synObject) {
            classListeners.remove(listener);
            //noinspection SuspiciousMethodCalls
            oneTimeClassListeners.remove(listener);
        }
    }

    void invoke(Method method, Object[] args) {
        Class<? extends T> classListener;
        T listener;

        if(listeners.size() > 0){
            while (listeners.size() > 0) {
                synchronized (synObject) {
                    listener = listeners.removeFirst();
                }
                listenersTMP.add(listener);
                invoke(method, args, listener);
            }
            synchronized (synObject){
                listeners.addAll(listenersTMP);
                listenersTMP.clear();
            }
        }

        while (oneTimeListeners.size() > 0) {
            synchronized (synObject) {
                listener = oneTimeListeners.removeFirst();
            }
            invoke(method, args, listener);
        }

        if(classListeners.size() > 0){
            while (classListeners.size() > 0) {
                synchronized (synObject) {
                    classListener = classListeners.removeFirst();
                }
                classListenersTMP.add(classListener);
                listener = Factory.inject(classListener);
                invoke(method, args, listener);
            }
            synchronized (synObject){
                classListeners.addAll(classListenersTMP);
                classListenersTMP.clear();
            }
        }

        while (oneTimeClassListeners.size() > 0) {
            synchronized (synObject) {
                classListener = oneTimeClassListeners.removeFirst();
            }
            listener = Factory.inject(classListener);
            invoke(method, args, listener);
        }
    }

    private void invoke(Method method, Object[] args, Object listener) {
        try {
            method.invoke(listener, args);
        } catch (Throwable e) {
            e.printStackTrace();
            if (UnhandledExceptionHandler.callback == null) {
                Log.e("LifeCycle", "Unhandled Exception", e);
                throw new Error("Unhandled Exception, consider providing UnhandledExceptionHandler.callback");
            } else {
                UnhandledExceptionHandler.callback.onApplicationError(e);
            }
        }
    }
}
