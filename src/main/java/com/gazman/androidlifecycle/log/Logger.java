// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle.log;


import android.util.Log;

import com.gazman.androidlifecycle.Factory;
import com.gazman.androidlifecycle.Settings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ilya Gazman on 06-Dec-14.
 */
public class Logger {

    public static final String DEFAULT_TAG = "App";

    private static final AtomicInteger id = new AtomicInteger();
    private String tag = DEFAULT_TAG;
    private String uniqueID = Integer.toString(id.incrementAndGet());
    private String prefix = "";
    private String suffix = "";
    private long startingTime = System.currentTimeMillis();
    private long lastCall = System.currentTimeMillis();
    private static String masterPrefix;

    /**
     * Creates logger using Factory and call the protected method init(tag);
     */
    public static Logger create(String tag){
        Logger logger = Factory.inject(Logger.class);
        logger.init(tag);
        return logger;
    }

    protected void init(String tag){
        this.tag = tag;
    }

    public static void setMasterPrefix(String masterPrefix){
        Logger.masterPrefix = masterPrefix;
    }

    protected String getClassAndMethodNames(int dept) {
        if (!Settings.allowLogs) {
            return "";
        }
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[dept];
        String className = stackTraceElement.getClassName();
        String[] classSplit = className.split("\\.");
        String classShortName = classSplit[classSplit.length - 1];
        return classShortName + "." + stackTraceElement.getMethodName();
    }

    /**
     * Apply prefix to all the logs
     * @param prefix prefix to apply
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Get the actual prefix. This one will include a unique id
     * @return log prefix
     */
    public String getPrefix() {
        if(masterPrefix != null){
            return masterPrefix + " " + uniqueID + "." + prefix;
        }
        return uniqueID + "." + prefix;
    }

    /**
     * Add suffix to all the logs
     * @param suffix suffix to add
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    /**
     * Default log
     * @param parameters Will concat those parameters using toString method
     *                   separated by space char.
     */
    public void d(Object... parameters) {
        print("d", null, parameters);
    }

    /**
     * Default log
     * @param parameters Will concat those parameters using toString method
     *                   separated by space char.
     * @param throwable Will print stack trace of this throwable
     */
    public void d(Throwable throwable, Object... parameters) {
        print("d", throwable, parameters);
    }

    /**
     * Default log
     * @param parameters Will concat those parameters using toString method
     *                   separated by space char.
     */
    public void i(Object... parameters) {
        print("i", null, parameters);
    }

    /**
     * Default log
     * @param parameters Will concat those parameters using toString method
     *                   separated by space char.
     * @param throwable Will print stack trace of this throwable
     */
    public void i(Throwable throwable, Object... parameters) {
        print("i", throwable, parameters);
    }

    /**
     * Warning log
     * @param parameters Will concat those parameters using toString method
     *                   separated by space char.
     */
    public void w(Object... parameters) {
        print("w", null, parameters);
    }

    /**
     * Warning log
     * @param parameters Will concat those parameters using toString method
     *                   separated by space char.
     * @param throwable Will print stack trace of this throwable
     */
    public void w(Throwable throwable, Object... parameters) {
        print("w", throwable, parameters);
    }

    /**
     * Exception log
     * @param parameters Will concat those parameters using toString method
     *                   separated by space char.
     */
    public void e(Object... parameters) {
        print("e", null, parameters);
    }

    /**
     * Exception log
     * @param parameters Will concat those parameters using toString method
     *                   separated by space char.
     * @param throwable Will print stack trace of this throwable
     */
    public void e(Throwable throwable, Object... parameters) {
        print("e", throwable, parameters);
    }

    private void print(String methodName, Throwable throwable, Object[] parameters) {
        setPrefix((prefix.length() > 0 ? prefix : "") + " " + getClassAndMethodNames(3) + " ");
        try {
            Method method;
            if (throwable != null) {
                method = Log.class.getMethod(methodName, String.class, String.class, Throwable.class);
            } else {
                method = Log.class.getMethod(methodName, String.class, String.class);
            }
            String message = getPrefix() + join(parameters, " ");
            if (message.length() > 4000) {
                int chunkCount = message.length() / 4000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 4000 * (i + 1);
                    String chunkMessage;
                    if (max >= message.length()) {
                        chunkMessage = "chunk " + i + " of " + chunkCount + ": " + message.substring(4000 * i);
                    } else {
                        chunkMessage = "chunk " + i + " of " + chunkCount + ": " + message.substring(4000 * i, max);
                    }
                    invoke(throwable, method, chunkMessage);
                }
            } else {
                invoke(throwable, method, message);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void invoke(Throwable throwable, Method method, String message) throws IllegalAccessException, InvocationTargetException {
        if (throwable != null) {
            method.invoke(null, tag, message, throwable);
        } else {
            method.invoke(null, tag, message);
        }
    }

    public static String join(Object[] parameters, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : parameters) {
            if (object != null) {
                stringBuilder.append(object);
            } else {
                stringBuilder.append("null");
            }
            stringBuilder.append(delimiter);
        }

        return stringBuilder.toString();
    }

    public void log(Object... objects) {
        long currentTimeMillis = System.currentTimeMillis();
        long totalTimePass = currentTimeMillis - startingTime;
        long timePass = currentTimeMillis - lastCall;
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : objects) {
            stringBuilder.append(object).append(" ");
        }
        Log.d(tag, getPrefix() + " " + totalTimePass + "(" + timePass + ") " + stringBuilder);
        lastCall = currentTimeMillis;
    }
}
