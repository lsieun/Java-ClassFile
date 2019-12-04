# Auto Boxing

**Auto Boxing** is used to convert primitive data types to their wrapper class objects. Wrapper class provide a wide range of function to be performed on the primitive types. The most common example is:

```java
int a = 56;
Integer i = a; // Auto Boxing
```

```java
public class HelloWorld {
    void test(){
        int a = 56;
        Integer i = a;
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: bipush          56   // 1038
0002: istore_1             // 3c
0003: iload_1              // 1b
0004: invokestatic    #2   // b80002     || java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
0007: astore_2             // 4d
0008: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0       9  this:Llsieun/sample/HelloWorld;
    1         3       6  a:I
    2         8       1  i:Ljava/lang/Integer;
```
