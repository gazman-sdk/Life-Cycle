Android Life Cycle
==========
A light library to speed up everything you are doing.

Take a look for basic usage below or watch the [slid show](https:/mdocs.google.com/presentation/d/181WIzXmmO7e16gPUp_sV2lsfFfhR2z8mh2VsDcotBNU/pub?start=true&loop=false&delayms=60000) for full docomintation:

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

**How to do it with Android Life Cycle?**


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
 - SignalsBag got two methods: **inject** and **create**, inject is to use signal as singleton and create to create new instance of the signal.
 
See the full documintation in [slide Show]( https://docs.google.com/presentation/d/181WIzXmmO7e16gPUp_sV2lsfFfhR2z8mh2VsDcotBNU/pub?start=true&loop=false&delayms=60000)
