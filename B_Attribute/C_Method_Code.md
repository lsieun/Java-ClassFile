# Code Attribute

The `Code` attribute is **a variable-length attribute** in the `attributes` table of a `method_info` structure.

```txt
method_info {
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;
    u2 attributes_count;
   attribute_info attributes[attributes_count];
}
```

If the method is either `native` or `abstract` , and is not **a class or interface initialization method**, then its `method_info` structure must not have a `Code` attribute in its attributes table<sub>【注：说明没有Code的情况】</sub>. Otherwise, its `method_info` structure must have **exactly one** `Code` attribute in its attributes table<sub>【注：说明两者数量关系】</sub>.

A `Code` attribute contains the Java Virtual Machine instructions and auxiliary information for a method, including an instance initialization method and a class or interface initialization method<sub>【注：这段我不懂在说什么】</sub>.

The Code attribute has the following format:

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

The items of the `Code_attribute` structure are as follows:

- `attribute_name_index：` The value of the `attribute_name_index` item must be a valid index into the `constant_pool` table<sub>【注：指向constant_pool，但是有两个约束】</sub>. The `constant_pool` entry at that index must be a `CONSTANT_Utf8_info`<sub>【注：第一个约束，“类型”上的约束】</sub> structure representing the string "Code"<sub>【注：第二个约束，“值”的约束】</sub>.
- `attribute_length`: The value of the `attribute_length` item indicates the length of the attribute, excluding **the initial six bytes**.
- `max_stack`: The value of the `max_stack` item gives **the maximum depth of the operand stack** of this method at any point during execution of the method.
- `max_locals`: The value of the `max_locals` item gives **the number of local variables** in the local variable array allocated upon invocation of this method, including the local variables used to pass parameters to the method on its invocation.
- `code_length`: The value of the `code_length` item gives **the number of bytes** in the code array for this method<sub>【注：字段含义】</sub>. The value of `code_length` must be **greater than zero** (as the code array must not be empty) and **less than `65536`**<sub>【注：字段值的有效范围】</sub>.
- `code[]`: The `code` array gives the actual bytes of Java Virtual Machine code that implement the method.
- `exception_table_length`: The value of the `exception_table_length` item gives the number of entries in the `exception_table` table.
- `exception_table[]`: Each entry in the `exception_table` array describes one exception handler in the `code` array. The order of the handlers in the `exception_table` array is significant<sub>【注：表面是说“顺序”的重要性，但是却并不明白重要在哪里】</sub>.
- `attributes_count`: The value of the `attributes_count` item indicates the number of `attributes` of the `Code` attribute.
- `attributes[]`: Each value of the `attributes` table must be an `attribute_info` structure.
