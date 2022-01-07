package com.gazman.lifecycle.log;

/**
 * Created by Ilya Gazman on 3/7/2016.
 */
public class LogSettings {
    private boolean enabled;
    private boolean printMethodName;
    private boolean printTime;
    private String appPrefix;
    private String suffix;
    private String delimiter;
    private String prefixDelimiter;
    private boolean showPid;
    private int minTagLength;

    public void init() {
        setEnabled(true);
        setPrefixDelimiter("|");
        setDelimiter(" ");
        setAppPrefix("~");
        setPrintMethodName(false);
        setPrintTime(true);
        setSuffix("");
        setShowPid(true);
        setMinTagLength(18);
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
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

    public String getAppPrefix() {
        return appPrefix;
    }

    public void setAppPrefix(String appPrefix) {
        this.appPrefix = appPrefix;
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

    public boolean isShowPid() {
        return showPid;
    }

    public void setShowPid(boolean showPid) {
        this.showPid = showPid;
    }

    public int getMinTagLength() {
        return minTagLength;
    }

    public void setMinTagLength(int minTagLength) {
        this.minTagLength = minTagLength;
    }
}
