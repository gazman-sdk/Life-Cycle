// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle;

public interface Injector {
    /**
     * Will be called right after the constructor
     *
     * @param family The family this instance been injected in to
     */
    void injectionHandler(String family);
}
