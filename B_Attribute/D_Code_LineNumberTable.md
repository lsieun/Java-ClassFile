# LineNumberTable

The `LineNumberTable` attribute is an optional variable-length attribute in the `attributes` table of a `Code` attribute. It may be used by debuggers to determine which part of the code array corresponds to a given line number in the original source file.

If multiple `LineNumberTable` attributes are present in the `attributes` table of a `Code` attribute, then they may appear in any order.

There may be more than one `LineNumberTable` attribute **per line of a source file** in the `attributes` table of a `Code` attribute. That is, `LineNumberTable` attributes may together represent a given line of a source file, and need not be one-to-one with source lines.

The `LineNumberTable` attribute has the following format:

```txt
LineNumberTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 line_number_table_length;
    {
        u2 start_pc;
        u2 line_number;
    } line_number_table[line_number_table_length];
}
```

- `start_pc`

The value of the `start_pc` item must be a valid index into the `code` array of this `Code` attribute. The item indicates the index into the `code` array at which the code for a new line in the original source file begins.

- `line_number`

The value of the `line_number` item gives the corresponding line number in the original source file.
