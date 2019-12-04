# Assertions

<!-- TOC -->

- [1. Assert](#1-assert)
  - [1.1. Field: `$assertionsDisabled:Z`](#11-field-assertionsdisabledz)
  - [1.2. Method: `<clinit>:()V`](#12-method-clinitv)
  - [1.3. Method: `test:(I)V`](#13-method-testiv)
- [2. Class.desiredAssertionStatus()](#2-classdesiredassertionstatus)

<!-- /TOC -->

## 1. Assert

```java
public class A_Assert {
    public void test(int i) {
        assert i == 10;
        System.out.println(i);
    }
}
```

Out:

```txt
fields
    FieldInfo {Value='$assertionsDisabled:Z', AccessFlags='[ACC_STATIC,ACC_FINAL,ACC_SYNTHETIC]', Attrs='[]', HexCode='1018000800090000'}
methods_count='0003' (3)
methods
    MethodInfo {Value='<init>:()V', AccessFlags='[ACC_PUBLIC]', Attrs='[Code]', HexCode='...'}
    MethodInfo {Value='test:(I)V', AccessFlags='[ACC_PUBLIC]', Attrs='[Code, MethodParameters]', HexCode='...'}
    MethodInfo {Value='<clinit>:()V', AccessFlags='[ACC_STATIC]', Attrs='[Code]', HexCode='...'}
```

### 1.1. Field: `$assertionsDisabled:Z`

这是一个`static final`类型的，并且是自动生成的字段（`ACC_SYNTHETIC`）。

```txt
FieldInfo {Value='$assertionsDisabled:Z', AccessFlags='[ACC_STATIC,ACC_FINAL,ACC_SYNTHETIC]', Attrs='[]', HexCode='1018000800090000'}
```

### 1.2. Method: `<clinit>:()V`

在`<clinit>:()V`方法中，执行`Class.desiredAssertionStatus()`来给`$assertionsDisabled:Z`赋值。

```txt
0000: ldc             #5   // 1205       || lsieun/sample/java4_assertions/A_Assert
0002: invokevirtual   #6   // b60006     || java/lang/Class.desiredAssertionStatus:()Z
0005: ifne            7    // 9a0007
0008: iconst_1             // 04
0009: goto            4    // a70004
0012: iconst_0             // 03
0013: putstatic       #2   // b30002     || lsieun/sample/java4_assertions/A_Assert.$assertionsDisabled:Z
0016: return               // b1
```

### 1.3. Method: `test:(I)V`

```txt
0000: getstatic       #2   // b20002     || lsieun/sample/java4_assertions/A_Assert.$assertionsDisabled:Z
0003: ifne            17   // 9a0011
0006: iload_1              // 1b
0007: bipush          10   // 100a
0009: if_icmpeq       11   // 9f000b
0012: new             #3   // bb0003     || java/lang/AssertionError
0015: dup                  // 59
0016: invokespecial   #4   // b70004     || java/lang/AssertionError.<init>:()V
0019: athrow               // bf
0020: getstatic       #5   // b20005     || java/lang/System.out:Ljava/io/PrintStream;
0023: iload_1              // 1b
0024: invokevirtual   #6   // b60006     || java/io/PrintStream.println:(I)V
0027: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      28  this:Llsieun/sample/java4_assertions/A_Assert;
    1         0      28  i:I
```

## 2. Class.desiredAssertionStatus()

```java
public class HelloWorld {
    public static void main(String[] args) {
        boolean b = HelloWorld.class.desiredAssertionStatus();
        System.out.println(b);
    }
}
```

Out:

```bash
$ java HelloWorld
false
$ java -ea HelloWorld
true
```
