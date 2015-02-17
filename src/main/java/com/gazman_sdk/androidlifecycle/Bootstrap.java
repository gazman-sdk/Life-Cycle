package com.gazman_sdk.androidlifecycle;

/**
 * Created by Gazman on 2/17/2015.
 */
public class Bootstrap {

    public Registrar registrar;

    public Bootstrap(Registrar registrar){
        this.registrar = registrar;
    }

    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                registrar.initialize();
            }
        }, "Registration Thread").start();
    }
}
