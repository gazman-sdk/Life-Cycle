// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle;

import com.gazman.androidlifecycle.utils.UnhandledExceptionHandler;

import java.lang.reflect.Constructor;
import java.util.HashMap;

class ClassConstructor {

    private static final Object[] NO_PARAMS = new Object[0];
    static HashMap<Class<?>, HashMap<String, Object>> singletons = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T constructSingleTon(String family, Class<?> classToUse, Object... params) {
        HashMap<String, Object> map = singletons.get(classToUse);
        if (map == null) {
            map = new HashMap<>();
            singletons.put(classToUse, map);
        }
        Object instance = map.get(family);
        if (instance == null) {
            if (params.length > 0) {
                instance = construct(classToUse, params);
            } else {
                instance = build(classToUse);
            }
            map.put(family, instance);
            if (instance instanceof Injector) {
                ((Injector) instance).injectionHandler(family);
            }
        }
        return (T) instance;
    }

    @SuppressWarnings("unchecked")
    public static <T> T build(Class<T> classToUse) {
        try {
            Builder<T> builder = Registrar.buildersMap.get(classToUse);
            if (builder != null) {
                return builder.build(classToUse, NO_PARAMS);
            }
            return classToUse.newInstance();
        } catch (Throwable throwable) {
            if (UnhandledExceptionHandler.callback != null) {
                UnhandledExceptionHandler.callback.onApplicationError(throwable);
            } else {
                throw new Error("Injection failed", throwable);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T construct(Class<T> classToUse, Object... params) {
        try {
            Builder<T> builder = Registrar.buildersMap.get(classToUse);
            if (builder != null) {
                return builder.build(classToUse, params);
            }
            Constructor<?>[] constructors = classToUse.getConstructors();
            MAIN_LOOP:
            for (Constructor<?> constructor : constructors) {

                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length != params.length) {
                    continue;
                }
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    if (!parameterType.isAssignableFrom(params[i].getClass())) {
                        continue MAIN_LOOP;
                    }
                }
                return (T) constructor.newInstance(params);

            }
        } catch (Throwable throwable) {
            if (UnhandledExceptionHandler.callback != null) {
                UnhandledExceptionHandler.callback.onApplicationError(throwable);
            } else {
                throw new Error("Injection failed", throwable);
            }
        }
        Error error = new Error("Constructor not found");
        if (UnhandledExceptionHandler.callback != null) {
            UnhandledExceptionHandler.callback.onApplicationError(error);
        } else {
            throw error;
        }

        return null;
    }
}
