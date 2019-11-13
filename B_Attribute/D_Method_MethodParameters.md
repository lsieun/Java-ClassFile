# The MethodParameters Attribute

The `MethodParameters` attribute is **a variable-length attribute** in the `attributes` table of a `method_info` structure.<sub>【注：说明两者的数量关系，至多有一个MethodParameters属性】</sub>

```txt
method_info {
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;
    u2 attributes_count;
   attribute_info attributes[attributes_count];
}
```

A `MethodParameters` attribute records information about the **formal parameters** of a method, such as their names.

There may be at most one `MethodParameters` attribute in the `attributes` table of a `method_info` structure.<sub>【注：说明两者的数量关系，至多有一个MethodParameters属性】</sub>

The `MethodParameters` attribute has the following format:

```txt
MethodParameters_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u1 parameters_count;
    {
        u2 name_index;
        u2 access_flags;
    } parameters[parameters_count];
}
```

The value of the `access_flags` item is as follows:

- `0x0010 (ACC_FINAL)`: Indicates that the formal parameter was declared `final`.
- `0x1000 (ACC_SYNTHETIC)`: Indicates that the formal parameter was not explicitly or implicitly declared in source code.
- `0x8000 (ACC_MANDATED)`: Indicates that the formal parameter was implicitly declared in source code.

## How to Generate

在`.class`文件中，为了生成Method的`MethodParameters`属性，需要满足这两个条件：

- （1）在使用javac时，使用`-parameters`参数
- （2）方法本身，至少要有一个参数；如果方法没有参数，也不会生成`MethodParameters`属性

