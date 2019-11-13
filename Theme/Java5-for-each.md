# Java for-each Loop (Enhanced for Loop)

In Java, there is another form of for loop (in addition to standard for loop) to work with arrays and collection, the enhanced for loop.

Here's an example to iterate through elements of an array using standard for loop:

To learn about standard syntax of for loop, arrays and collections, visit:

- Java for Loop
- Java arrays
- Java collections

## Java arrays

```java
public class HelloWorld {
    private static char[] vowels = {'a', 'e', 'i', 'o', 'u'};

    public static void main(String[] args) {
        for (int i = 0; i < vowels.length; ++i) {
            System.out.println(vowels[i]);
        }
    }
}
```

```txt
=== === ===  === === ===  === === ===
0000: iconst_0             // 03
0001: istore_1             // 3c
0002: iload_1              // 1b
0003: getstatic       #2   // b20002     || lsieun/sample/HelloWorld.vowels:[C
0006: arraylength          // be
0007: if_icmpge       20   // a20014
0010: getstatic       #3   // b20003     || java/lang/System.out:Ljava/io/PrintStream;
0013: getstatic       #2   // b20002     || lsieun/sample/HelloWorld.vowels:[C
0016: iload_1              // 1b
0017: caload               // 34
0018: invokevirtual   #4   // b60004     || java/io/PrintStream.println:(C)V
0021: iinc       1    1    // 840101
0024: goto            -22  // a7ffea
0027: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      28  args:[Ljava/lang/String;
    1         2      25  i:I
```

```java
public class HelloWorld {
    private static char[] vowels = {'a', 'e', 'i', 'o', 'u'};

    public static void main(String[] args) {
        // foreach loop
        for (char item: vowels) {
            System.out.println(item);
        }
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: getstatic       #2   // b20002     || lsieun/sample/HelloWorld.vowels:[C
0003: astore_1             // 4c
0004: aload_1              // 2b
0005: arraylength          // be
0006: istore_2             // 3d
0007: iconst_0             // 03
0008: istore_3             // 3e
0009: iload_3              // 1d
0010: iload_2              // 1c
0011: if_icmpge       22   // a20016
0014: aload_1              // 2b
0015: iload_3              // 1d
0016: caload               // 34
0017: istore          4    // 3604
0019: getstatic       #3   // b20003     || java/lang/System.out:Ljava/io/PrintStream;
0022: iload           4    // 1504
0024: invokevirtual   #4   // b60004     || java/io/PrintStream.println:(C)V
0027: iinc       3    1    // 840301
0030: goto            -21  // a7ffeb
0033: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      34  args:[Ljava/lang/String;
    4        19       8  item:C
```

## Java collections

```java
public class HelloWorld {
    private static List<Integer> list = new ArrayList<>();

    static {
        list.add(1);
        list.add(2);
        list.add(3);
    }

    public static void main(String[] args) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }
}
```

```txt
=== === ===  === === ===  === === ===
0000: iconst_0             // 03
0001: istore_1             // 3c
0002: iload_1              // 1b
0003: getstatic       #2   // b20002     || lsieun/sample/HelloWorld.list:Ljava/util/List;
0006: invokeinterface #3   // b900030100 || java/util/List.size:()I
0011: if_icmpge       24   // a20018
0014: getstatic       #4   // b20004     || java/lang/System.out:Ljava/io/PrintStream;
0017: getstatic       #2   // b20002     || lsieun/sample/HelloWorld.list:Ljava/util/List;
0020: iload_1              // 1b
0021: invokeinterface #5   // b900050200 || java/util/List.get:(I)Ljava/lang/Object;
0026: invokevirtual   #6   // b60006     || java/io/PrintStream.println:(Ljava/lang/Object;)V
0029: iinc       1    1    // 840101
0032: goto            -30  // a7ffe2
0035: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      36  args:[Ljava/lang/String;
    1         2      33  i:I
```

```java
public class HelloWorld {
    private static List<Integer> list = new ArrayList<>();

    static {
        list.add(1);
        list.add(2);
        list.add(3);
    }

    public static void main(String[] args) {
        // foreach loop
        for (Integer item: list) {
            System.out.println(item);
        }
    }
}
```

Output:

```txt
=== === ===  === === ===  === === ===
0000: getstatic       #2   // b20002     || lsieun/sample/HelloWorld.list:Ljava/util/List;
0003: invokeinterface #3   // b900030100 || java/util/List.iterator:()Ljava/util/Iterator;
0008: astore_1             // 4c
0009: aload_1              // 2b
0010: invokeinterface #4   // b900040100 || java/util/Iterator.hasNext:()Z
0015: ifeq            23   // 990017
0018: aload_1              // 2b
0019: invokeinterface #5   // b900050100 || java/util/Iterator.next:()Ljava/lang/Object;
0024: checkcast       #6   // c00006     || java/lang/Integer
0027: astore_2             // 4d
0028: getstatic       #7   // b20007     || java/lang/System.out:Ljava/io/PrintStream;
0031: aload_2              // 2c
0032: invokevirtual   #8   // b60008     || java/io/PrintStream.println:(Ljava/lang/Object;)V
0035: goto            -26  // a7ffe6
0038: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      39  args:[Ljava/lang/String;
    2        28       7  item:Ljava/lang/Integer;
```

