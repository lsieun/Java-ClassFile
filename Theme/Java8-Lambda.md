# Lambda

<!-- TOC -->

- [1. Method References in Bytecode](#1-method-references-in-bytecode)
- [2. Bound Instance Method References in Bytecode](#2-bound-instance-method-references-in-bytecode)
- [3. Free Instance Method References in Bytecode](#3-free-instance-method-references-in-bytecode)
- [4. Inline Lambda Definitions and Lambda Lifting](#4-inline-lambda-definitions-and-lambda-lifting)

<!-- /TOC -->

This chapter is for those who want to go deeper and understand how lambdas really work. This is your red pill vs. blue pill moment: do you want to live in a world where Java has lambdas and they work great and everything is slick and awesome, or do you want to see what is really going on beneath the surface and discover the magic of the compiler pulls? If you are happy with the lambdas as you know and love them, then skip this chapter and enjoy. If you need to know what your code really means, then hold on tight: it’s time to go down the rabbit hole.

We know that Java code itself does not compile directly to bytes that are executed by the operating system. Instead, the Java compiler (henceforth, “javac”) compiles to another intermediate form, which is executed by the Java Virtual Machine (henceforth, “JVM”). This intermediate form is called the Java bytecode. Bytecode, however, has no concept of lambdas. It also has no concept of try-with-resources blocks, enhanced for-loops, or many of the other structures within Java. Instead, the compiler converts those Java structures into an underlying form in bytecode.

In this chapter, we will see how the compiler converts lambda commands in the Java language into structures within the Java bytecode language.

| Java Language | Bytecode Language |
| ------------- | ----------------- |
| Lambda        | Bootstrap Methods |

通用命令：

```bash
javac -g HelloWorld.java
javap -v -p HelloWorld.class
```

## 1. Method References in Bytecode

In terms of bytecode, the simplest kind of lambda is the **method reference**.

```java
import java.util.function.Supplier;

public class HelloWorld  {
    public static String lambdiseMe() {
        return "Hello World!";
    }

    public static Supplier<String> getSupplier() {
        return HelloWorld::lambdiseMe;
    }
}
```

Output:

```txt
  public static java.lang.String lambdiseMe();
    descriptor: ()Ljava/lang/String;
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=1, locals=0, args_size=0
         0: ldc           #2                  // String Hello World!
         2: areturn
      LineNumberTable:
        line 5: 0

  public static java.util.function.Supplier<java.lang.String> getSupplier();
    descriptor: ()Ljava/util/function/Supplier;
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=1, locals=0, args_size=0
         0: invokedynamic #3,  0              // InvokeDynamic #0:get:()Ljava/util/function/Supplier;
         5: areturn
      LineNumberTable:
        line 9: 0
    Signature: #15                          // ()Ljava/util/function/Supplier<Ljava/lang/String;>;
}
SourceFile: "HelloWorld.java"
InnerClasses:
     public static final #38= #37 of #41; //Lookup=class java/lang/invoke/MethodHandles$Lookup of class java/lang/invoke/MethodHandles
BootstrapMethods:
  0: #21 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(
      Ljava/lang/invoke/MethodHandles$Lookup;
      Ljava/lang/String;
      Ljava/lang/invoke/MethodType;
      Ljava/lang/invoke/MethodType;
      Ljava/lang/invoke/MethodHandle;
      Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #22 ()Ljava/lang/Object;
      #23 invokestatic HelloWorld.lambdiseMe:()Ljava/lang/String;
      #24 ()Ljava/lang/String;
```

The `getSupplier` method is of the same basic form of the `lambdiseMe` method: it executes a command to push an object onto the stack, and then it returns the object on that stack. The difference is the command that pushes the object onto the stack. In `lambdiseMe`, it is loading a constant. In `getSupplier`, the value for the stack comes from this strange call, `invokedynamic`.

The `invokedynamic` instruction was introduced in **Java 7** to support dynamic languages that ran on the JVM, such as JRuby and Groovy. These dynamic languages had a similar problem: for certain common method calls, they could not know exactly what method to call until runtime. No amount of polymorphism helped them out: they simply had to defer the decision about what method to run until the moment the code was executed. The result was that each dynamic language had its own method dispatch structure built on top of Java’s own method dispatch structure, and those structures tended to be bad for Java’s optimizations. To help them out (and lay the groundwork for **lambdas**), **Java 7** introduced `invokedynamic`.<sub>讲这个instruction的由来</sub>

The `invokedynamic` instruction tells Java to wait until runtime, and then resolve the method name when the `invokedynamic` instruction is first encountered. When the `invokedynamic` instruction is encountered, it will delegate to another method provided by the classfile, called the “**bootstrap method**.” The **bootstrap method** will get a bunch of information about the call site, and may also take some extra constants. The bootstrap method will be expected to return the method binding for that location, which is encapsulated in the type `java.lang.invoke.CallSite`. That `CallSite` has all the information that the JVM needs to dispatch that method: from that point forward, when that particular `invokedynamic` instruction is encountered, the JVM will execute the method as specified by the `CallSite`.

```txt
invokedynamic --> bootstrap method --> CallSite --> JVM will execute the method
```

```txt
0: invokedynamic #3,  0              // InvokeDynamic #0:get:()Ljava/util/function/Supplier;
```

In this case, the instruction executes the Bootstrap Method `#0`, which is the “0” argument to the `invokedynamic` instruction. That **bootstrap methods** have their own pool, and in this case, `#0` is the static method `metafactory` on the class `java.lang.invoke.LambdaMetafactory`. Every bootstrap method is automatically passed `MethodHandles.Lookup` (a utility class to look up methods), the String name of the invoked method, and the type the `CallSite` is expected to provide as a `MethodType`. In this case, the type that is expected is a `Supplier`, and we are implementing the `get` method: that is specified by the other argument to `invokedynamic` (the `#3`).

```java
public class LambdaMetafactory {
    public static CallSite metafactory(MethodHandles.Lookup caller,
                                       String invokedName,
                                       MethodType invokedType,
                                       MethodType samMethodType,
                                       MethodHandle implMethod,
                                       MethodType instantiatedMethodType)
    {
        // 省略
    }
}
```

In the bootstrap method pool, though, we see a few other method arguments declared. These are tacked onto the end, and they are the **three remaining arguments** to the `metafactory` call. The second argument is the simplest: it is the source of the implementation for the lambda. In this case, it is the static invocation of our `lambdiseMe` method. The first and last arguments are **the implementation signature** and **runtime type of the method being implemented**. **The implementation signature** is something that takes no arguments and returns an object – `()Ljava/lang/Object;` – because that is the signature of `Supplier.get()` that we are trying to match. However, we also tell the runtime via the third argument that this method will actually always return a String – `()Ljava/lang/String;` – which is useful for optimization and casting.

```txt
BootstrapMethods:
  0: #21 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(
      Ljava/lang/invoke/MethodHandles$Lookup;
      Ljava/lang/String;
      Ljava/lang/invoke/MethodType;
      Ljava/lang/invoke/MethodType;
      Ljava/lang/invoke/MethodHandle;
      Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #22 ()Ljava/lang/Object;
      #23 invokestatic HelloWorld.lambdiseMe:()Ljava/lang/String;
      #24 ()Ljava/lang/String;
```

Let’s pull it all together. The `invokedynamic` instruction is encountered for the first time. The `metafactory` method is called, being passed **some default arguments** and **some custom arguments**. **The default arguments** are the utility for method lookup, the name of the invoked method, and an instruction to return a `Supplier`. **The custom arguments** specify that we will implement `Supplier.get()`, and even though it normally takes no arguments and returns an `Object`, we will really provide an implementation that takes no arguments and returns a `String`. Another custom argument specifies that the implementation of that is through statically invoking our `lambdiseMe` method. This bootstrap method returns the details about the call site, which is invoked to generate the `Supplier` based on our lambda. Whew!

This is a lot of work on paper, but **the shocking part is how fast this is, especially given repeated invocations**. The overhead of lambdas compared to anonymous functions is usually too small to be measured, especially on any kind of modern hardware. The Java 8 runtime is tuned well for this kind of work, and it does all this heavy lifting on the first invocation so that later invocations are lightweight and easy to optimize.

Given all this work for **a static method**, though, what happens when we want to call a method on some bound object, such as our old friend, `System.out::println`?

## 2. Bound Instance Method References in Bytecode

In the previous section, we saw how a static method was turned into a functional interface implementation via a method reference. The static case is the simplest; binding an instance method is trickier. An instance method also has to store its particular instance for later execution, and that instance has to be used to invoke the method.

```java
import java.util.function.Consumer;

public class HelloWorld  {
    public Consumer<String> getConsumer() {
        return System.out::println;
    }
}
```

Output:

```txt
  public java.util.function.Consumer<java.lang.String> getConsumer();
    descriptor: ()Ljava/util/function/Consumer;
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: dup
         4: invokevirtual #3                  // Method java/lang/Object.getClass:()Ljava/lang/Class;
         7: pop
         8: invokedynamic #4,  0              // InvokeDynamic #0:accept:(Ljava/io/PrintStream;)Ljava/util/function/Consumer;
        13: areturn
      LineNumberTable:
        line 5: 0
    Signature: #14                          // ()Ljava/util/function/Consumer<Ljava/lang/String;>;
}
SourceFile: "HelloWorld.java"
InnerClasses:
     public static final #47= #46 of #52; //Lookup=class java/lang/invoke/MethodHandles$Lookup of class java/lang/invoke/MethodHandles
BootstrapMethods:
  0: #22 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(
      Ljava/lang/invoke/MethodHandles$Lookup;
      Ljava/lang/String;
      Ljava/lang/invoke/MethodType;
      Ljava/lang/invoke/MethodType;
      Ljava/lang/invoke/MethodHandle;
      Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #23 (Ljava/lang/Object;)V
      #24 invokevirtual java/io/PrintStream.println:(Ljava/lang/String;)V
      #25 (Ljava/lang/String;)V
```

If we compare this to the previous function, we see that there is a bit more going on. The first thing is the `getstatic` call, which loads `System.out` onto the stack. Eventually, we will pass that instance into our `invokedynamic` call on instruction `8`, which will again go by way of the bootstrap method to produce our `Consumer`. The bootstrap method is practically identical to what we saw with the static method reference, except that our method is being invoked using `invokevirtual` instead of `invokestatic`. Whereas `invokestatic` makes the static method call, `invokevirtual` makes an instance method call. So that is our method reference, and the instance to invoke it on is also passed into the bootstrap method. Aside from that difference, this is all very similar to what we saw before. But what is that stuff in instructions `3`, `4`, and `7`?

```txt
         3: dup
         4: invokevirtual #3                  // Method java/lang/Object.getClass:()Ljava/lang/Class;
         7: pop
```

It is possible that this variable came from some other classloader far away, or that it was somehow gamed into existence. The bootstrap method, however, is going to need access to the class, and is going to need it to be fully formed. To ensure that the class is fully loaded and accessible in the current context, we will call the `Object.getClass()` method on `System.out`. The `invokevirtual` call, however, will consume the top element of the stack as the target, and we want to keep the top element around – it’s our reference to `System.out`. So we first do a `dup`, which duplicates the top element of the stack. The stack now has two `System.out` references, and we consume one with `invokevirtual` to call the `Object.getClass()` method. We don’t want to actually do anything with the class, however, so we can then just pop it off the stack.

This class maneuver(巧妙的动作；花招) is the kind of bookkeeping that you do not have to deal with in the world of Java, and which the compiler provides to you free of charge. It also demonstrates the subtlety and complexity of bootstrapping and method handle lookups, which it is very easy to get wrong. So please love and appreciate your compiler!

## 3. Free Instance Method References in Bytecode

There is one last kind of method reference: **the free instance method reference**. This is the case when we pass in a type and an instance method to the method reference, and it creates a method that accepts that type and calls the given instance method. This is somewhat like the bound method reference, in that it has to track what type is being called. However, we do not have a particular instance that is being called in this case, so it can’t act like the bound instance method reference. How does it work?

```java
import java.math.BigInteger;
import java.util.function.Function;

public class HelloWorld  {
    public Function<BigInteger, String> getFunction() {
        return BigInteger::toString;
    }
}
```

Output:

```txt
  public java.util.function.Function<java.math.BigInteger, java.lang.String> getFunction();
    descriptor: ()Ljava/util/function/Function;
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: invokedynamic #2,  0              // InvokeDynamic #0:apply:()Ljava/util/function/Function;
         5: areturn
      LineNumberTable:
        line 6: 0
    Signature: #12                          // ()Ljava/util/function/Function<Ljava/math/BigInteger;Ljava/lang/String;>;
}
SourceFile: "HelloWorld.java"
InnerClasses:
     public static final #36= #35 of #42; //Lookup=class java/lang/invoke/MethodHandles$Lookup of class java/lang/invoke/MethodHandles
BootstrapMethods:
  0: #17 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(
      Ljava/lang/invoke/MethodHandles$Lookup;
      Ljava/lang/String;
      Ljava/lang/invoke/MethodType;
      Ljava/lang/invoke/MethodType;
      Ljava/lang/invoke/MethodHandle;
      Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #18 (Ljava/lang/Object;)Ljava/lang/Object;
      #19 invokevirtual java/math/BigInteger.toString:()Ljava/lang/String;
      #20 (Ljava/math/BigInteger;)Ljava/lang/String;
```

It may seem like the free instance method is the strangest of all three types of method references, but the actual implementation is just as simple as the static method reference! The only difference is that the method handle passed into the bootstrap uses `invokevirtual` instead of `invokestatic`. This goes to show that the `invokevirtual` and the `invokestatic` calls are not directly invoked, but are instead implementation details about the functional interface itself. So there is no difference between creating a static and a free instance method, except in the way the implementation calls the methods.

## 4. Inline Lambda Definitions and Lambda Lifting

The last kind of lambda that you can have is **a lambda with an inline definition**. We are not talking about a reference to an existing method here, but instead defining an entirely new unit of functionality right inline. You may consider code like this:

```java
import java.util.function.Supplier;

public class HelloWorld  {
    public Supplier<String> getSupplier() {
        return () -> "Hello World!";
    }
}
```

```bash
javap -v -p HelloWorld.class
```

Output:

```txt
  public java.util.function.Supplier<java.lang.String> getSupplier();
    descriptor: ()Ljava/util/function/Supplier;
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: invokedynamic #2,  0              // InvokeDynamic #0:get:()Ljava/util/function/Supplier;
         5: areturn
      LineNumberTable:
        line 5: 0
    Signature: #13                          // ()Ljava/util/function/Supplier<Ljava/lang/String;>;

  private static java.lang.String lambda$getSupplier$0();
    descriptor: ()Ljava/lang/String;
    flags: ACC_PRIVATE, ACC_STATIC, ACC_SYNTHETIC
    Code:
      stack=1, locals=0, args_size=0
         0: ldc           #3                  // String Hello World!
         2: areturn
      LineNumberTable:
        line 5: 0
}
SourceFile: "HelloWorld.java"
InnerClasses:
     public static final #38= #37 of #41; //Lookup=class java/lang/invoke/MethodHandles$Lookup of class java/lang/invoke/MethodHandles
BootstrapMethods:
  0: #20 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #21 ()Ljava/lang/Object;
      #22 invokestatic HelloWorld.lambda$getSupplier$0:()Ljava/lang/String;
      #23 ()Ljava/lang/String;
```

What the compiler will do in this case is create a method for you with that implementation. That is called a “**synthetic method**.” This will be a **static method** that takes no arguments and returns the string, “Hello, World!” Once that is created, the compiler has now reduced the problem of the inline lambda into the **static method reference** problem that we saw before. It is a quite clever solution, and ultimately quite trivial an implementation – which means it is boring for us.

The more interesting case is when you have variables in the mix. Let’s create a method that calls an instance method (requiring access to `this`) along with a local variable in the scope. After all, these lambdas are closures: they enclose their scope. This kind of stuff is their job! How is the compiler going to deal with that complexity? The answer is in Following.

```java
import java.util.function.Supplier;

public class HelloWorld  {
    public String provideMessage(String message) {
        return message;
    }

    public Supplier<String> getSupplier(String message) {
        return () -> this.provideMessage(message);
    }
}
```

Output:

```txt
  public java.lang.String provideMessage(java.lang.String);
    descriptor: (Ljava/lang/String;)Ljava/lang/String;
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=2, args_size=2
         0: aload_1
         1: areturn
      LineNumberTable:
        line 5: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       2     0  this   LHelloWorld;
            0       2     1 message   Ljava/lang/String;

  public java.util.function.Supplier<java.lang.String> getSupplier(java.lang.String);
    descriptor: (Ljava/lang/String;)Ljava/util/function/Supplier;
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: invokedynamic #2,  0              // InvokeDynamic #0:get:(LHelloWorld;Ljava/lang/String;)Ljava/util/function/Supplier;
         7: areturn
      LineNumberTable:
        line 9: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       8     0  this   LHelloWorld;
            0       8     1 message   Ljava/lang/String;
    Signature: #20                          // (Ljava/lang/String;)Ljava/util/function/Supplier<Ljava/lang/String;>;

  private java.lang.String lambda$getSupplier$0(java.lang.String);
    descriptor: (Ljava/lang/String;)Ljava/lang/String;
    flags: ACC_PRIVATE, ACC_SYNTHETIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: aload_1
         2: invokevirtual #3                  // Method provideMessage:(Ljava/lang/String;)Ljava/lang/String;
         5: areturn
      LineNumberTable:
        line 9: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       6     0  this   LHelloWorld;
            0       6     1 message   Ljava/lang/String;
}
SourceFile: "HelloWorld.java"
InnerClasses:
     public static final #46= #45 of #49; //Lookup=class java/lang/invoke/MethodHandles$Lookup of class java/lang/invoke/MethodHandles
BootstrapMethods:
  0: #26 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #27 ()Ljava/lang/Object;
      #28 invokespecial HelloWorld.lambda$getSupplier$0:(Ljava/lang/String;)Ljava/lang/String;
      #29 ()Ljava/lang/String;
```

Now things have gotten really quite strange. We see the example of **the synthetic method** with the strange name: `lambda$getSupplier$0(java.lang.String);`. That is an instance method that takes a `String`, and calls `provideMessage` on that string using the instance: it loads `aload_0`, which is `this`, and then `aload_1`, which is the argument, and then does an `invokevirtual`. Our `getSupplier` method does something similar: it loads the `message` and `this`, and then does our familiar `invokedynamic` call to generate the supplier.

Note that we are passing in two arguments in our signature to `invokedynamic` now: all the variables that we need to reference inside our lambda implementation become arguments to `invokedynamic`. This is called “**lambda lifting**”: we lift the bound variables out of the lambda and make them arguments to instantiating it. The signal that we have performed lambda lifting is the use of `invokespecial` as our means of method invocation: we saw it used before to signal a constructor, but now we are using it as a signal to the bootstrap method that we are having to construct a new instance with the lifted arguments.

The important part to realize is that declaring a lambda that uses variables is a lot like declaring a new type that refers to those variables: we will end up instantiating a new instance that will hold onto those variables, and it will end up referring to those member fields when it executes. Therefore, defining a lambda inline and defining your own anonymous inner class have practically identical performance characteristics. Use whichever approach is more natural for the space you are in.<sub>这段话，我有点读不懂。</sub>
