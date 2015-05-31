// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle.signals;

import com.gazman.androidlifecycle.task.Scheduler;

/**
 * Created by Ilya Gazman on 3/2/2015.
 */
public interface BootstrapTimeSignal {

    void onBootstrap(Scheduler scheduler);
}
