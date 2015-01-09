// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/android_life_cycle/blob/master/LICENSE.md
// =================================================================================================
package com.gazman_sdk.androidlifecycle;

import com.gazman_sdk.androidlifecycle.utils.UnhandledExceptionHandler;

import java.lang.reflect.Constructor;
import java.util.HashMap;

class ClassConstructor {
	
	private static HashMap<Class<?>, HashMap<String, Object>> singletons = new HashMap<Class<?>, HashMap<String,Object>>();
	
	@SuppressWarnings("unchecked")
	public static <T> T constructSingleTon(String family, Class<?> classToUse, Object... params) {
		HashMap<String,Object> map = singletons.get(classToUse);
		if(map == null){
			map = new HashMap<String, Object>();
			singletons.put(classToUse, map);
		}
		Object instance = map.get(family);
		if(instance == null){
			if(params.length > 0){
				instance = construct(classToUse, params);
			}
			else{
				instance = construct(classToUse);
			}
			map.put(family, instance);
			if (instance instanceof Injector) {
				((Injector) instance).injectionHandler(family);
			}
		}
		return (T) instance;
	}

	@SuppressWarnings("unchecked")
	public static <T> T construct(Class<?> classToUse) {
		try {
			return (T) classToUse.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new IllegalStateException("Injection failed");
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T construct(Class<?> classToUse, Object... params) {
		Constructor<?>[] constructors = classToUse.getConstructors();
		MAIN_LOOP: for (Constructor<?> constructor : constructors) {
			try {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				if (parameterTypes.length != params.length) {
					continue;
				}
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    if(!parameterType.isAssignableFrom(params[i].getClass())){
                        continue MAIN_LOOP;
                    }
                }
				return (T) constructor.newInstance(params);
				
			} catch (Throwable throwable) {
                if(UnhandledExceptionHandler.callback != null){
                    UnhandledExceptionHandler.callback.onApplicationError(throwable);
                }
                else {
                    throw new Error("Injection failed", throwable);
                }
			}
		}
        Error error = new Error("Constructor not found");
        if(UnhandledExceptionHandler.callback != null){
            UnhandledExceptionHandler.callback.onApplicationError(error);
        }
        else {
            throw error;
        }

        return null;
    }
}
