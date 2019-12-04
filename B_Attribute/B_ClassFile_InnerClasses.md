# The InnerClasses Attribute

<!-- TOC -->

- [1. Intro](#1-intro)
- [2. Example](#2-example)
  - [2.1. First](#21-first)
  - [2.2. Second](#22-second)

<!-- /TOC -->

## 1. Intro

The `InnerClasses` attribute is a variable-length attribute in the `attributes` table of a `ClassFile` structure.

If the constant pool of a class or interface `C` contains at least one `CONSTANT_Class_info` entry which represents a class or interface that is not a member of a package, then there must be exactly one `InnerClasses` attribute in the `attributes` table of the `ClassFile` structure for `C`.

The `InnerClasses` attribute has the following format:

```txt
InnerClasses_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    {
        u2 inner_class_info_index;
        u2 outer_class_info_index;
        u2 inner_name_index;
        u2 inner_class_access_flags;
    } classes[number_of_classes];
}
```

## 2. Example

### 2.1. First

以前的时候，我总以为“InnerClasses”只是记录自己内部定义的类，下面这个例子让我意识到我错了。原来，别的类里定义的内部类，只要引用到了，它也要进行记录。

```java
import java.lang.invoke.MethodHandles;

public class HelloWorld {
    public void test() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
    }
}
```

Output:

```txt
constant_pool
    |014| CONSTANT_Class {Value='java/lang/invoke/MethodHandles$Lookup', HexCode='070019'}
    |015| CONSTANT_Utf8 {Value='Lookup', HexCode='0100064c6f6f6b7570'}
    |016| CONSTANT_Utf8 {Value='InnerClasses', HexCode='01000c496e6e6572436c6173736573'}
    |017| CONSTANT_Utf8 {Value='Ljava/lang/invoke/MethodHandles$Lookup;', HexCode='0100274c6a6176612f6c616e672f696e766f6b652f4d6574686f6448616e646c6573244c6f6f6b75703b'}
    |018| CONSTANT_Utf8 {Value='SourceFile', HexCode='01000a536f7572636546696c65'}
    |019| CONSTANT_Utf8 {Value='HelloWorld.java', HexCode='01000f48656c6c6f576f726c642e6a617661'}
    |020| CONSTANT_NameAndType {Value='<init>:()V', HexCode='0c00050006'}
    |021| CONSTANT_Class {Value='java/lang/invoke/MethodHandles', HexCode='07001a'}

InnerClasses:
HexCode: 00100000000a0001000e0015000f0019
attribute_name_index='0010' (16)
attribute_length='0000000a' (10)
number_of_classes='0001' (1)


|001|
inner_class_info_index='000e' (14)
outer_class_info_index='0015' (21)
inner_name_index='000f' (15)
inner_class_access_flags='0019' ([ACC_PUBLIC,ACC_STATIC,ACC_FINAL])
```

如果对于MethodHandles类不熟悉，可以使用下面的代码进行测试：

```java
import java.util.Map;

public class HelloWorld {
    public void test() {
        Map.Entry<String, String> entry = null;
    }
}
```

### 2.2. Second

这个示例的主要目的，就是在自己的内部定义类。

```java
public class HelloWorld {
    public static class NameAndDesc {
        public String name;
        public String desc;
    }
}
```

Output:

```txt
constant_pool
    |001| CONSTANT_Methodref {Value='java/lang/Object.<init>:()V', HexCode='0a00030010'}
    |002| CONSTANT_Class {Value='lsieun/sample/HelloWorld', HexCode='070011'}
    |003| CONSTANT_Class {Value='java/lang/Object', HexCode='070012'}
    |004| CONSTANT_Class {Value='lsieun/sample/HelloWorld$NameAndDesc', HexCode='070013'}
    |005| CONSTANT_Utf8 {Value='NameAndDesc', HexCode='01000b4e616d65416e6444657363'}
    |006| CONSTANT_Utf8 {Value='InnerClasses', HexCode='01000c496e6e6572436c6173736573'}

InnerClasses:
HexCode: 00060000000a00010004000200050009
attribute_name_index='0006' (6)
attribute_length='0000000a' (10)
number_of_classes='0001' (1)


|001|
inner_class_info_index='0004' (4)
outer_class_info_index='0002' (2)
inner_name_index='0005' (5)
inner_class_access_flags='0009' ([ACC_PUBLIC,ACC_STATIC])
```
