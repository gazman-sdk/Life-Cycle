package com.gazman.androidlifecycle.task;

import com.gazman.androidlifecycle.Factory;
import com.gazman.androidlifecycle.log.Logger;
import com.gazman.androidlifecycle.signal.Signal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ilya Gazman on 6/16/2015.
 */
public class SchedulerCallback {
    private AtomicInteger count;
    private Logger logger = Factory.injectWithParams(Logger.class, "Scheduler");
    public Runnable callback;

    public void init(int count){
        this.count = new AtomicInteger(count);
    }

    public InvocationHandler createHandler(){
        return new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (count.decrementAndGet() <= 0) {
                    callback.run();
                }
                logger.log("Completed", method.getName(), count);
                return null;
            }
        };
    }

    public void addOne(){
        count.incrementAndGet();
    }
}
