Android Life Cycle
==========
A light library to speed up everything you are doing.

Take a look for basic usage below:

Factory
-------
Allows you to inject classes by calling **Factory.inject**, if the injected class implements Singleton interface, it insures that only one instance of this class will be created during application life cycle.

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

Just like events, this is a method for sharing information between classes

```Java
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
        System.out.println("Hello world");
    }
 
}
```
When **run** method of class **A** is called, **SayHiSignal** will be dispatched, and **handleSayHi** method of class **B** will be called. Of course **B** need to register for that signal, as it is doing in the **init** method, or nothing will happen.<br> 
Here is what you have to do, to create a signal



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
```


 - There could be multiple listeners for each signal
 - It is possible to register and unregister from signal.
 - The interface method may have parameters, just make sure to pass them for the dispatch method.
 - Signals are **Singleton**s, to use them locally create them without Factory. For example as a member of singleton model
 
Advanced usage
--------------
The documentation is not complete just yet, here are the additional usages that will be documented soon.

 - Using **Factory** as [factory patern](http://en.wikipedia.org/wiki/Factory_method_pattern)
 - Using **Factory** with family property, to implement [multiton patern](http://en.wikipedia.org/wiki/Multiton_pattern).
 - Using **Registrar** to create all the application dependencies, such as signal registration and factory pattern, during bootstrap time. 







