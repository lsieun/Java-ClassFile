# Java 7之try-with-resources语句

## without try-xxx

```java
import java.io.*;

public class HelloWorld {
    static int test(String path) throws IOException {
        InputStream in = new FileInputStream(path);
        return in.read();
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: new             #2   // bb0002     || java/io/FileInputStream
0003: dup                  // 59
0004: aload_0              // 2a
0005: invokespecial   #3   // b70003     || java/io/FileInputStream.<init>:(Ljava/lang/String;)V
0008: astore_1             // 4c
0009: aload_1              // 2b
0010: invokevirtual   #4   // b60004     || java/io/InputStream.read:()I
0013: ireturn              // ac

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      14  path:Ljava/lang/String;
    1         9       5  in:Ljava/io/InputStream;
```

## try-with-resource

```java
import java.io.*;

public class HelloWorld {
    static int test(String path) throws IOException {
        try (InputStream in = new FileInputStream(path);) {
            return in.read();
        }
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: new             #2   // bb0002     || java/io/FileInputStream
0003: dup                  // 59
0004: aload_0              // 2a
0005: invokespecial   #3   // b70003     || java/io/FileInputStream.<init>:(Ljava/lang/String;)V
0008: astore_1             // 4c
0009: aconst_null          // 01
0010: astore_2             // 4d
0011: aload_1              // 2b
0012: invokevirtual   #4   // b60004     || java/io/InputStream.read:()I
0015: istore_3             // 3e
0016: aload_1              // 2b
0017: ifnull          29   // c6001d
0020: aload_2              // 2c
0021: ifnull          21   // c60015
0024: aload_1              // 2b
0025: invokevirtual   #5   // b60005     || java/io/InputStream.close:()V
0028: goto            18   // a70012
0031: astore          4    // 3a04
0033: aload_2              // 2c
0034: aload           4    // 1904
0036: invokevirtual   #7   // b60007     || java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
0039: goto            7    // a70007
0042: aload_1              // 2b
0043: invokevirtual   #5   // b60005     || java/io/InputStream.close:()V
0046: iload_3              // 1d
0047: ireturn              // ac
0048: astore_3             // 4e
0049: aload_3              // 2d
0050: astore_2             // 4d
0051: aload_3              // 2d
0052: athrow               // bf
0053: astore          5    // 3a05
0055: aload_1              // 2b
0056: ifnull          29   // c6001d
0059: aload_2              // 2c
0060: ifnull          21   // c60015
0063: aload_1              // 2b
0064: invokevirtual   #5   // b60005     || java/io/InputStream.close:()V
0067: goto            18   // a70012
0070: astore          6    // 3a06
0072: aload_2              // 2c
0073: aload           6    // 1906
0075: invokevirtual   #7   // b60007     || java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
0078: goto            7    // a70007
0081: aload_1              // 2b
0082: invokevirtual   #5   // b60005     || java/io/InputStream.close:()V
0085: aload           5    // 1905
0087: athrow               // bf

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      88  path:Ljava/lang/String;
    1         9      79  in:Ljava/io/InputStream;

Exception Table:
from    to  target  type
  24    28      31  java/lang/Throwable
  11    16      48  java/lang/Throwable
  11    16      53  All Exceptions(catch_type = 0)
  63    67      70  java/lang/Throwable
  48    55      53  All Exceptions(catch_type = 0)
```

## try-finally

```java
public class HelloWorld {
    static int test(String path) throws IOException {
        InputStream in = null;

        try {
            in = new FileInputStream(path);
            return in.read();
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: aconst_null          // 01
0001: astore_1             // 4c
0002: new             #2   // bb0002     || java/io/FileInputStream
0005: dup                  // 59
0006: aload_0              // 2a
0007: invokespecial   #3   // b70003     || java/io/FileInputStream.<init>:(Ljava/lang/String;)V
0010: astore_1             // 4c
0011: aload_1              // 2b
0012: invokevirtual   #4   // b60004     || java/io/InputStream.read:()I
0015: istore_2             // 3d
0016: aload_1              // 2b
0017: ifnull          7    // c60007
0020: aload_1              // 2b
0021: invokevirtual   #5   // b60005     || java/io/InputStream.close:()V
0024: iload_2              // 1c
0025: ireturn              // ac
// 这里是发生“Exception”的情况下，处理的逻辑
0026: astore_3             // 4e
0027: aload_1              // 2b
0028: ifnull          7    // c60007
0031: aload_1              // 2b
0032: invokevirtual   #5   // b60005     || java/io/InputStream.close:()V
0035: aload_3              // 2d
0036: athrow               // bf

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      37  path:Ljava/lang/String;
    1         2      35  in:Ljava/io/InputStream;

Exception Table:
from    to  target  type
   2    16      26  All Exceptions(catch_type = 0)
```

## Summary

初步对比：

- try-with-resouces: 手写的Java Code少，生成的Byte Code多
- try-finally：手写的Java Code多，生成的Byte Code少

