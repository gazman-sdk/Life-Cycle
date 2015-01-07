// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/gazman-sdk/blob/master/LICENSE.md
// =================================================================================================
package com.gazman_sdk.androidlifecycle;

import java.util.Iterator;
import java.util.LinkedList;


public abstract class BaseSignal<T> implements SingleTon, ISignal<T> {
	private LinkedList<T> listeners = new LinkedList<T>();
	private final Object synObject = new Object();
    private int maximumListeners;

    protected interface DispatchCallback<T>{
        void dispatch(T listener);
    }

    protected BaseSignal(int maximumListeners){
        this.maximumListeners = maximumListeners;
    }

	protected BaseSignal() {

	}

	protected void dispatch(DispatchCallback<T> callback) {
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
				callback.dispatch(listener);
			}
		}
	}

    @Override
	public final void addListener(T listener) {
		synchronized (synObject) {
            if(maximumListeners > 0 && maximumListeners == listeners.size()){
                listeners.removeFirst();
            }
            listeners.remove(listener);
            listeners.add(listener);
		}
	}

    @Override
	public final void removeListener(T listener) {
		synchronized (synObject) {
			listeners.remove(listener);
		}
	}
}
