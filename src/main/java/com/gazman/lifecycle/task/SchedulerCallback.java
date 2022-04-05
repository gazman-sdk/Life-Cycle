package com.gazman.lifecycle.task;

import com.gazman.lifecycle.log.Logger;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ilya Gazman on 6/16/2015.
 */
public class SchedulerCallback {
    private final Logger logger = Logger.create("Scheduler");
    public Runnable callback;
    private AtomicInteger count;

    public void init(int count) {
        this.count = new AtomicInteger(count);
    }

    public InvocationHandler createHandler() {
        return (proxy, method, args) -> {
            if (count.decrementAndGet() <= 0) {
                callback.run();
            }
            logger.d("Completed", method.getName(), count);
            return null;
        };
    }

    public void addOne() {
        count.incrementAndGet();
    }
}
