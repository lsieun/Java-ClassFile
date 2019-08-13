# LocalVariableTypeTable

## 两者的相同之处

The `LocalVariableTypeTable` attribute is an optional variable-length attribute in the `attributes` table of a `Code` attribute. It may be used by debuggers to determine the value of a given local variable during the execution of a method.

If multiple `LocalVariableTypeTable` attributes are present in the `attributes` table of a given `Code` attribute, then they may appear in any order.

There may be no more than one `LocalVariableTypeTable` attribute **per local variable** in the `attributes` table of a `Code` attribute.

## 两者的不同之处

The `LocalVariableTypeTable` attribute differs from the `LocalVariableTable` attribute in that it provides **signature information**<sub>【注：“签名”信息】</sub> rather than **descriptor information**<sub>【注：“描述符”信息】</sub>. This difference is only significant for variables whose type uses **a type variable** or **parameterized type**<sub>【注：泛型】</sub>. Such variables will appear in both tables, while variables of other types will appear only in `LocalVariableTable`.

## 内部信息

The `LocalVariableTypeTable` attribute has the following format:

```txt
LocalVariableTypeTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 local_variable_type_table_length;
    {
        u2 start_pc;
        u2 length;
        u2 name_index;
        u2 signature_index;
        u2 index;
    } local_variable_type_table[local_variable_type_table_length];
}
```

- `signature_index`

The value of the `signature_index` item must be a valid index into the `constant_pool` table. The `constant_pool` entry at that index must contain a `CONSTANT_Utf8_info` structure representing a field signature which encodes the type of a local variable in the source program.
