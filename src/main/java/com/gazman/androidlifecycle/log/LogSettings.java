package com.gazman.androidlifecycle.log;

/**
 * Created by Ilya Gazman on 3/7/2016.
 */
public class LogSettings {
    private boolean enabled;
    private boolean printMethodName;
    private boolean printTime;
    private String prefix;
    private String suffix;
    private String delimiter;
    private String prefixDelimiter;

    public void init(){
        setEnabled(true);
        setPrefixDelimiter("|");
        setDelimiter(" ");
        setPrefix("");
        setPrintMethodName(false);
        setPrintTime(false);
        setSuffix("");
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public boolean isPrintTime() {
        return printTime;
    }

    public void setPrintTime(boolean printTime) {
        this.printTime = printTime;
    }

    public boolean isPrintMethodName() {
        return printMethodName;
    }

    public void setPrintMethodName(boolean printMethodName) {
        this.printMethodName = printMethodName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefixDelimiter() {
        return prefixDelimiter;
    }

    public void setPrefixDelimiter(String prefixDelimiter) {
        this.prefixDelimiter = prefixDelimiter;
    }
}
