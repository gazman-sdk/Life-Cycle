// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/gazman-sdk/blob/master/LICENSE.md
// =================================================================================================
package com.gazman_sdk.androidlifecycle.signal;


import com.gazman_sdk.androidlifecycle.Signal;

/**
 * Fires after the main registration process of the application is complete. It will fire on the registration process thread.
 * 
 */
public class RegistrationCompleteSignal extends
        Signal<IRegistrationCompleteSignal> implements
		IRegistrationCompleteSignal {

	@Override
	public void registrationCompleteHandler() {
		dispatch();
	}
}
