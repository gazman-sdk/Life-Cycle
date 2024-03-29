package com.gazman.lifecycle;

import com.gazman.lifecycle.signal.SignalsHelper;

import java.util.HashMap;
import java.util.LinkedList;


public abstract class Registrar {
    // GUIDE value
    static final String DEFAULT_FAMILY = "de9502c7-a41a-4fdb-9d42-249b94fbeaa9";
    static HashMap<Class<?>, HashMap<String, Class<?>>> classesMap = new HashMap<>();
    @SuppressWarnings("rawtypes")
    static HashMap<Class<?>, Builder> buildersMap = new HashMap<>();
    static LinkedList<Registrar> registrars = new LinkedList<>();

    /**
     * Will call {@link #registerClass(Class, Class, String)} with <br>
     * registerClass(claz, Object.class, DEFAULT_FAMILY)
     *
     * @param claz the class tree to register
     */
    protected void registerClass(Class<?> claz) {
        registerClass(claz, Object.class, DEFAULT_FAMILY);
    }

    /**
     * Will call {@link #registerClass(Class, Class, String)} with <br>
     * registerClass(claz, Object.class, family)
     *
     * @param claz the class tree to register
     */
    protected void registerClass(Class<?> claz, String family) {
        registerClass(claz, Object.class, family);
    }

    /**
     * Will call {@link #registerClass(Class, Class, String)} with <br>
     * registerClass(claz, topClaz, family)
     *
     * @param claz the class tree to register
     */
    protected <T, X extends T> void registerClass(Class<X> claz,
                                                  Class<T> topClaz) {
        registerClass(claz, topClaz, DEFAULT_FAMILY);
    }

    /**
     * When any class  @claz super family up to @topClaz will be injected @claz
     * will be created.
     *
     * @param claz    claz the class tree to register
     * @param topClaz topClaz and all it's supper classes will be mapped to claz
     * @param family  a key to be use in a multiton pattern
     */
    protected <T, X extends T> void registerClass(Class<X> claz,
                                                  Class<T> topClaz, String family) {
        if (family == null) {
            family = DEFAULT_FAMILY;
        }
        @SuppressWarnings("unchecked")
        Class<? super T> superclass = (Class<? super T>) claz;
        while (superclass != topClaz && superclass != null) {
            HashMap<String, Class<?>> hashMap = classesMap.get(superclass);
            //noinspection Java8MapApi
            if (hashMap == null) {
                hashMap = new HashMap<>();
                classesMap.put(superclass, hashMap);
            }
            hashMap.put(family, claz);
            // next
            superclass = superclass.getSuperclass();
        }

    }

    /**
     * When classToInject will be injected, it will be constructed using this builder
     */
    protected <T> void addBuilder(Class<T> classToInject, Builder<T> builder) {
        buildersMap.put(classToInject, builder);
    }

    protected void addRegistrar(Registrar registrar) {
        registrars.add(registrar);
        registrar.initRegistrars();
    }

    /**
     * A place to make all your {@link #registerClass(Class)} calls.
     */
    protected abstract void initClasses();

    protected abstract void initSignals(SignalsHelper signalsHelper);

    /**
     * A place to make all your {@link #addRegistrar(Registrar)} calls. <br>
     * *Note that order it very important - If you perform class overriding you
     * would prefer to add this Registrar last.
     */
    protected abstract void initRegistrars();

    /**
     * As the last step in the registration
     * You can optionally set general setting of your project here.
     */
    protected void initSettings() {

    }
}
