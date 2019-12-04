# Switch String

<!-- TOC -->

- [1. ByteCode](#1-bytecode)
- [2. Java Decompiler](#2-java-decompiler)
  - [2.1. IDEA反编译](#21-idea%e5%8f%8d%e7%bc%96%e8%af%91)
  - [2.2. jd.jar反编译](#22-jdjar%e5%8f%8d%e7%bc%96%e8%af%91)
- [3. Recap](#3-recap)

<!-- /TOC -->

```java
public class SwitchString {
    public void test(String input) {
        String result;

        switch (input) {
            case "AAA":
                result = input + "A";
                break;
            case "BBB":
                result = input + "B";
                break;
            default:
                result = input + "C";
        }

        System.out.println(result);
    }
}
```

## 1. ByteCode

```txt
0000: aload_1              // 2b
0001: astore_3             // 4e
0002: iconst_m1            // 02
0003: istore          4    // 3604
0005: aload_3              // 2d
0006: invokevirtual   #2   // b60002     || java/lang/String.hashCode:()I
0009: lookupswitch    54   // ab000000000036000000020000fc210000001b000100020000002a
      {
          64545: 27
          65538: 42
        default: 54
      }
0036: aload_3              // 2d
0037: ldc             #3   // 1203       || AAA
0039: invokevirtual   #4   // b60004     || java/lang/String.equals:(Ljava/lang/Object;)Z
0042: ifeq            21   // 990015
0045: iconst_0             // 03
0046: istore          4    // 3604
0048: goto            15   // a7000f
0051: aload_3              // 2d
0052: ldc             #5   // 1205       || BBB
0054: invokevirtual   #4   // b60004     || java/lang/String.equals:(Ljava/lang/Object;)Z
0057: ifeq            6    // 990006
0060: iconst_1             // 04
0061: istore          4    // 3604
0063: iload           4    // 1504
0065: lookupswitch    73   // ab00000000004900000002000000000000001b0000000100000032
      {
              0: 27
              1: 50
        default: 73
      }
0092: new             #6   // bb0006     || java/lang/StringBuilder
0095: dup                  // 59
0096: invokespecial   #7   // b70007     || java/lang/StringBuilder.<init>:()V
0099: aload_1              // 2b
0100: invokevirtual   #8   // b60008     || java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
0103: ldc             #9   // 1209       || A
0105: invokevirtual   #8   // b60008     || java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
0108: invokevirtual   #10  // b6000a     || java/lang/StringBuilder.toString:()Ljava/lang/String;
0111: astore_2             // 4d
0112: goto            46   // a7002e
0115: new             #6   // bb0006     || java/lang/StringBuilder
0118: dup                  // 59
0119: invokespecial   #7   // b70007     || java/lang/StringBuilder.<init>:()V
0122: aload_1              // 2b
0123: invokevirtual   #8   // b60008     || java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
0126: ldc             #11  // 120b       || B
0128: invokevirtual   #8   // b60008     || java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
0131: invokevirtual   #10  // b6000a     || java/lang/StringBuilder.toString:()Ljava/lang/String;
0134: astore_2             // 4d
0135: goto            23   // a70017
0138: new             #6   // bb0006     || java/lang/StringBuilder
0141: dup                  // 59
0142: invokespecial   #7   // b70007     || java/lang/StringBuilder.<init>:()V
0145: aload_1              // 2b
0146: invokevirtual   #8   // b60008     || java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
0149: ldc             #12  // 120c       || C
0151: invokevirtual   #8   // b60008     || java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
0154: invokevirtual   #10  // b6000a     || java/lang/StringBuilder.toString:()Ljava/lang/String;
0157: astore_2             // 4d
0158: getstatic       #13  // b2000d     || java/lang/System.out:Ljava/io/PrintStream;
0161: aload_2              // 2c
0162: invokevirtual   #14  // b6000e     || java/io/PrintStream.println:(Ljava/lang/String;)V
0165: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0     166  this:Llsieun/sample/java7_swith_string/SwitchString;
    1         0     166  input:Ljava/lang/String;
    2       112       3  result:Ljava/lang/String;
    2       135       3  result:Ljava/lang/String;
    2       158       8  result:Ljava/lang/String;
```

## 2. Java Decompiler

### 2.1. IDEA反编译

IDEA的反编译后显示的结果与ByteCode更为接近。

```java
public class SwitchString {
    public SwitchString() {
    }

    public void test(String input) {
        byte var4 = -1;
        switch(input.hashCode()) {
        case 64545:
            if (input.equals("AAA")) {
                var4 = 0;
            }
            break;
        case 65538:
            if (input.equals("BBB")) {
                var4 = 1;
            }
        }

        String result;
        switch(var4) {
        case 0:
            result = input + "A";
            break;
        case 1:
            result = input + "B";
            break;
        default:
            result = input + "C";
        }

        System.out.println(result);
    }
}
```

### 2.2. jd.jar反编译

```java
public class SwitchString
{
  public void test(String input) {
    String result, result, result;
    switch (input) {
      case "AAA":
        result = input + "A";
        break;
      case "BBB":
        result = input + "B";
        break;
      default:
        result = input + "C";
        break;
    } 
    System.out.println(result);
  }
}
```

## 3. Recap

在switch中使用`String`经过两个步骤：

- （1） 计算String的HashCode
- （2） 调用String的equals方法
