Life Cycle - for android
==========
A light library to speed up everything you are doing.

Releaselog
-----------
**5/31/2015**

- Fixed synchronizations bugs in signal. Now you can add signal while signal is been dispatched, it will be added to the current dispatch cycle or to the next one.
- Added LifeCycle.exit() - It will unregister all the signals in the system and free all the singletons, also it dispatch DisposableSignal so you can add your custom dispose logic
- Added Logger, I think it's a good idea to have the logger in the top hierarchy of your modules.

Usage
-----
Lear how to use life cycle in the official [slid show](https://docs.google.com/presentation/d/181WIzXmmO7e16gPUp_sV2lsfFfhR2z8mh2VsDcotBNU/pub?start=false&loop=false&delayms=3000). It covers all life cycle futures, or read the top futures belaw.

Factory
-------
How would you usually create a Singleton in Android?

```Java
class MyModel{
    private static volatile MyModel instance;
    
    public static synchronized MyModel getInstance(){
        if(instance == null){
            instance = new MyModel(); 
        }
        return instance;
    }
}

class A{
    MyModel myModel = MyModel.getInstance();
}
class B{
    MyModel myModel = MyModel.getInstance();
}
```

**How to do it with Life Cycle?**

```Java
class MyModel implements Singleton{
    
}

class A{
    MyModel myModel = Factory.inject(MyModel.class);
}
class B{
    MyModel myModel = Factory.inject(MyModel.class);
}
```

Both classes **A** and **B** have the same reference to **MyModel** class.
 
- In addition to that you can replace singleton with extended version of it in all the places where it been use.
- When exit the application, you can free all the singletons in your system at once and allow GC to collect them.

 * To inject class with constructor parameters, call **Factory.injectWithParams()**

Signals
-------
A method of sharing information between classes, aka events.<br>
How would you usualy create a custom event in Android?

```Java
interface SayHi
{
    void onSayHi()
}

class SayHiEvent{
    
    private static volatile SayHiEvent instance;
    ArrayList<SayHi> listeners = new ArrayList<SayHi>();
    
    public static synchronized SayHiEvent getInstance(){
        if(instance == null){
            instance = new SayHiEvent(); 
        }
        return instance;
    }

    public void addListener(SayHi listener){
        listeners.add(listener);
    }
    
    public void removeListener(SayHi listener){
        listeners.remove(listener);
    }
    
    public void dispatch(){
        for(SayHi listener : listeners){
            listener.handleSayHi();
        }
    }

}

class A{
    SayHiEvent event = SayHiEvent.getInstance();

    void run(){
        event.dispatch();
    }
    
}

class B implements SayHi{
    SayHiEvent event = SayHiEvent.getInstance();
 
    void init(){
        event.addListener(this);
    }
 
    void onSayHi(){
        System.out.println("Hello world!");
    }
 
}
```

**How to do it with Life Cycle?**


```Java
interface SayHiSignal
{
    void onSayHi()
}

class A{
    Signal<SayHiSignal> signal = SignalsBag.inject(SayHiSignal.class);

    void run(){
        signal.dispatcher.onSayHi();
    }
    
}

class B implements SayHiSignal{
    Signal<SayHiSignal> signal = SignalsBag.inject(SayHiSignal.class);
 
    void init(){
        signal.addListener(this);
    }
 
    void onSayHi(){ 
        System.out.println("Hello world!");
    }
 
}
```

 - There could be multiple listeners for each signal
 - It is possible to register and unregister from signal.
 - Also signal got the method **addListenerOnce()**, it will automaticaly unregister the listener after the first dispatch of the signal
 - SignalsBag got two methods: **inject** and **create**, "inject" is to use signal as singleton and "create" to create new instance of the signal.
 
See the full documintation in [slide Show](https://docs.google.com/presentation/d/181WIzXmmO7e16gPUp_sV2lsfFfhR2z8mh2VsDcotBNU/pub?start=false&loop=false&delayms=3000)

License
-------
Copyright 2014 Ilya Gazman

Licensed under the Coffe License (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://gazman-sdk.com/license/
