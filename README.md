Android Life Cycle
==========
A light library to speed up everything you are doing.

Take a look for basic usage below:

Factory
-------
How would you usualy create a Singleton in Android?

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

**How to do it with Android Life Cycle?**

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

 - To inject class with constructor parameters, call **Factory.injectWithParams()**

Signals
-------
A method of sharing information between classes, aka events.<br>
How would you usualy create a custom event in Android?

```Java
interface SayHi
{
    void onSayHi()
}

class SayHiEvent(){
    
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

**How to do it with Android Life Cycle?**


```Java
interface SayHiSignal
{
    void onSayHi()
}

class A{
    Signal<SayHiSignal> signal = SignalsBag.inject(SayHi.class);

    void run(){
        signal.dispatcher.onSayHi();
    }
    
}

class B implements SayHi{
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
 - SignalsBag got two methods: **inject** and **create**, inject is to use signal as singleton and create to create new instance of the signal.
 
Continue reading more on [gazman-sdk.com](http://gazman-sdk.com)
--------------
The documentation is not complete just yet(But the source is!), here are the additional usages that will be documented soon.

 - Using **Factory** as [factory patern](http://en.wikipedia.org/wiki/Factory_method_pattern)
 - Using **Factory** with family property, to implement [multiton patern](http://en.wikipedia.org/wiki/Multiton_pattern).
 - Using **Registrar** to create all the application dependencies, such as signal registration and factory pattern, during bootstrap time. 
