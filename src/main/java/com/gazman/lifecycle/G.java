package com.gazman.lifecycle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A ContextWrapper for application context
 */
public class G extends ContextWrapper {

    @SuppressLint("StaticFieldLeak")
    public static final Context app = new G();
    public static final Handler main = new Handler(Looper.getMainLooper());
    public static ErrorLogger errorLogger;

    public interface ErrorLogger{
        void onError(Exception e);
    }

    /**
     * Single thread executor
     */
    public static final ExecutorService IO = Executors.newSingleThreadExecutor();
    /**
     * Cached Thread Pool
     */
    private static final ExecutorService CE = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable){
        CE.execute(runnable);
    }

    public static void executeOnCESafely(Runnable runnable) {
        CE.execute(() -> {
            try{
                runnable.run();
            }
            catch (Exception e){
                if(errorLogger != null){
                    errorLogger.onError(e);
                }
            }
        });
    }

    public static void executeOnMainSafely(Runnable runnable){
        main.post(() -> {
            try{
                runnable.run();
            }
            catch (Exception e){
                if(errorLogger != null){
                    errorLogger.onError(e);
                }
            }
        });
    }

    public static final int version = Build.VERSION.SDK_INT;
    private static boolean initialized = false;

    private G() {
        super(null);
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceUniqueIDForFraudUsage() {
        try {
            return Settings.Secure.getString(app.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ignore) {
        }
        return null;
    }

    public static String getPlayStoreLink() {
        return "https://market.android.com/details?id=" + G.app.getPackageName();
    }

    public static void init(Context context) {
        if (initialized) {
            return;
        }
        initialized = true;
        ((G) app).attachBaseContext(context.getApplicationContext());
    }

    @Override
    public Object getSystemService(String name) {
        if (getBaseContext() == null) {
            return null;
        }
        return super.getSystemService(name);
    }
}
