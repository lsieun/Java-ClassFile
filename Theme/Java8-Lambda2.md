# Anonymous Class VS Lambda

<!-- TOC -->

- [1. java.io.FileFilter](#1-javaiofilefilter)
- [java.lang.Runnable](#javalangrunnable)

<!-- /TOC -->

- Anonymous Class
- Lambda = anonymous functions

## 1. java.io.FileFilter

Before Java 8：使用匿名类的方式，会自动生成一个类`HelloWorld$1`，实现了`FileFilter`接口，使用了`invokespecial`指令。

```java
import java.io.File;
import java.io.FileFilter;

public class HelloWorld {
    public void test() {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isHidden();
            }
        };
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: new             #2   // bb0002     || lsieun/sample/HelloWorld$1
0003: dup                  // 59
0004: aload_0              // 2a
0005: invokespecial   #3   // b70003     || lsieun/sample/HelloWorld$1.<init>:(Llsieun/sample/HelloWorld;)V
0008: astore_1             // 4c
0009: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      10  this:Llsieun/sample/HelloWorld;
    1         9       1  filter:Ljava/io/FileFilter;
```

Java 8:使用`invokedynamic`命令

```java
import java.io.File;
import java.io.FileFilter;

public class HelloWorld {
    public void test() {
        FileFilter filter = File::isHidden;
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: invokedynamic   #2   // ba00020000 || accept:()Ljava/io/FileFilter;
0005: astore_1             // 4c
0006: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0       7  this:Llsieun/sample/HelloWorld;
    1         6       1  filter:Ljava/io/FileFilter;
```

## java.lang.Runnable

```java
public class HelloWorld {
    public void test() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello World!");
            }
        };
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: new             #2   // bb0002     || lsieun/sample/HelloWorld$1
0003: dup                  // 59
0004: aload_0              // 2a
0005: invokespecial   #3   // b70003     || lsieun/sample/HelloWorld$1.<init>:(Llsieun/sample/HelloWorld;)V
0008: astore_1             // 4c
0009: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      10  this:Llsieun/sample/HelloWorld;
    1         9       1  r:Ljava/lang/Runnable;
```

```java
public class HelloWorld {
    public void test() {
        Runnable r = () -> System.out.println("Hello World");
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: invokedynamic   #2   // ba00020000 || run:()Ljava/lang/Runnable;
0005: astore_1             // 4c
0006: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0       7  this:Llsieun/sample/HelloWorld;
    1         6       1  r:Ljava/lang/Runnable;
```
