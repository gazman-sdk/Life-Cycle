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

How to do it in Android Life Cycle?

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

How to create a custom event in Android?

```Java
interface ISayHi
{
    void handleSayHi()
}

class SayHiEvent(){
    
    private static volatile SayHiEvent instance;
    ArrayList<ISayHi> listeners = new ArrayList<ISayHi>();
    
    public static synchronized SayHiEvent getInstance(){
        if(instance == null){
            instance = new SayHiEvent(); 
        }
        return instance;
    }

    public void addListener(ISayHi listener){
        listeners.add(listener);
    }
    
    public void removeListener(ISayHi listener){
        listeners.add(listener);
    }
    
    public void dispatch(){
        for(ISayHi listener : listeners){
            listener.handleSayHi();
        }
    }

}

class A{

    SayHiSignal signal = SayHiEvent.getInstance();

    void run(){
        signal.dispatch();
    }
    
}

class B implements ISayHiSignal{
    SayHiSignal signal = SayHiEvent.getInstance();
 
    void init(){
        signal.addListener(this);
    }
 
    void handleSayHi(){
        System.out.println("Hello world!");
    }
 
}
```

How to do it in Android Life Cycle?


```Java
class SayHiSignal extends Signal<ISayHiSignal> implements ISayHiSignal
{
    void handleSayHi(){
        dispatch();
    }
}
interface ISayHiSignal
{
    void handleSayHi()
}

class A{
    SayHiSignal signal = Factory.inject(SayHiSignal.class);

    void run(){
        signal.handleSayHi();
    }
    
}

class B implements ISayHiSignal{
    SayHiSignal signal = Factory.inject(SayHiSignal.class);
 
    void init(){
        signal.addListener(this);
    }
 
    void handleSayHi(){
        System.out.println("Hello world!");
    }
 
}
```

 - There could be multiple listeners for each signal
 - It is possible to register and unregister from signal.
 - The interface method may have parameters, just make sure to pass them for the dispatch method.
 - Signals are **Singleton**s, to use them locally create them without Factory. For example as a member of singleton model
 
Advanced usage
--------------
The documentation is not complete just yet(But the source is!), here are the additional usages that will be documented soon.

 - Using **Factory** as [factory patern](http://en.wikipedia.org/wiki/Factory_method_pattern)
 - Using **Factory** with family property, to implement [multiton patern](http://en.wikipedia.org/wiki/Multiton_pattern).
 - Using **Registrar** to create all the application dependencies, such as signal registration and factory pattern, during bootstrap time. 







