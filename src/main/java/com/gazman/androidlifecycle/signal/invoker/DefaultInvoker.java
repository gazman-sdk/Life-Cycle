package com.gazman.androidlifecycle.signal.invoker;

import java.lang.reflect.Method;

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
public class DefaultInvoker implements Invoker {
    protected BaseInvoker baseInvoker = new BaseInvoker();

    @Override
    public void invoke(Method method, Object[] args, Object listener) {
        baseInvoker.method = method;
        baseInvoker.args = args;
        baseInvoker.listener = listener;
        baseInvoker.run();
    }
}
