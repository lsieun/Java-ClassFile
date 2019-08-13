# The InnerClasses Attribute

The `InnerClasses` attribute is a variable-length attribute in the `attributes` table of a `ClassFile` structure.

If the constant pool of a class or interface `C` contains at least one `CONSTANT_Class_info` entry which represents a class or interface that is not a member of a package, then there must be exactly one `InnerClasses` attribute in the `attributes` table of the `ClassFile` structure for `C`.

The `InnerClasses` attribute has the following format:

```txt
InnerClasses_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    {
        u2 inner_class_info_index;
        u2 outer_class_info_index;
        u2 inner_name_index;
        u2 inner_class_access_flags;
    } classes[number_of_classes];
}
```


