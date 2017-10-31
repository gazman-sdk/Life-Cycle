package com.gazman.androidlifecycle.signal.invoker;

import android.util.Log;

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
            method.invoke(listener, args);
        } catch (Throwable e) {
            if (UnhandledExceptionHandler.callback == null) {
                Throwable cause = e.getCause();
                if (cause == null) {
                    cause = e;
                }
                Log.e("LifeCycle", "Unhandled Exception, consider providing UnhandledExceptionHandler.callback", cause);
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
