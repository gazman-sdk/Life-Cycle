package com.gazman.lifecycle;

public interface Injector {
    /**
     * Will be called right after the constructor
     *
     * @param family The family this instance been injected in to
     */
    void injectionHandler(String family);
}
