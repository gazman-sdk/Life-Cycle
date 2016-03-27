package com.gazman.androidlifecycle.signal.invoker;

import java.lang.reflect.Method;

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
public interface Invoker {

    void invoke(Method method, Object[] args, Object listener);
}
