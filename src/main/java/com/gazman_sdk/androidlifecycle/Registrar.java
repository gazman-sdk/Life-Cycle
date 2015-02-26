// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/android_life_cycle/blob/master/LICENSE.md
// =================================================================================================
package com.gazman_sdk.androidlifecycle;

import com.gazman_sdk.androidlifecycle.signals.Signal;
import com.gazman_sdk.androidlifecycle.signals.SignalsBag;

import java.util.HashMap;
import java.util.LinkedList;


public abstract class Registrar {
    static HashMap<Class<?>, HashMap<String, Class<?>>> classesMap = new HashMap<Class<?>, HashMap<String, Class<?>>>();
    // GUIDE value
    public static final String DEFAULT_FAMILY = "de9502c7-a41a-4fdb-9d42-249b94fbeaa9";

    private static LinkedList<Registrar> registrars = new LinkedList<Registrar>();
    private static boolean initializationComplete = false;
    private static Object synObject = new Object();

    /**
     * Register for initializationCompleteSignal. If the initialization already complete the signal will be callback will be instantly executed.
     * <br>This process is thread safety.
     *
     * @param callback InitializationCompleteSignal callback
     */
    public static <T extends RegistrationCompleteSignal> void registerForInitializationComplete(
            T callback) {
        synchronized (synObject) {
            if (initializationComplete) {
                callback.registrationCompleteHandler();
            } else {
                Signal<RegistrationCompleteSignal> signal = SignalsBag.inject(RegistrationCompleteSignal.class);
                signal.addListenerOnce(callback);
            }
        }
    }

    /**
     * The entry point to the initialization process
     */
    void initialize() {
        synchronized (synObject) {
            if (initializationComplete) {
                throw new IllegalStateException(
                        "Initialization process has already been executed.");
            }
            initializationComplete = true;
            initRegistrars();
            for (Registrar registrar : registrars) {
                registrar.initClasses();
            }
            initClasses();
            for (Registrar registrar : registrars) {
                registrar.initSignals();
            }
            initSignals();
            registrars = null;
        }
        Signal<RegistrationCompleteSignal> signal = SignalsBag.inject(RegistrationCompleteSignal.class);
        signal.dispatcher.registrationCompleteHandler();
    }

    /**
     * Will call {@link #registerClass(Class, Class, String)} with <br>
     * registerClass(claz, Object.class, DEFAULT_FAMILY)
     *
     * @param claz the class tree to register
     */
    protected void registerClass(Class<?> claz) {
        registerClass(claz, Object.class, DEFAULT_FAMILY);
    }

    /**
     * Will call {@link #registerClass(Class, Class, String)} with <br>
     * registerClass(claz, Object.class, family)
     *
     * @param claz the class tree to register
     */
    protected void registerClass(Class<?> claz, String family) {
        registerClass(claz, Object.class, family);
    }

    /**
     * Will call {@link #registerClass(Class, Class, String)} with <br>
     * registerClass(claz, topClaz, family)
     *
     * @param claz the class tree to register
     */
    protected <T, X extends T> void registerClass(Class<X> claz,
                                                  Class<T> topClaz) {
        registerClass(claz, topClaz, DEFAULT_FAMILY);
    }

    /**
     * When any class from @claz super family up to @topClaz will be injected @claz
     * will be created.
     *
     * @param claz    claz the class tree to register
     * @param topClaz
     * @param family
     */
    protected <T, X extends T> void registerClass(Class<X> claz,
                                                  Class<T> topClaz, String family) {
        if (family == null) {
            family = DEFAULT_FAMILY;
        }
        @SuppressWarnings("unchecked")
        Class<? super T> superclass = (Class<? super T>) claz;
        while (superclass != topClaz) {
            HashMap<String, Class<?>> hashMap = classesMap.get(superclass);
            if (hashMap == null) {
                hashMap = new HashMap<String, Class<?>>();
                classesMap.put(superclass, hashMap);
            }
            hashMap.put(family, claz);
            // next
            superclass = superclass.getSuperclass();
        }

    }

    protected void addRegistrar(Registrar registrar) {
        registrars.add(registrar);
        registrar.initRegistrars();
    }

    /**
     * A place to make all your {@link #registerClass(Class)} calls.
     */
    protected abstract void initClasses();

    protected abstract void initSignals();

    /**
     * A place to make all your {@link #addRegistrar(Registrar)} calls. <br>
     * *Note that order it very important - If you perform class overriding you
     * would prefer to add this Registrar last.
     */
    protected abstract void initRegistrars();

}
