package com.gazman.androidlifecycle.signal.invoker;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
public class ExecuteInvoker implements Invoker {

    private Executor executor;

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void invoke(Method method, Object[] args, Object listener) {
        executor.execute(new BaseInvoker(method, args, listener));
    }
}
