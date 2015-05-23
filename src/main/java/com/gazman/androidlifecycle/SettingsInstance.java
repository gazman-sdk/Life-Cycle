package com.gazman.androidlifecycle;

import com.gazman.androidlifecycle.log.Logger;
import com.gazman.androidlifecycle.signal.SignalsHelper;

/**
 * Created by Ilya Gazman on 5/23/2015.
 */
public final class SettingsInstance {
    SettingsInstance(){}

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
    public void setLogger(Class<? extends Logger> loggerClass){
        registrar.registerClass(loggerClass);
    }
}
