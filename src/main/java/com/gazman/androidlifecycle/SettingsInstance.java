// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle;

import com.gazman.androidlifecycle.log.Logger;
import com.gazman.androidlifecycle.signal.SignalsHelper;

/**
 * Created by Ilya Gazman on 5/23/2015.
 */
public final class SettingsInstance {
    SettingsInstance() {
    }

    private Registrar registrar = new Registrar() {

        @Override
        protected void initClasses() {

        }

        @Override
        protected void initSignals(SignalsHelper signalsHelper) {

        }

        @Override
        protected void initRegistrars() {

        }
    };

    /**
     * Since Logger is used in LifeCycle the only way to override it
     * in life cycle as well, is using this method
     *
     * @param loggerClass the new logger to use
     */
    public void setLogger(Class<? extends Logger> loggerClass) {
        registrar.registerClass(loggerClass);
    }
}
