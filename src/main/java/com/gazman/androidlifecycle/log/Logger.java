package com.gazman.androidlifecycle.log;


import android.util.Log;

import com.gazman.androidlifecycle.Bootstrap;
import com.gazman.androidlifecycle.Settings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by user on 06-Dec-14.
 */
public class Logger {
    private static final AtomicInteger id = new AtomicInteger();
    private String tag;
    private String uniqueID = Integer.toString(id.incrementAndGet());
    private String prefix = "";
    private String suffix = "";
    private long startingTime = System.currentTimeMillis();
    private long lastCall = System.currentTimeMillis();

    public Logger(Class tag){
        this(tag.getSimpleName());
    }

    public Logger(String tag){
        this.tag = tag;
    }

    public void d(Object...parameters){
        print("d", null, parameters);
    }

    protected String getClassAndMethodNames(int deap) {
        if(!Settings.allowLogs){
            return "";
        }
        StackTraceElement stackTraceElement = new Exception().getStackTrace()[deap];
        String className = stackTraceElement.getClassName();
        String[] classSplit = className.split("\\.");
        String classShortName = classSplit[classSplit.length - 1];
        return classShortName + "." + stackTraceElement.getMethodName();
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return (uniqueID != null ? uniqueID : "") + ": " + prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void d(Throwable throwable, Object...parameters){
        print("d", throwable, parameters);
    }

    public void i(Object...parameters){
        print("i", null, parameters);
    }

    public void i(Throwable throwable, Object...parameters){
        print("i", throwable, parameters);
    }

    public void w(Object...parameters){
        print("w", null, parameters);
    }

    public void w(Throwable throwable, Object...parameters){
        print("w", throwable, parameters);
    }

    public void e(Object...parameters){
        print("e", null, parameters);
    }

    public void e(Throwable throwable, Object...parameters){
        print("e", throwable, parameters);
    }

    private void print(String methodName, Throwable throwable, Object[] parameters) {
        setPrefix(getClassAndMethodNames(3));
        try {
            Method method;
            if(throwable != null){
                method = Log.class.getMethod(methodName, String.class, String.class, Throwable.class);
            }
            else {
                method = Log.class.getMethod(methodName, String.class, String.class);
            }
            String message = getPrefix() + toString(parameters);
            if (message.length() > 4000) {
                int chunkCount = message.length() / 4000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 4000 * (i + 1);
                    String chunkMessage;
                    if (max >= message.length()) {
                        chunkMessage =  "chunk " + i + " of " + chunkCount + ": " + message.substring(4000 * i);
                    } else {
                        chunkMessage =  "chunk " + i + " of " + chunkCount + ": " + message.substring(4000 * i, max);
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
        if(throwable != null){
            method.invoke(null, tag, message, throwable);
        }
        else {
            method.invoke(null, tag, message);
        }
    }

    private String toString(Object[] parameters){
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : parameters) {
            if(object != null){
                stringBuilder.append(object);
            }
            else{
                stringBuilder.append("null");
            }
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    public void log(Object...objects){
        long currentTimeMillis = System.currentTimeMillis();
        long totalTimePass = currentTimeMillis - startingTime;
        long timePass = currentTimeMillis - lastCall;
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : objects) {
            stringBuilder.append(object).append(" ");
        }
        Log.d(tag, totalTimePass + "(" + timePass + ") " + stringBuilder);
        lastCall = currentTimeMillis;
    }
}
