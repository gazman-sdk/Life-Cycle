package com.gazman.androidlifecycle.signal;

import com.gazman.androidlifecycle.task.Scheduler;

/**
 * Created by Ilya Gazman on 5/18/2015.
 */
public interface Disposable {
    void onDispose(Scheduler scheduler);
}
