package com.gazman_sdk.androidlifecycle;

import android.app.Application;

/**
 * Created by user on 04-Dec-14.
 */
public abstract class LifeCycleApplication extends Application{
    private static LifeCycleApplication instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getRegistrar().initialize();
            }
        }, "Registration Thread").start();
    }

    protected abstract Registrar getRegistrar();

    public static LifeCycleApplication getInstance() {
        return instance;
    }
}
