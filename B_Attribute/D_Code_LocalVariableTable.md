# LocalVariableTable

## 外部介绍

The `LocalVariableTable` attribute is an optional variable-length attribute<sub>【注：变长的属性】</sub> in the `attributes` table of a `Code` attribute<sub>【注：位于Code属性下】</sub>. It may be used by debuggers<sub>【注：用途，用于调试】</sub> to determine the value of a given local variable during the execution of a method.

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

```txt
LocalVariableTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 local_variable_table_length;
    {
        u2 start_pc;
        u2 length;
        u2 name_index;
        u2 descriptor_index;
        u2 index;
    } local_variable_table[local_variable_table_length];
}
```

If multiple<sub>【注：多个】</sub> `LocalVariableTable` attributes are present in the `attributes` table of a `Code` attribute<sub>【注：位于Code属性之下】</sub>, then they may appear in any order<sub>【注：顺序是不固定的】</sub>.

There may be no more than one<sub>【注：只有一个】</sub> `LocalVariableTable` attribute **per local variable**<sub>【注：对于每个局部变量来说】</sub> in the `attributes` table of a `Code` attribute.

## 内部解释（参数解释）

The `LocalVariableTable` attribute has the following format:

```txt
LocalVariableTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 local_variable_table_length;
    {
        u2 start_pc;
        u2 length;
        u2 name_index;
        u2 descriptor_index;
        u2 index;
    } local_variable_table[local_variable_table_length];
}
```

- `start_pc, length`

The value of the `start_pc`<sub>【注：起始的索引】</sub> item must be a valid index into the `code` array of this `Code` attribute and must be the index of the opcode of an instruction.

The value of `start_pc + length`<sub>【注：结束的索引】</sub> must either be a valid index into the `code` array of this `Code` attribute and be the index of the opcode of an instruction, or it must be the first index beyond the end of that `code` array.

The `start_pc` and `length` items indicate that the given local variable has a value at indices into the `code` array in the interval `[ start_pc, start_pc + length)`<sub>【注：一个区间范围】</sub>, that is, between `start_pc` inclusive and `start_pc + length` exclusive.

- `name_index`

The value of the `name_index` item must be a valid index into the `constant_pool` table. The `constant_pool` entry at that index must contain a `CONSTANT_Utf8_info` structure representing a valid unqualified name denoting a local variable.

- `descriptor_index`

The value of the `descriptor_index` item must be a valid index into the `constant_pool` table. The `constant_pool` entry at that index must contain a `CONSTANT_Utf8_info` structure representing a field descriptor which encodes the type of a local variable in the source program.

- `index`

The value of the `index` item must be a valid index into the **local variable array** of **the current frame**. The given local variable is at `index` in the local variable array of the current frame.

If the given local variable is of type `double` or `long`, it occupies both `index` and `index + 1`.
