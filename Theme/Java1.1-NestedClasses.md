# Nested Classes

- Modifier
- class_info, super_class_info, interfaces
- constructor
- 引用外部的字段、方法、局部变量
- Attributes：InnerClass, EnclosingMethod

## Anonymous classes

```java
public class HelloWorld {
    public void test() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello World");
            }
        };
        r.run();
    }
}
```

`HelloWorld`类的`test()`方法的ByteCode：

```txt
=== === ===  === === ===  === === ===
0000: new             #2   // bb0002     || lsieun/sample/HelloWorld$1
0003: dup                  // 59
0004: aload_0              // 2a
0005: invokespecial   #3   // b70003     || lsieun/sample/HelloWorld$1.<init>:(Llsieun/sample/HelloWorld;)V
0008: astore_1             // 4c
0009: aload_1              // 2b
0010: invokeinterface #4   // b900040100 || java/lang/Runnable.run:()V
0015: return               // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      16  this:Llsieun/sample/HelloWorld;
    1         9       7  r:Ljava/lang/Runnable;
```

`HelloWorld$1`类的ClassFile的ByteCode：

- 字段：`this$0`（注意：自动生成，有ACC_SYNTHETIC标识）
- 构造器：`<init>:(Llsieun/sample/HelloWorld;)V` （注意：接收“外部类”的参数）
- 属性：EnclosingMethod、InnerClasses（注意：建立“内部类”和“外部类”之间的关系）

```txt
fields_count='0001' (1)
fields
    FieldInfo {Value='this$0:Llsieun/sample/HelloWorld;', AccessFlags='[ACC_FINAL,ACC_SYNTHETIC]', Attrs='[]', HexCode='10100009000a0000'}
methods_count='0002' (2)
methods
    MethodInfo {Value='<init>:(Llsieun/sample/HelloWorld;)V', AccessFlags='null', Attrs='[Code, MethodParameters]', HexCode='...'}
    MethodInfo {Value='run:()V', AccessFlags='[ACC_PUBLIC]', Attrs='[Code]', HexCode='...'}
attributes_count='0003' (3)
attributes
    SourceFile {name_index='0016'(22), length='00000002'(2), HexCode='0016000000020017'}
    EnclosingMethod {name_index='0018'(24), length='00000004'(4), HexCode='0018000000040019001a'}
    InnerClasses {name_index='0011'(17), length='0000000a'(10), HexCode='00110000000a00010006000000000000'}
```
