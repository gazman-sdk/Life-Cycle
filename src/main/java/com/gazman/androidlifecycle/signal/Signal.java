// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
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
 * Created by Ilya Gazman on 2/24/2015.
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
    private final LinkedList<Class<? extends T>> oneTimeClassListeners = new LinkedList<>();

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

    /**
     * Register for this signal, until removeListener is called.
     * @param listener listener to register
     */
    public void addListener(T listener) {
        applyListener(listeners, listener);
    }

    /**
     * Register for this signal, until removeListener is called.
     * The listener will be injected each time before its been dispatched to.
     * @param listener the listener class to inject when dispatch is happening
     */
    public void addListener(Class<? extends T> listener) {
        applyListener(classListeners, listener);
    }

    /**
     * Same as add listener, only it will immediately unregister
     * after the first dispatch.
     * Note that it does not matter how many method this listener got in the interface,
     * it will unregister after the first one to be dispatched
     * @param listener listener to register
     */
    public void addListenerOnce(T listener){
        applyListener(oneTimeListeners, listener);
    }

    /**
     * Same as add listener, only it will immediately unregister
     * after the first dispatch.
     * Note that it does not matter how many method this listener got in the interface,
     * it will unregister after the first one to be dispatched
     * @param listener the listener class to inject when dispatch is happening
     */
    public void addListenerOnce(Class<? extends T> listener){
        applyListener(oneTimeClassListeners, listener);
    }

    private <TYPE> void applyListener(LinkedList<TYPE> list, TYPE listener) {
        validateListener(listener);
        synchronized (synObject) {
            if (!list.contains(listener)) {
                list.add(listener);
            }
        }
    }

    private void validateListener(Object listener) {
        if(listener == null){
            throw new NullPointerException("Listener can't be null");
        }
    }

    /**
     * Remove listener that been added thru addListener or addListenerOnce
     * @param listener listener to unregister
     */
    public void removeListener(T listener) {
        validateListener(listener);
        synchronized (synObject) {
            listeners.remove(listener);
            oneTimeListeners.remove(listener);
        }
    }

    /**
     * Remove listener that been added thru addListener or addListenerOnce
     * @param listener listener to unregister
     */
    public void removeListener(Class<? extends T> listener) {
        validateListener(listener);
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
