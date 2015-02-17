// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  https://github.com/Ilya-Gazman/android_life_cycle/blob/master/LICENSE.md
// =================================================================================================
package com.gazman_sdk.androidlifecycle.helper;

import com.gazman_sdk.androidlifecycle.Signal;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 29-Nov-14.
 */
public class RegistrarHelper {

    private ArrayList<RegisterBundle> registerBundles = new ArrayList<>();

    public <T> void registerSignal(RegisterBundle<T> registerBundle){
        registerBundle.register();
        registerBundles.add(registerBundle);
    }

    public <T> void unregister(RegisterBundle<T> registerBundle){
        if(registerBundle != null){
            registerBundle.unregister();
            registerBundles.remove(registerBundle);
        }
    }

    public void unregisterAll(){
        for (RegisterBundle registerBundle : registerBundles) {
            registerBundle.unregister();
        }
        registerBundles.clear();
    }
}
