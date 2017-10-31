// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle;

/**
 * Created by Ilya Gazman on 3/18/2015.
 */
public interface Builder<T> {

    /**
     * Construct new instance of the class using optional params
     *
     * @param classToInject
     * @param params        will not be null
     * @return new instance of the class
     */
    T build(Class<T> classToInject, Object[] params);
}
