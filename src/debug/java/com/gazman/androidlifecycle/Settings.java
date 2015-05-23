package com.gazman.androidlifecycle;

import com.gazman.androidlifecycle.log.Logger;
import com.gazman.androidlifecycle.signal.SignalsHelper;

/**
 * Created by Ilya Gazman on 5/23/2015.
 */
public class Settings {

    public static boolean allowLogs = true;

    public static final SettingsInstance instance = new SettingsInstance();

}
