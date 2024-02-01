package com.gazman.lifecycle.signal.invoker;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Ilya Gazman on 3/18/2016.
 */
public class MainHandlerInvoker extends HandlerInvoker {

    {
        setHandler(new Handler(Looper.getMainLooper()));
    }
}
