package com.gazman.lifecycle;

/**
 * Created by Ilya Gazman on 3/18/2015.
 */
public interface Builder<T> {

    /**
     * Construct new instance of the class using optional params
     *
     * @param params will not be null
     * @return new instance of the class
     */
    T build(Class<T> classToInject, Object[] params);
}
