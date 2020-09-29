package com.gazman.androidlifecycle.signal.invoker;

import com.gazman.androidlifecycle.utils.UnhandledExceptionHandler;

import java.lang.reflect.Method;

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
public class BaseInvoker implements Runnable {
    public Method method;
    public Object[] args;
    public Object listener;

    public BaseInvoker() {
    }

    public BaseInvoker(Method method, Object[] args, Object listener) {
        this.method = method;
        this.args = args;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(listener, args);
        } catch (Throwable e) {
            e.printStackTrace();
            if (UnhandledExceptionHandler.callback == null) {
                System.err.println("Unhandled Exception, consider providing UnhandledExceptionHandler.callback");
                try {
                    Thread.sleep(10);
                    android.os.Process.killProcess(android.os.Process.myPid());
                } catch (InterruptedException ignore) {
                }
            } else {
                UnhandledExceptionHandler.callback.onApplicationError(e);
            }
        }
    }
}
