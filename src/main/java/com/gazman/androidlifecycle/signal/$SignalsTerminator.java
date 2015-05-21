package com.gazman.androidlifecycle.signal;

/**
 * Created by Ilya Gazman on 5/18/2015.
 */
public class $SignalsTerminator {

    public static void exit(){
        SignalsBag.map.clear();
    }
}
