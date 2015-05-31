// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle.signal;

import com.gazman.androidlifecycle.task.Scheduler;

/**
 * Created by Ilya Gazman on 5/18/2015.
 */
public interface DisposableSignal {
    void onDispose(Scheduler scheduler);
}