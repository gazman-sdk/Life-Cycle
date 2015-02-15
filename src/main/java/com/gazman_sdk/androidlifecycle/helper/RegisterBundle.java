package com.gazman_sdk.androidlifecycle.helper;

import com.gazman_sdk.androidlifecycle.Factory;
import com.gazman_sdk.androidlifecycle.ISignal;
import com.gazman_sdk.androidlifecycle.Registrar;

/**
 * Created by Gazman on 2/14/2015.
 */
public class RegisterBundle<T> {

    private Class<? extends ISignal<T>> signalClass;
    private ISignal<T> signalInstance;
    private T handler;
    private String family;

    protected RegisterBundle(){}

    public static <T> RegisterBundle<T> build(Class<T> type){
        return new RegisterBundle<>();
    }

    public RegisterBundle setHandler(T handler) {
        this.handler = handler;
        return this;
    }

    public RegisterBundle setSignalClass(Class<? extends ISignal<T>> signalClass) {
        this.signalClass = signalClass;
        return this;
    }

    public RegisterBundle setSignalInstance(ISignal<T> signalInstance) {
        this.signalInstance = signalInstance;
        return this;
    }

    public RegisterBundle setFamily(String family) {
        this.family = family;
        return this;
    }

    protected void register() {
        init();
        signalInstance.addListener(handler);
    }

    protected void unregister() {
        init();
        signalInstance.removeListener(handler);
    }

    private void init() {
        if (handler == null) {
            throw new NullPointerException("Handler wasn't set");
        }

        if (signalInstance == null && signalClass == null) {
            throw new NullPointerException("Signal wasn't set");
        }

        if (signalInstance == null) {
            signalInstance = Factory.inject(signalClass, family != null ? family : Registrar.DEFAULT_FAMILY);
        }
    }
}
