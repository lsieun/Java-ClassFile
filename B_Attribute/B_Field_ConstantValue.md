# ConstantValue

The `ConstantValue` attribute is a fixed-length attribute in the attributes table of a `field_info` structure.

The `ConstantValue` attribute has the following format:

```txt
ConstantValue_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 constantvalue_index;
}
```

