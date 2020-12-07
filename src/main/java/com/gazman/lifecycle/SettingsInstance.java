package com.gazman.lifecycle;

import com.gazman.lifecycle.log.Logger;
import com.gazman.lifecycle.signal.SignalsHelper;

/**
 * Created by Ilya Gazman on 5/23/2015.
 */
public final class SettingsInstance {
    SettingsInstance() {
    }

    private final Registrar registrar = new Registrar() {

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
