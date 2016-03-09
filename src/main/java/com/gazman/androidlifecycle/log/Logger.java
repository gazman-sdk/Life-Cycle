// =================================================================================================
//	Life Cycle Framework for native android
//	Copyright 2014 Ilya Gazman. All Rights Reserved.
//
//	This is not free software. You can redistribute and/or modify it
//	in accordance with the terms of the accompanying license agreement.
//  http://gazman-sdk.com/license/
// =================================================================================================
package com.gazman.androidlifecycle.log;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gazman.androidlifecycle.Factory;
import com.gazman.androidlifecycle.G;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ilya Gazman on 06-Dec-14.
 */
@SuppressWarnings("unused")
public class Logger {

    private static final AtomicInteger id = new AtomicInteger();
    private String tag;
    private String uniqueID = Integer.toString(id.incrementAndGet());
    private long startingTime = System.currentTimeMillis();
    private long lastCall = System.currentTimeMillis();
    private LogSettings localSettings;

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
        localSettings = Factory.inject(LogSettings.class);
        localSettings.init();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LogSettings getSettings() {
        return localSettings;
    }

    protected String getClassAndMethodNames(int dept) {
        if(!localSettings.isPrintMethodName()){
            return "";
        }
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[dept];
        String className = stackTraceElement.getClassName();
        String[] classSplit = className.split("\\.");
        String classShortName = classSplit[classSplit.length - 1];
        return classShortName + "." + stackTraceElement.getMethodName();
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
        if(!localSettings.isEnabled()){
            return;
        }

        try {
            Method method;
            if (throwable != null) {
                method = Log.class.getMethod(methodName, String.class, String.class, Throwable.class);
            } else {
                method = Log.class.getMethod(methodName, String.class, String.class);
            }
            printMessage(throwable, method, buildMessage(parameters));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildMessage(Object[] parameters) {
        return getPrefix() + join(parameters, " ") + localSettings.getSuffix();
    }

    private String getPrefix() {
        String methodPrefix = getClassAndMethodNames(5);
        String timePrefix = getTimePrefix();
        return join(
                localSettings.getPrefixDelimiter(),
                uniqueID,
                timePrefix,
                methodPrefix,
                localSettings.getPrefix());
    }

    private void printMessage(Throwable throwable, Method method, String message)
            throws IllegalAccessException,
            InvocationTargetException {
        if (message.length() > 4000) {
            printChunkedMessage(throwable, method, message);
        } else {
            invoke(throwable, method, message);
        }
    }

    private void printChunkedMessage(Throwable throwable, Method method, String message) throws IllegalAccessException, InvocationTargetException {
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
    }

    private void invoke(Throwable throwable, Method method, String message) throws IllegalAccessException, InvocationTargetException {
        if (throwable != null) {
            method.invoke(null, tag, message, throwable);
        } else {
            method.invoke(null, tag, message);
        }
    }

    public static String join(String delimiter, Object...parameters) {
        return join(parameters, delimiter);
    }

    public static String join(Object[] parameters, String delimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : parameters) {
            String objectString = object != null ? object.toString() : "null";
            stringBuilder.append(objectString);
            if(objectString.length() > 0){
                stringBuilder.append(delimiter);
            }
        }

        return stringBuilder.toString();
    }

    public String getTimePrefix() {
        if(!localSettings.isPrintTime()){
            return "";
        }
        long currentTimeMillis = System.currentTimeMillis();
        long totalTimePass = currentTimeMillis - startingTime;
        long timePass = currentTimeMillis - lastCall;
        lastCall = currentTimeMillis;

        return totalTimePass + "(" + timePass + ")";
    }

    public void toast(Object... objects){
        toast(G.app, objects);
    }

    public void toast(Context context, Object... objects){
        if(!localSettings.isEnabled()){
            return;
        }
        String message = join(objects, localSettings.getDelimiter());
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
