// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/gazman-sdk/blob/master/LICENSE.md
// =================================================================================================
package com.gazman_sdk.androidlifecycle;

import android.util.Log;

import com.gazman_sdk.androidlifecycle.utils.UnhandledExceptionHandler;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;


public abstract class Signal<T> implements SingleTon, ISignal<T> {
	private Method method;
	private LinkedList<T> listeners = new LinkedList<T>();
	private final Object synObject = new Object();
    private int maximumListeners;

    protected Signal(int maximumListeners){
        this();
        this.maximumListeners = maximumListeners;
    }

	protected Signal() {
		Class<?>[] interfaces = getClass().getInterfaces();
		if (interfaces.length != 1) {
			throw new IllegalArgumentException(
					this.getClass()
							+ "Must implement exactly two interfaces, not more and not less. curently implementing "
							+ interfaces.length);
		}
		Method[] methods = interfaces[0].getMethods();
		if (methods.length != 1) {
			throw new IllegalArgumentException(interfaces[0]
					+ "Must have exactly one method, not more and not less");
		}
		method = methods[0];
	}

	protected void dispatch(Object... arguments) {
		Iterator<T> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			T listener;
			synchronized (synObject) {
				if (iterator.hasNext()) {
					listener = iterator.next();
				} else {
					listener = null;
				}
			}
			if (listener != null) {
				invoke(listener, arguments);
			}
		}
	}

	private void invoke(T listener, Object... arguments) {
		if (listener == null) {
			throw new NullPointerException("Listener can't be null");
		}
		try {
			method.invoke(listener, arguments);
		} catch (Throwable e){
            e.printStackTrace();
            if(UnhandledExceptionHandler.callback == null){
                Log.e("LifeCycle", "Unhandled Exception", e);
                throw new Error("Unhandled Exception, consider providing UnhandledExceptionHandler.callback");
            }
            else{
                UnhandledExceptionHandler.callback.onApplicationError(e);
            }
        }
    }

	public final void addListener(T listener) {
		synchronized (synObject) {
            if(maximumListeners > 0 && maximumListeners == listeners.size()){
                listeners.removeFirst();
            }
            listeners.remove(listener);
            listeners.add(listener);
		}
	}

	public final void removeListener(T listener) {
		synchronized (synObject) {
			listeners.remove(listener);
		}
	}
}
