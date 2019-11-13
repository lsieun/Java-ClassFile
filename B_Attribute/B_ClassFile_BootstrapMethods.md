# The BootstrapMethods Attribute

The `BootstrapMethods` attribute is a **variable-length attribute** in the `attributes` table of a `ClassFile` structure.<sub>注：说明BootstrapMethods所处的位置</sub>

The `BootstrapMethods` attribute records **bootstrap methods** used to produce **dynamically-computed constants** and **dynamically-computed call sites**.<sub>注：说明BootstrapMethods的用处</sub>

There must be exactly one `BootstrapMethods` attribute in the `attributes` table of a `ClassFile` structure if the `constant_pool` table of the `ClassFile` structure has at least one `CONSTANT_Dynamic_info` or `CONSTANT_InvokeDynamic_info` entry.

There may be at most one `BootstrapMethods` attribute in the `attributes` table of a `ClassFile` structure.

The `BootstrapMethods` attribute has the following format:

```txt
BootstrapMethods_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 num_bootstrap_methods;
    {
        u2 bootstrap_method_ref;
        u2 num_bootstrap_arguments;
        u2 bootstrap_arguments[num_bootstrap_arguments];
    } bootstrap_methods[num_bootstrap_methods];
}
```

```java
import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Attr_BootstrapMethods {
    public Consumer<String> getConsumer() {
        return System.out::println;
    }

    public Function<BigInteger, String> getFunction() {
        return BigInteger::toString;
    }

    public Supplier<String> getSupplier1() {
        return () -> "Hello World!";
    }

    public Supplier<String> getSupplier2() {
        return () -> "Hello Lambda!";
    }
}
```

Byte Code:

```txt
constant_pool
    |039| CONSTANT_Utf8 {Value='BootstrapMethods', HexCode='010010426f6f7473747261704d6574686f6473'}
    |040| CONSTANT_MethodHandle {Value='java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;', HexCode='0f06003f'}
    |041| CONSTANT_MethodType {Value='(Ljava/lang/Object;)V', HexCode='100040'}
    |042| CONSTANT_MethodHandle {Value='java/io/PrintStream.println:(Ljava/lang/String;)V', HexCode='0f050041'}
    |043| CONSTANT_MethodType {Value='(Ljava/lang/String;)V', HexCode='100042'}
    |044| CONSTANT_NameAndType {Value='accept:(Ljava/io/PrintStream;)Ljava/util/function/Consumer;', HexCode='0c00430044'}
    |045| CONSTANT_MethodType {Value='(Ljava/lang/Object;)Ljava/lang/Object;', HexCode='100045'}
    |046| CONSTANT_MethodHandle {Value='java/math/BigInteger.toString:()Ljava/lang/String;', HexCode='0f050046'}
    |047| CONSTANT_MethodType {Value='(Ljava/math/BigInteger;)Ljava/lang/String;', HexCode='100047'}
    |048| CONSTANT_NameAndType {Value='apply:()Ljava/util/function/Function;', HexCode='0c00480018'}
    |049| CONSTANT_MethodType {Value='()Ljava/lang/Object;', HexCode='100049'}
    |050| CONSTANT_MethodHandle {Value='lsieun/sample/classfile_attr/Attr_BootstrapMethods.lambda$getSupplier1$0:()Ljava/lang/String;', HexCode='0f06004a'}
    |051| CONSTANT_MethodType {Value='()Ljava/lang/String;', HexCode='10001f'}
    |052| CONSTANT_NameAndType {Value='get:()Ljava/util/function/Supplier;', HexCode='0c004b001b'}
    |053| CONSTANT_MethodHandle {Value='lsieun/sample/classfile_attr/Attr_BootstrapMethods.lambda$getSupplier2$1:()Ljava/lang/String;', HexCode='0f06004c'}
    |054| CONSTANT_Utf8 {Value='Hello Lambda!', HexCode='01000d48656c6c6f204c616d62646121'}
    |055| CONSTANT_Utf8 {Value='Hello World!', HexCode='01000c48656c6c6f20576f726c6421'}

BootstrapMethods:
HexCode: 00270000002a0004002800030029002a002b00280003002d002e002f0028000300310032003300280003003100350033
attribute_name_index='0027' (39)
attribute_length='0000002a' (42)
num_bootstrap_methods='0004' (4)
|001|
bootstrap_method_ref='0028' (40)
num_bootstrap_arguments='0003' (3)
bootstrap_arguments='0029' ([41, 42, 43])
|002|
bootstrap_method_ref='002a' (40)
num_bootstrap_arguments='002b' (3)
bootstrap_arguments='0028' ([45, 46, 47])
|003|
bootstrap_method_ref='0003' (40)
num_bootstrap_arguments='002d' (3)
bootstrap_arguments='002e' ([49, 50, 51])
|004|
bootstrap_method_ref='002f' (40)
num_bootstrap_arguments='0028' (3)
bootstrap_arguments='0003' ([49, 53, 51])
```

