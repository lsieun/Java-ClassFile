# Varargs

<!-- TOC -->

- [1. Intro](#1-intro)
- [2. Example](#2-example)
  - [2.1. String Varargs](#21-string-varargs)
  - [2.2. int Varargs](#22-int-varargs)

<!-- /TOC -->

## 1. Intro

Varargs were introduced in Java 5 and provide a short-hand for methods that support an arbitrary number of parameters of one type.

Varargs are straightforward to use. But there're a few rules we have to keep in mind:

- Each method can only have one varargs parameter
- The varargs argument must be the last parameter

The varargs facility works by first creating an array whose size is the number of arguments passed at the call site, then putting the argument values into the array, and finally passing the array to the method.<sub>实现原理</sub>

## 2. Example

### 2.1. String Varargs

```java
public class A_String {
    public void targetMethod(String comment, String... args) {
        //
    }

    public void test() {
        targetMethod("Hello Varargs", "A", "B", "C");
    }

    public void test_empty() {
        // 注意：这里String类型可变参数的长度为0
        targetMethod("Hello String");
    }
}
```

targetMethod的签名是：`targetMethod:(Ljava/lang/String;[Ljava/lang/String;)V`

test()方法的bytecode如下：

```txt
=== === ===  === === ===  === === ===
0000: aload_0              // 2a
0001: ldc             #2   // 1202       || Hello Varargs
创建数组
0003: iconst_3             // 06
0004: anewarray       #3   // bd0003     || java/lang/String
添加第一个元素
0007: dup                  // 59
0008: iconst_0             // 03
0009: ldc             #4   // 1204       || A
0011: aastore              // 53
添加第二个元素
0012: dup                  // 59
0013: iconst_1             // 04
0014: ldc             #5   // 1205       || B
0016: aastore              // 53
添加第三个元素
0017: dup                  // 59
0018: iconst_2             // 05
0019: ldc             #6   // 1206       || C
0021: aastore              // 53
调用方法
0022: invokevirtual   #7   // b60007     || lsieun/sample/java5_varargs/A_String.targetMethod:(Ljava/lang/String;[Ljava/lang/String;)V
0025: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      26  this:Llsieun/sample/java5_varargs/A_String;
```

test_empty()方法的bytecode如下：

```txt
0000: aload_0              // 2a
0001: ldc             #2   // 1202       || Hello String
创建长度为0的数组
0003: iconst_0             // 03
0004: anewarray       #3   // bd0003     || java/lang/String
调用方法
0007: invokevirtual   #7   // b60007     || lsieun/sample/java5_varargs/A_String.targetMethod:(Ljava/lang/String;[Ljava/lang/String;)V
0010: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      11  this:Llsieun/sample/java5_varargs/A_String;
```

### 2.2. int Varargs

```java
public class B_Int {
    public void targetMethod(String comment, int... args) {
        //
    }

    public void test() {
        targetMethod("Hello int", 1, 2, 3);
    }
}
```

targetMethod的签名是：`targetMethod:(Ljava/lang/String;[I)V`

test()方法的bytecode如下：

```txt
0000: aload_0              // 2a
0001: ldc             #2   // 1202       || Hello int
创建数组
0003: iconst_3             // 06
0004: newarray        10   // bc0a       || int
添加第一个元素
0006: dup                  // 59
0007: iconst_0             // 03
0008: iconst_1             // 04
0009: iastore              // 4f
添加第二个元素
0010: dup                  // 59
0011: iconst_1             // 04
0012: iconst_2             // 05
0013: iastore              // 4f
添加第三个元素
0014: dup                  // 59
0015: iconst_2             // 05
0016: iconst_3             // 06
0017: iastore              // 4f
调用方法
0018: invokevirtual   #3   // b60003     || lsieun/sample/java5_varargs/B_Int.targetMethod:(Ljava/lang/String;[I)V
0021: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      22  this:Llsieun/sample/java5_varargs/B_Int;
```
