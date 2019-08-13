# StackMapTable

<!-- TOC -->

- [1. Intro](#1-intro)
  - [1.1. What](#11-what)
  - [1.2. save space](#12-save-space)
- [2. Position](#2-position)
- [3. StackMapTable_attribute](#3-stackmaptableattribute)
  - [3.1. stack map frame](#31-stack-map-frame)
  - [3.2. 定性描述](#32-%e5%ae%9a%e6%80%a7%e6%8f%8f%e8%bf%b0)
  - [3.3. 定量表达](#33-%e5%ae%9a%e9%87%8f%e8%a1%a8%e8%be%be)
- [4. verification_type_info](#4-verificationtypeinfo)
  - [4.1. Top_variable_info](#41-topvariableinfo)
  - [4.2. Integer_variable_info](#42-integervariableinfo)
  - [4.3. Float_variable_info](#43-floatvariableinfo)
  - [4.4. Null_variable_info](#44-nullvariableinfo)
  - [4.5. UninitializedThis_variable_info](#45-uninitializedthisvariableinfo)
  - [4.6. Object_variable_info](#46-objectvariableinfo)
  - [4.7. Uninitialized_variable_info](#47-uninitializedvariableinfo)
  - [4.8. Long_variable_info](#48-longvariableinfo)
  - [4.9. Double_variable_info](#49-doublevariableinfo)
- [5. stack_map_frame](#5-stackmapframe)
  - [5.1. same_frame](#51-sameframe)
  - [5.2. same_locals_1_stack_item_frame](#52-samelocals1stackitemframe)
  - [5.3. reserved](#53-reserved)
  - [5.4. same_locals_1_stack_item_frame_extended](#54-samelocals1stackitemframeextended)
  - [5.5. chop_frame](#55-chopframe)
  - [5.6. same_frame_extended](#56-sameframeextended)
  - [5.7. append_frame](#57-appendframe)
  - [5.8. full_frame](#58-fullframe)
- [6. Examples](#6-examples)
  - [6.1. append_frame example](#61-appendframe-example)
  - [6.2. full_frame example](#62-fullframe-example)
- [7. Stack Map Frame Example](#7-stack-map-frame-example)
  - [7.1. example 001](#71-example-001)
  - [7.2. example 002](#72-example-002)
  - [7.3. example 003](#73-example-003)
  - [7.4. example 004](#74-example-004)
  - [7.5. example 005](#75-example-005)
  - [7.6. example 006](#76-example-006)
  - [7.7. example 007](#77-example-007)
  - [7.7. example 008](#77-example-008)
  - [7.7. example 009](#77-example-009)
  - [new Object](#new-object)
  - [array](#array)
  - [instanceof and checkcast](#instanceof-and-checkcast)
- [8. StringBuilder代码优化](#8-stringbuilder%e4%bb%a3%e7%a0%81%e4%bc%98%e5%8c%96)

<!-- /TOC -->

A `StackMapTable` attribute is used during **the process of verification** by type checking<sub>【注：讲它的作用】</sub>.

## 1. Intro

### 1.1. What

Classes compiled with Java 6 or higher contain, in addition to bytecode instructions, a set of **stack map frames** that are used to speed up **the class verification process** inside the Java Virtual Machine. **A stack map frame** gives the state of the execution frame of a method at some point during its execution. More precisely it gives **the type of the values** that are contained in **each local variable slot** and in **each operand stack slot** just before **some specific bytecode instruction** is executed.

```java
public class Bean {
    private int intValue;

    public int getValue() {
        return intValue;
    }

    public void setValue(int intValue) {
        this.intValue = intValue;
    }

    public void checkAndSetValue(int intValue) {
        if (intValue >= 0) {
            this.intValue = intValue;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
```

`getValue:()I`的输出：

```txt
code='2ab40002ac'
    0: aload_0         // 2a
    1: getfield 2      // b40002
    4: ireturn         // ac

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0       5  this:Llsieun/sample/HelloWorld;
```

`checkAndSetValue:(I)V`的输出：

```txt
code='1b9b000b2a1bb50002a7000bbb000359b70004bfb1'
    0: iload_1         // 1b
    1: iflt 11         // 9b000b
    4: aload_0         // 2a
    5: iload_1         // 1b
    6: putfield 2      // b50002
    9: goto 11         // a7000b
   12: new 3           // bb0003
   15: dup             // 59
   16: invokespecial 4 // b70004
   19: athrow          // bf
   20: return          // b1

LocalVariableTable:
index  start_pc  length  name_and_type
    0         0      21  this:Llsieun/sample/HelloWorld;
    1         0      21  intValue:I
```

For example, if we consider the `getValue` method, we can define three stack map frames giving the state of the execution frame just before `ALOAD`, just before `GETFIELD`, and just before `IRETURN`. These three stack map frames can be described as follows, where **the types** between the **first square brackets** correspond to **the local variables**, and **the others** to **the operand stack**:

```txt
State of the execution frame before     Instruction
[pkg/Bean] []                           ALOAD 0
[pkg/Bean] [pkg/Bean]                   GETFIELD
[pkg/Bean] [I]                          IRETURN
```

We can do the same for the `checkAndSetF` method:

```txt
State of the execution frame before                            Instruction
[pkg/Bean I] []                                                ILOAD 1
[pkg/Bean I] [I]                                               IFLT label
[pkg/Bean I] []                                                ALOAD 0
[pkg/Bean I] [pkg/Bean]                                        ILOAD 1
[pkg/Bean I] [pkg/Bean I]                                      PUTFIELD
[pkg/Bean I] []                                                GOTO end
[pkg/Bean I] []                                                label :
[pkg/Bean I] []                                                NEW
[pkg/Bean I] [Uninitialized(label)]                            DUP
[pkg/Bean I] [Uninitialized(label) Uninitialized(label)]       INVOKESPECIAL
[pkg/Bean I] [java/lang/IllegalArgumentException]              ATHROW
[pkg/Bean I] []                                                end :
[pkg/Bean I] []                                                RETURN
```

This is similar to the previous method, except for the `Uninitialized(label)` type. This is a special type that is **used only in stack map frames**<sub>【注：说明使用场景】</sub>, and that designates **an object whose memory has been allocated but whose constructor has not been called yet**<sub>【注：说明对象的状态】</sub>. The argument designates the instruction that created this object<sub>【注：这句我没有看懂，是说要创建某个类吗？】</sub>. The only possible method that can be called on a value of this type is a constructor<sub>【注：这样一个对象，可以调用的方法】</sub>. When it is called, all the occurrences of this type in the frame are replaced with the real type, here `IllegalArgumentException`. Stack map frames can use **three other special types**: `UNINITIALIZED_THIS` is the initial type of local variable `0` in constructors, `TOP` corresponds to an undefined value, and `NULL` corresponds to `null`.

### 1.2. save space

As said above, starting from Java 6, compiled classes contain, in addition to bytecode, a set of stack map frames. **In order to save space, a compiled method does not contain one frame per instruction**: in fact it contains **only the frames for the instructions that correspond to jump targets or exception handlers**, or **that follow unconditional jump instructions**<sub>【注：这里是讲stack map frame记录的条件】</sub>. Indeed the other frames can be easily and quickly inferred from these ones<sub>【注：其他情况的stack map frame是可以推衍出来的】</sub>.

- frame
  - the instructions that correspond to jump targets
    - IFXXX (conditional jump)
    - GOTO (unconditional jump)
  - instructions that correspond to exception handlers
    - exception table
  - the instructions that follow unconditional jump instructions
    - GOTO
    - ATHROW

In the case of the `checkAndSetF` method, this means that **only two frames** are stored: one for the `NEW` instruction, because it is the target of the `IFLT` instruction, but also because it follows the unconditional jump `GOTO` instruction, and  one for the `RETURN` instruction, because it is the target of the `GOTO` instruction, and also because it follows the “unconditional jump” `ATHROW` instruction.

In order to **save even more space**<sub>【注：为了节省更多的空间，采取下面两个措施】</sub>, **each frame is compressed by storing only its difference compared to the previous frame**<sub>【注：第一个措施，只存储“差异”部分】</sub>, and **the initial frame is not stored at all**<sub>【注：第二个措施，最开始的frame不进行存储，因为它可以从method parameter中推衍出来】</sub>, because it can easily be deduced from the method parameter types. In the case of the `checkAndSetF` method the **two frames** that must be stored are equal and are equal to the **initial frame**, so they are stored as the single byte value designated by the `SAME` mnemonic.

```txt
StackMapTable:
    SAME {offset_delta=12}
    SAME {offset_delta=7}
}
```

## 2. Position

The `StackMapTable` attribute is a variable-length attribute in the `attributes` table of a `Code` attribute<sub>【注：讲它的位置】</sub>.

> Position: Code_attribute-->attributes-->StackMapTable

```txt
Code_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 max_stack;
    u2 max_locals;
    u4 code_length;
    u1 code[code_length];
    u2 exception_table_length;
    {
        u2 start_pc;
        u2 end_pc;
        u2 handler_pc;
        u2 catch_type;
    } exception_table[exception_table_length];
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}
```

There may be **at most one** `StackMapTable` attribute in the `attributes` table of a `Code` attribute<sub>【注：讲它的数量】</sub>.

> 数量对比：Code_attribute-->1:1-->StackMapTable

## 3. StackMapTable_attribute

The `StackMapTable` attribute has the following format:

```txt
StackMapTable_attribute {
    u2              attribute_name_index;
    u4              attribute_length;
    u2              number_of_entries;
    stack_map_frame entries[number_of_entries];
}
```

### 3.1. stack map frame

A **stack map frame** specifies (either explicitly or implicitly) **the bytecode offset** at which it applies, and **the verification types** of **local variables** and **operand stack entries** for that offset.

- stack map frame
  - the bytecode offset
  - the verification types
    - local variables
    - operand stack entries

### 3.2. 定性描述

**Each stack map frame** described in the `entries` table relies on **the previous frame** for some of its semantics<sub>【注：】一种“向前依赖”的关系</sub>. **The first stack map frame** of a method is implicit, and computed from **the method descriptor** by the type checker<sub>【注：讲述第一个stack map frame】</sub>. The `stack_map_frame` structure at `entries[0]` therefore describes **the second stack map frame** of the method<sub>【注：讲述第二个stack map frame】</sub>.

### 3.3. 定量表达

The **bytecode offset** at which a stack map frame applies is calculated by taking the value `offset_delta` specified in the frame (either explicitly or implicitly), and adding `offset_delta + 1` to the **bytecode offset of the previous frame**<sub>【注：常规情况、其计算方式】</sub>, unless the previous frame is **the initial frame** of the method<sub>【注：特殊情况】</sub>. In that case, **the bytecode offset** at which the stack map frame applies is the value `offset_delta` specified in the frame<sub>【注：特殊情况的计算方式】</sub>.

## 4. verification_type_info

We say that **an instruction in the bytecode** has a corresponding **stack map frame**<sub>【注：先说结果，再说条件，if后面有两个条件】</sub> if the instruction starts at offset `i` in the `code` array of a `Code` attribute<sub>【注：第一个条件，在offset为i的地方有一个instruction】</sub>, and the `Code` attribute has a `StackMapTable` attribute whose `entries` array contains a stack map frame that applies at bytecode offset `i`<sub>【注：第二个条件，在offset为i的地方有一个stack map frame】</sub>.

A **verification type** specifies **the type of either one or two locations**, where **a location** is either **a single local variable** or **a single operand stack entry**. A **verification type** is represented by a discriminated union, `verification_type_info` , that consists of **a one-byte tag**, indicating which item of the union is in use, followed by zero or more bytes, giving more information about the `tag`.

```txt
union verification_type_info {
    Top_variable_info;
    Integer_variable_info;
    Float_variable_info;
    Long_variable_info;
    Double_variable_info;
    Null_variable_info;
    UninitializedThis_variable_info;
    Object_variable_info;
    Uninitialized_variable_info;
}
```

A **verification type** that specifies one location in the **local variable array** or in the **operand stack** is represented by the following items of the `verification_type_info` union:

### 4.1. Top_variable_info

- The `Top_variable_info` item indicates that the **local variable** has the verification type `top`.

```txt
Top_variable_info {
    u1 tag = ITEM_Top; /* 0 */
}
```

### 4.2. Integer_variable_info

- The `Integer_variable_info` item indicates that the location has the verification type `int`.

```txt
Integer_variable_info {
    u1 tag = ITEM_Integer; /* 1 */
}
```

### 4.3. Float_variable_info

- The `Float_variable_info` item indicates that the location has the verification type `float`.

```txt
Float_variable_info {
    u1 tag = ITEM_Float; /* 2 */
}
```

### 4.4. Null_variable_info

- The `Null_variable_info` type indicates that the location has the verification type `null`.

```txt
Null_variable_info {
    u1 tag = ITEM_Null; /* 5 */
}
```

### 4.5. UninitializedThis_variable_info

- The `UninitializedThis_variable_info` item indicates that the location has the verification type `uninitializedThis`.

```txt
UninitializedThis_variable_info {
    u1 tag = ITEM_UninitializedThis; /* 6 */
}
```

### 4.6. Object_variable_info

- The `Object_variable_info` item indicates that the location has the verification type which is the class represented by the `CONSTANT_Class_info` structure found in the `constant_pool` table at the index given by `cpool_index`.

```txt
Object_variable_info {
    u1 tag = ITEM_Object; /* 7 */
    u2 cpool_index;
}
```

### 4.7. Uninitialized_variable_info

- The `Uninitialized_variable_info` item indicates that the location has the verification type `uninitialized(Offset)`. The `Offset` item indicates the offset<sub>【注：飞机起飞】</sub>, in the `code` array of the `Code` attribute that contains this `StackMapTable` attribute,<sub>【注：飞机降落】</sub> of the `new` instruction that created the object being stored in the location.

```txt
Uninitialized_variable_info {
    u1 tag = ITEM_Uninitialized; /* 8 */
    u2 offset;
}
```

A **verification type** that specifies **two locations** in the **local variable array** or in the **operand stack** is represented by the following items of the `verification_type_info` union:

### 4.8. Long_variable_info

- The `Long_variable_info` item indicates that **the first of two locations** has the verification type `long`.

```txt
Long_variable_info {
    u1 tag = ITEM_Long; /* 4 */
}
```

### 4.9. Double_variable_info

- The `Double_variable_info` item indicates that **the first of two locations** has the verification type `double`.

```txt
Double_variable_info {
    u1 tag = ITEM_Double; /* 3 */
}
```

## 5. stack_map_frame

A **stack map frame** is represented by a discriminated union, `stack_map_frame`, which consists of a **one-byte tag**, indicating which item of the union is in use, followed by zero or more bytes, giving more information about the tag.

```txt
union stack_map_frame {
    same_frame;
    same_locals_1_stack_item_frame;
    same_locals_1_stack_item_frame_extended;
    chop_frame;
    same_frame_extended;
    append_frame;
    full_frame;
}
```

The **tag** indicates the **frame type** of the stack map frame:

### 5.1. same_frame

- The frame type `same_frame` is represented by tags in the range `[0-63]`<sub>【注：取值范围】</sub>. This frame type indicates that **the frame** has exactly **the same local variables** as **the previous frame** and that **the operand stack is empty**. The `offset_delta` value for the frame is the value of the tag item, `frame_type`.

```txt
same_frame {
    u1 frame_type = SAME; /* 0-63 */
}
```

### 5.2. same_locals_1_stack_item_frame

- The frame type `same_locals_1_stack_item_frame` is represented by tags in the range `[64, 127]`. This frame type indicates that **the frame** has exactly **the same local variables** as **the previous frame** and that **the operand stack has one entry**. The `offset_delta` value for the frame is given by the formula `frame_type - 64`. The **verification type** of the one stack entry appears after the frame type.

```txt
same_locals_1_stack_item_frame {
    u1 frame_type = SAME_LOCALS_1_STACK_ITEM; /* 64-127 */
    verification_type_info stack[1];
}
```

### 5.3. reserved

- Tags in the range `[128-246]` are reserved for future use.

### 5.4. same_locals_1_stack_item_frame_extended

- The frame type `same_locals_1_stack_item_frame_extended` is represented by the tag `247`. This frame type indicates that **the frame** has exactly **the same local variables** as **the previous frame** and that **the operand stack has one entry**. The `offset_delta` value for the frame is given explicitly, unlike in the frame type `same_locals_1_stack_item_frame` . The verification type of the one stack entry appears after `offset_delta`.

```txt
same_locals_1_stack_item_frame_extended {
    u1 frame_type = SAME_LOCALS_1_STACK_ITEM_EXTENDED; /* 247 */
    u2 offset_delta;
    verification_type_info stack[1];
}
```

### 5.5. chop_frame

The frame type `chop_frame` is represented by tags in the range `[248-250]`. This frame type indicates that **the frame** has **the same local variables** as **the previous frame** except that the last `k` local variables are absent, and that **the operand stack is empty**. The value of `k` is given by the formula `251 - frame_type`. The `offset_delta` value for the frame is given explicitly.

```txt
chop_frame {
    u1 frame_type = CHOP; /* 248-250 */
    u2 offset_delta;
}
```

### 5.6. same_frame_extended

- The frame type `same_frame_extended` is represented by the tag `251`. This frame type indicates that **the frame** has exactly **the same local variables** as **the previous frame** and that **the operand stack is empty**. The `offset_delta` value for the frame is given explicitly, unlike in the frame type `same_frame`.

```txt
same_frame_extended {
    u1 frame_type = SAME_FRAME_EXTENDED; /* 251 */
    u2 offset_delta;
}
```

### 5.7. append_frame

- The frame type `append_frame` is represented by tags in the range `[252-254]`. This frame type indicates that **the frame** has **the same locals** as **the previous frame** except that `k` additional locals are defined, and that **the operand stack is empty**. The value of `k` is given by the formula `frame_type - 251`. The `offset_delta` value for the frame is given explicitly.

```txt
append_frame {
    u1 frame_type = APPEND; /* 252-254 */
    u2 offset_delta;
    verification_type_info locals[frame_type - 251];
}
```

### 5.8. full_frame

- The frame type `full_frame` is represented by the tag `255`. The `offset_delta` value for the frame is given explicitly.

```txt
full_frame {
    u1 frame_type = FULL_FRAME; /* 255 */
    u2 offset_delta;
    u2 number_of_locals;
    verification_type_info locals[number_of_locals];
    u2 number_of_stack_items;
    verification_type_info stack[number_of_stack_items];
}
```

## 6. Examples

### 6.1. append_frame example

```java
public class HelloWorld {
    public static void testSimple() {
        boolean flag = true;
        int a = 0;
        Object obj = null;
        if(flag) {
            a = 1;
        }
        else {
            a = 2;
        }
    }
}
```

### 6.2. full_frame example

```java
public class HelloWorld {
    public static void testSimple() {
        boolean flag = true;
        int a = 0;
        double d = 0;
        Object obj = null;
        if(flag) {
            a = 1;
        }
        else {
            a = 2;
        }
    }
}
```

查看stack map的示例

```java
System.out.println(i);
```

```java
System.out.printf("%s\n", "World", "Hello");
```

## 7. Stack Map Frame Example

### 7.1. example 001

```java
public void testSimple() {
    int a = 1;
    int b = 2;
    int c = a + b;
}
```

### 7.2. example 002

```java
public void testSimple() {
    System.out.println(100);
}
```

### 7.3. example 003

```java
public void testSimple() {
    int max = Math.max(5, 15);
}
```

### 7.4. example 004

```java
public void testSimple() {
    System.out.printf("%s\n", "World", "Hello");
}
```

### 7.5. example 005

```java
public void testSimple() {
    Map map = new HashMap();
    map.put("key", "value");
}
```

### 7.6. example 006

```java
public void testSimple() {
    String[] array = new String[2];
    array[0] = array[1] = "Hello";
}
```

### 7.7. example 007

- ifeq
- goto

```java
public void testSimple() {
    boolean flag = true;
    int i = 0;
    if(flag) {
        i = 1;
    }
    else {
        i = 2;
    }
    System.out.println(i);
}
```

### 7.7. example 008

```java
public void testSimple() {
    int value = 1;
    int result = 0;

    switch (value) {
        case 10:
            result = 1;
            break;
        case 20:
            result = 2;
            break;
        case 30:
            result = 3;
            break;
        default:
            result = 4;
    }
}
```

### 7.7. example 009

```java
public void testSimple() {
    int value = 1;
    int result = 0;

    switch (value) {
        case 10:
            result = 1;
            break;
        case 20:
            result = 2;
            break;
        case 30:
            result = 3;
            break;
        default:
            result = 4;
    }
}
```

### new Object

不带参数的构造方法

```java
String str = new String();
```

带参数的构造方法

```java
String str = new String("Hello");
```

### array

- newarray

```java
int[] array = new int[5];
```

- anewarray

```java
String[] array = new String[5];
```

- multianewarray

```java
// 基本类型的多维数组
int[][] array = new int[5][6];
// 对象类型的多维数组
String[][] array = new String[5][6];
```

### instanceof and checkcast

- instanceof

```java
String str = "Hello";
boolean flag = str instanceof Object;
```

- checkcast

```java
Object obj = new Object();
String str = (String) obj;
```

## 8. StringBuilder代码优化

优化前：29个instruction

```java
public void testSimple() {
    StringBuilder sb = new StringBuilder();
    sb.append("Hello");
    sb.append("World");
    System.out.println(sb);
}
```

优化后：27个instruction

```java
public void testSimple() {
    StringBuilder sb = new StringBuilder();
    sb.append("Hello").append("World");
    System.out.println(sb);
}
```

