# Deprecated

The `Deprecated` attribute is an optional<sub>【注：可选的】</sub> fixed-length<sub>【注：固定长度的】</sub> attribute in the `attributes` table of a `ClassFile`, `field_info`, or `method_info` structure. A class, interface, method, or field may be marked using a `Deprecated` attribute to indicate that the class, interface, method, or field has been superseded.

A run-time interpreter or tool that reads the `class` file format, such as a compiler, can use this marking to advise the user that a superseded class, interface, method, or field is being referred to. The presence of a `Deprecated` attribute does not alter the semantics of a class or interface.

The `Deprecated` attribute has the following format:

```txt
Deprecated_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
}
```
