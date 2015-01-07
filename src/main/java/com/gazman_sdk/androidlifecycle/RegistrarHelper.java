package com.gazman_sdk.androidlifecycle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 29-Nov-14.
 */
public class RegistrarHelper {

    private ArrayList<ArrayList<Runnable>> removeCommands = new ArrayList<ArrayList<Runnable>>();
    private HashMap<Signal, ArrayList<Runnable>> map = new HashMap<Signal, ArrayList<Runnable>>();

    public <T> void registerSignal(Class<? extends ISignal<T>> signalClass,
                         Class<? extends T> handlerClass){
        final T handler = Factory.inject(handlerClass);
        registerSignal(signalClass, handler);
    }

    public <T> void registerSignal(Class<? extends ISignal<T>> signalClass,
                                   final T handler){
        final ISignal<T> signal = Factory.inject(signalClass);
        registerSignal(signal, handler);
    }

    public <T> void registerSignal(Signal<T> signal,
                                   Class<? extends T> handlerClass){
        final T handler = Factory.inject(handlerClass);
        registerSignal(signal, handler);
    }

    public <T> void registerSignal(final ISignal<T> signal,
                                   final T handler){
        signal.addListener(handler);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                signal.removeListener(handler);
            }
        };
        ArrayList<Runnable> runnables = map.get(signal);
        if(runnables == null){
            runnables = new ArrayList<Runnable>();
            map.put((Signal) signal, runnables);
            removeCommands.add(runnables);
        }
        runnables.add(runnable);
    }

    public void unregister(Signal signal){
        ArrayList<Runnable> runnables = map.get(signal);
        if(runnables == null){
            return;
        }
        for (int i = 0; i < runnables.size(); i++) {
            runnables.get(i).run();
        }
        runnables.clear();
    }

    public void unregisterAll(){
        for (int i = 0; i < removeCommands.size(); i++) {
            ArrayList<Runnable> runnables = removeCommands.get(i);
            for (int j = 0; j < runnables.size(); j++) {
                runnables.get(j).run();
            }
        }
        removeCommands.clear();
        map.clear();
    }
}
