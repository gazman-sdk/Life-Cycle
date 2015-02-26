package com.gazman_sdk.androidlifecycle.signals;

import android.util.Log;

import com.gazman_sdk.androidlifecycle.Factory;
import com.gazman_sdk.androidlifecycle.utils.UnhandledExceptionHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;

/**
 * Created by Gazman on 2/24/2015.
 */
public final class Signal<T> {

    public final T dispatcher;
    LinkedList<T> listeners = new LinkedList<>();
    LinkedList<Class<T>> classListeners = new LinkedList<>();
    LinkedList<T> oneTimeListeners = new LinkedList<>();
    LinkedList<Class<T>> oneTimeClassListeners = new LinkedList<>();

    Signal(Class<T> type) {
        dispatcher = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, invocationHandler);
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void addListener(Class<T> listener) {
        classListeners.add(listener);
    }

    public void addListenerOnce(T listener){
        oneTimeListeners.add(listener);
    }

    public void addListenerOnce(Class<T> listener){
        oneTimeClassListeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
        oneTimeListeners.remove(listener);
    }

    public void removeListener(Class<T> listener) {
        classListeners.remove(listener);
        oneTimeClassListeners.remove(listener);
    }

    private InvocationHandler invocationHandler = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            for (Object listener : listeners) {
                invoke(method, args, listener);
            }
            for (Object listener : oneTimeListeners) {
                invoke(method, args, listener);
            }
            for (Class<T> classListener : classListeners) {
                T listener = Factory.inject(classListener);
                invoke(method, args, listener);
            }
            for (Class<T> classListener : oneTimeClassListeners) {
                T listener = Factory.inject(classListener);
                invoke(method, args, listener);
            }

            oneTimeListeners.clear();
            oneTimeClassListeners.clear();

            return null;
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
    };
}
