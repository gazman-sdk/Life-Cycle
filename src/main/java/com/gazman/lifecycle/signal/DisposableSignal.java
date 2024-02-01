package com.gazman.lifecycle.signal;

import com.gazman.lifecycle.task.Scheduler;

/**
 * Created by Ilya Gazman on 5/18/2015.
 */
public interface DisposableSignal {
    void onDispose(Scheduler scheduler);
}
