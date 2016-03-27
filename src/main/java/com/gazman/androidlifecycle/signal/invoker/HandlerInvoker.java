package com.gazman.androidlifecycle.signal.invoker;

import android.os.Handler;

import java.lang.reflect.Method;

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
public class HandlerInvoker extends DefaultInvoker {
    private Handler handler;
    private boolean runInstantlyIfOnHandlerThread = true;

    public void setRunInstantlyIfOnHandlerThread(boolean runInstantlyIfOnHandlerThread) {
        this.runInstantlyIfOnHandlerThread = runInstantlyIfOnHandlerThread;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void invoke(Method method, Object[] args, Object listener) {
        if(runInstantlyIfOnHandlerThread){
            if(Thread.currentThread() == handler.getLooper().getThread()){
                super.invoke(method, args, listener);
                return;
            }
        }
        handler.post(new BaseInvoker(method, args, listener));
    }
}
