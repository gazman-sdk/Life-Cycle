Life Cycle is a light and powerful library to handle events and dependencies in your app.

Signals
-------
Signals was separated to its own repo now and is used as a dependency.

Check out below for more details
https://github.com/gazman-sdk/signals

Singletons
------------
Life cycle singletons allow you to create, remove and overwrite singletons easily 
```java
class LoginModel implements Singleton{
    
}

class LoginScreen{
    LoginModele loginModule = Factory.inject(LoginModele.class);
}
```

`Factory.inject` method will return an instance of the injected class. It is promised that if the class implements the singleton interface it will be created only once. Otherwise, a new instance of it will be created each injection.

It's also possible to pass constructors` params during the injection, however, it's highly unrecommended as it breaks hard-typing.

```java
class LoginModel implements Singleton{
    LoginModel(String userName){
        
    }
}

class LoginScreen{
    LoginModele loginModule = Factory.injectWithParams(LoginModele.class, "userName");
}
```

Registrars and Singleton overwriting
-------------------------------------
Registrars are Life Cycle dynamic injections mechanism. It allows you to manage dependencies as well as pre-register for signals.

The main Register class is called `Bootstrap`, the registration process is starting when 'Bootstrap.initialize()' is called.

```java
class MyApp extends Bootstrap {
    @Override
    protected void initClasses() {
        registerClass(MyUserModel.class);
    }

    @Override
    protected void initSignals(SignalsHelper signalsHelper) {
        signalsHelper.addListener(LoginSignal.class, LoginScreen.class);
    }

    @Override
    protected void initRegistrars() {
        addRegistrar(new GameRegistrar());
    }
}
```  

`registerClass` Allows you to override any injected class, even if it's a singleton. In the example above when a class that super MyUserModel, in other words, if MyUserModel is an instance of some class, then whenever that superclass is injected MyUserModel instance will be returned. If MyUserModel extends UserModel, then when UserModel will be injected, MyUserModel instance will be returned. This rule applies to all the injection levels. 

`initSignals` Allows you to register signals during the bootstrap phase. It's useful if you are building a library and want to define some signals ahead of time.

```initRegistrars` - 'Bootstrap' extends a Registrar by adding it the 'Bootstrap.initialize()' method. However, you might use this place to add other registers in your app or some other libraries, and since Bootstrap is a Registrar if a library has a Bootstrap it can be added as a register here.

In case you need more control over the injection you can use a builder, it will be invoked upon every injection of the provided class. Note that the actual injected class is calculated before the builder logic kicks in, so if `A` extends `B` extends `C` and the builder was mapped to `B`, then it will be invoked upon `B` injections. If `registerClass(B.class)` was called then it will be invoked upon `C` injections as well. However if  `registerClass(A.class)` was called then it will no longer be invoked not for A injections and not for B injections. 

```java
class MyApp extends Bootstrap {
    @Override
    protected void initClasses() {
        this.registerClass(MyUserModel.class);
        addBuilder(MyUserModel.class, (classToInject, params) -> {
            MyUserModel myUserModel = ...
            return myUserModel;
        });
    }
}
```

Families
---------
Sometimes times you need using the [Multiton pattern](https://en.wikipedia.org/wiki/Multiton_pattern). For example, you are making an eCommerce app, and you got two products pages that share much functionality but needs to have their model and may even some minor changes. To address this need, a family was created.

```java
class LoginScreen{
    LoginModele loginModule = Factory.inject(LoginModele.class, 'bathFamily');
}
```

If LoginModel is a singleton, then it's promised to have only one instance if it for each family. Also, note that the injection works recursively. If the login model will make any injections during in its constructor and will not provide any family, it will default to the last used family, `'bathFamily'` in our case. It only works during the constructor time.

StackOverflow bug
-----------------

Consider the below example.

```java
class LoginScreen{
    LoginModele loginModule = Factory.inject(LoginModele.class);
}

class LoginModel{
    LoginScreen loginScreen = Factory.inject(LoginScreen.class);
}
```

During the construction of `LoginScreen` we are constructing a `LoginModel` but before the construction of aether one of those is ended we are starting to construct `LoginScreen` again from the `LoginModel`. The result of this will be a StackOverflowException. So instead of telling you don't do it, try to keep a separation of control in your app and fallow the [Single responsibility principle](https://en.wikipedia.org/wiki/Single_responsibility_principle). I actually would like to show you a LifeCycle solution to this, it's called `Injector` and used like this:
 
```java
class LoginScreen{
    LoginModele loginModule = Factory.inject(LoginModele.class);
}

class LoginModel implements Injector{
    LoginScreen loginScreen;
    
    @Override
    public void injectionHandler(String family) {
        loginScreen = Factory.inject(LoginScreen.class);
    }
}
```

The `Injector` will keep the family recursion policy that the constructors have and it will prevent the StackOverflowException by allowing LoginScreen to be constructed before it been injected. Also if you were looking for a way to get the current family information to perhaps associate it with file names or other persistent settings, using `Injector` is the way to get it. 