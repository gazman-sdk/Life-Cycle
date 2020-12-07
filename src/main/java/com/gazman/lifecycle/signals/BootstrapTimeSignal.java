package com.gazman.lifecycle.signals;

import com.gazman.lifecycle.task.Scheduler;

/**
 * Created by Ilya Gazman on 3/2/2015.
 */
public interface BootstrapTimeSignal {

    void onBootstrap(Scheduler scheduler);
}
