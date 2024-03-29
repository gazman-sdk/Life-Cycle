package com.gazman.lifecycle.signal;

import com.gazman.lifecycle.Factory;
import com.gazman.lifecycle.signal.invoker.DefaultInvoker;
import com.gazman.lifecycle.signal.invoker.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ilya Gazman on 2/24/2015.
 */
public final class Signal<T> {

    private static final Invoker DEFAULT_INVOKER = new DefaultInvoker();
    public final T dispatcher;
    public final Class<T> originalType;
    private final Object synObject = new Object();
    private final ArrayList<T> listeners = new ArrayList<>();
    private final ArrayList<Class<? extends T>> classListeners = new ArrayList<>();
    private final LinkedList<Class<? extends T>> classListenersTMP = new LinkedList<>();
    private final LinkedList<T> oneTimeListeners = new LinkedList<>();
    private final LinkedList<Class<? extends T>> oneTimeClassListeners = new LinkedList<>();
    private boolean hasListeners;
    private Invoker invoker = DEFAULT_INVOKER;

    Signal(Class<T> type) {
        originalType = type;
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            Signal.this.invoke(method, args);
            return null;
        };
        //noinspection unchecked
        dispatcher = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, invocationHandler);
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    /**
     * Register for this signal, until removeListener is called.
     *
     * @param listener listener to register
     */
    public void addListener(T listener) {
        applyListener(listeners, listener);
    }

    /**
     * Register for this signal, until removeListener is called.
     * The listener will be injected each time before its been dispatched to.
     *
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
     *
     * @param listener listener to register
     */
    public void addListenerOnce(T listener) {
        applyListener(oneTimeListeners, listener);
    }

    /**
     * Same as add listener, only it will immediately unregister
     * after the first dispatch.
     * Note that it does not matter how many method this listener got in the interface,
     * it will unregister after the first one to be dispatched
     *
     * @param listener the listener class to inject when dispatch is happening
     */
    public void addListenerOnce(Class<? extends T> listener) {
        applyListener(oneTimeClassListeners, listener);
    }

    private <TYPE> void applyListener(List<TYPE> list, TYPE listener) {
        validateListener(listener);
        synchronized (synObject) {
            if (!list.contains(listener)) {
                hasListeners = true;
                list.add(listener);
            }
        }
    }

    private void validateListener(Object listener) {
        if (listener == null) {
            throw new NullPointerException("Listener can't be null");
        }
    }

    /**
     * Remove listener that been added through addListener or addListenerOnce
     *
     * @param listener listener to unregister
     */
    public void removeListener(T listener) {
        validateListener(listener);
        synchronized (synObject) {
            listeners.remove(listener);
            oneTimeListeners.remove(listener);
            updateHasListeners();
        }
    }

    /**
     * Remove listener that been added thru addListener or addListenerOnce
     *
     * @param listener listener to unregister
     */
    public void removeListener(Class<? extends T> listener) {
        validateListener(listener);
        synchronized (synObject) {
            classListeners.remove(listener);
            classListenersTMP.remove(listener);
            oneTimeClassListeners.remove(listener);
            updateHasListeners();
        }
    }

    private void updateHasListeners() {
        synchronized (synObject) {
            hasListeners = 0 < classListeners.size() + oneTimeClassListeners.size() + listeners.size() + oneTimeListeners.size();
        }
    }

    void invoke(Method method, Object[] args) {
        Class<? extends T> classListener;
        T listener;

        if (listeners.size() > 0) {
            int i = 0;
            while (true) {
                synchronized (synObject) {
                    if (i < listeners.size()) {
                        listener = listeners.get(i);
                    } else {
                        break;
                    }
                }
                invoker.invoke(method, args, listener);
                i++;
            }
        }

        while (oneTimeListeners.size() > 0) {
            synchronized (synObject) {
                listener = oneTimeListeners.removeFirst();
            }
            invoker.invoke(method, args, listener);
        }

        if (classListeners.size() > 0) {
            int i = 0;
            while (true) {
                synchronized (synObject) {
                    if (i < classListeners.size()) {
                        classListener = classListeners.get(i);
                    } else {
                        break;
                    }
                }
                listener = Factory.inject(classListener);
                invoker.invoke(method, args, listener);
                i++;
            }
        }

        while (oneTimeClassListeners.size() > 0) {
            synchronized (synObject) {
                classListener = oneTimeClassListeners.removeFirst();
            }
            listener = Factory.inject(classListener);
            invoker.invoke(method, args, listener);
        }
        updateHasListeners();
    }


    public boolean hasListeners() {
        return hasListeners;
    }

    private static class NoStackTraceRunTimeException extends RuntimeException {
        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}
