// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/gazman-sdk/blob/master/LICENSE.md
// =================================================================================================
package com.gazman_sdk.androidlifecycle;

import java.util.HashMap;

public class Factory {

	/**
	 * Retrieve instance of claz class
	 * 
	 * @param claz
	 *            the class to retrieve
	 * @return instance of claz class
	 */
	public static <T> T inject(Class<T> claz) {
		return inject(claz, Registrar.DEFAULT_FAMILY);
	}

	/**
	 * Retrieve instance of claz class using MultiTon pattern with family as a
	 * key
	 * 
	 * @param claz
	 *            the class to retrieve
	 * @return instance of claz class
	 * @see <a href="http://en.wikipedia.org/wiki/Multiton_pattern">Multiton
	 *      pattern on wiki</a>
	 */
	public static <T> T inject(Class<T> claz, String family) {
		HashMap<String, Class<?>> hashMap = Registrar.classesMap.get(claz);
		Class<?> classToUse = hashMap != null ? hashMap.get(family) : claz;
		T instance;
		if (SingleTon.class.isAssignableFrom(classToUse)) {
			instance = ClassConstructor.constructSingleTon(family, classToUse);
		}
		else{
			instance = ClassConstructor.construct(classToUse);
			if (instance instanceof Injector) {
				((Injector) instance).injectionHandler(family);
			}
		}
		
		return instance;
	}

	/**
	 * Retrieve instance of claz class <br><br>
	 * *If you have more than one constructor it may result in slow
	 * performance as it will handle inside exceptions<br>
	 * *In case your constructor is a string call {@link #injectWithParams(Class, String, Object...) injectWithParams(claz, null, [String])}
	 * 
	 * @param claz
	 *            the class to retrieve
	 * @param params
	 *            contractor parameters
	 * @return instance of claz class
	 */
	public static <T> T injectWithParams(Class<T> claz, Object... params) {
		return injectWithParamsAndFamily(claz, Registrar.DEFAULT_FAMILY, params);
	}

	/**
	 * Retrieve instance of claz class <br><br>
	 * 
	 * *If you have more than one constructor it may result in slow
	 * performance as it will handle inside exceptions
	 * 
	 * @param claz
	 *            the class to inject
	 * @param family
	 *            class family for Multiton pattern
	 * @param params
	 *            the constructor parameters
	 * @return instance of claz class
	 * @see <a href="http://en.wikipedia.org/wiki/Multiton_pattern">Multiton
	 *      pattern on wiki</a>
	 */

	public static <T> T injectWithParamsAndFamily(Class<T> claz, String family,
			Object... params) {
		if(family == null){
			family = Registrar.DEFAULT_FAMILY;
		}
		HashMap<String, Class<?>> hashMap = Registrar.classesMap.get(claz);
		Class<?> classToUse = hashMap != null ? hashMap.get(family) : claz;
		
		T instance;
		if (SingleTon.class.isAssignableFrom(classToUse)) {
			instance = ClassConstructor.constructSingleTon(family, classToUse, params);
		}
		else{
			instance = ClassConstructor.construct(classToUse, params);
			if (instance instanceof Injector) {
				((Injector) instance).injectionHandler(family);
			}
		}
		
		return instance;
	}

}
