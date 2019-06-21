# ConstantPool

## Constant分层

```txt
FieldRef/MethodRef/InterfaceMethodRef
-----------------------------------------
String/Class/NameAndType
-----------------------------------------
Utf8/Integer/Float/Long/Double
```

## 示例代码

```java
public class ConstantPoolGen {
    private static final String METHODREF_DELIM = ":";
    private static final String IMETHODREF_DELIM = "#"; // Interface Method
    private static final String FIELDREF_DELIM = "&";
    private static final String NAT_DELIM = "%"; // Name and Type

    private static final int DEFAULT_BUFFER_SIZE = 256;

    private int size;

    private Constant[] constants;

    private int index = 1; // First entry (0) used by JVM

    private static class Index {
        final int index;

        Index(final int i) {
            index = i;
        }
    }

    private final Map<String, Index> string_table = new HashMap();
    private final Map<String, Index> class_table = new HashMap();
    private final Map<String, Index> n_a_t_table = new HashMap();
    private final Map<String, Index> utf8_table = new HashMap();
    private final Map<String, Index> cp_table = new HashMap();

    /**
     * Create empty constant pool.
     */
    public ConstantPoolGen() {
        size = DEFAULT_BUFFER_SIZE;
        constants = new Constant[size];
    }

    /**
     * @return current size of constant pool
     */
    public int getSize() {
        return index;
    }

    /**
     * @param i index in constant pool
     * @return constant pool entry at index i
     */
    public Constant getConstant(final int i) {
        return constants[i];
    }

    /**
     * Use with care!
     *
     * @param i index in constant pool
     * @param c new constant pool entry at index i
     */
    public void setConstant(final int i, final Constant c) {
        constants[i] = c;
    }

    /**
     * Resize internal array of constants.
     */
    protected void adjustSize() {
        if (index + 3 >= size) {
            final Constant[] cs = constants;
            size *= 2;
            constants = new Constant[size];
            System.arraycopy(cs, 0, constants, 0, index);
        }
    }

    // region lookup and add

    /**
     * Look for ConstantUtf8 in ConstantPool.
     *
     * @param key Utf8 string to look for
     * @return index on success, -1 otherwise
     */
    public int lookupUtf8(final String key) {
        final Index index = utf8_table.get(key);
        return (index != null) ? index.index : -1;
    }

    /**
     * Add a new Utf8 constant to the ConstantPool, if it is not already in there.
     *
     * @param str Utf8 string to add
     * @return index of entry
     */
    public int addUtf8(final String str) {
        int ret;
        if ((ret = lookupUtf8(str)) != -1) {
            return ret; // Already in CP
        }
        adjustSize();
        ret = index;
        constants[index++] = new ConstantUtf8(str);
        if (!utf8_table.containsKey(str)) {
            utf8_table.put(str, new Index(ret));
        }
        return ret;
    }

    /**
     * Look for ConstantInteger in ConstantPool.
     *
     * @param n integer number to look for
     * @return index on success, -1 otherwise
     */
    public int lookupInteger(final int n) {
        for (int i = 1; i < index; i++) {
            if (constants[i] instanceof ConstantInteger) {
                final ConstantInteger c = (ConstantInteger) constants[i];
                if (c.intValue == n) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Add a new Integer constant to the ConstantPool, if it is not already in there.
     *
     * @param n integer number to add
     * @return index of entry
     */
    public int addInteger(final int n) {
        int ret;
        if ((ret = lookupInteger(n)) != -1) {
            return ret; // Already in CP
        }
        adjustSize();
        ret = index;
        constants[index++] = new ConstantInteger(n);
        return ret;
    }

    /**
     * Look for ConstantFloat in ConstantPool.
     *
     * @param n Float number to look for
     * @return index on success, -1 otherwise
     */
    public int lookupFloat(final float n) {
        final int bits = Float.floatToIntBits(n);
        for (int i = 1; i < index; i++) {
            if (constants[i] instanceof ConstantFloat) {
                final ConstantFloat c = (ConstantFloat) constants[i];
                if (Float.floatToIntBits(c.floatValue) == bits) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Add a new Float constant to the ConstantPool, if it is not already in there.
     *
     * @param n Float number to add
     * @return index of entry
     */
    public int addFloat(final float n) {
        int ret;
        if ((ret = lookupFloat(n)) != -1) {
            return ret; // Already in CP
        }
        adjustSize();
        ret = index;
        constants[index++] = new ConstantFloat(n);
        return ret;
    }

    /**
     * Look for ConstantLong in ConstantPool.
     *
     * @param n Long number to look for
     * @return index on success, -1 otherwise
     */
    public int lookupLong(final long n) {
        for (int i = 1; i < index; i++) {
            if (constants[i] instanceof ConstantLong) {
                final ConstantLong c = (ConstantLong) constants[i];
                if (c.longValue == n) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Add a new long constant to the ConstantPool, if it is not already in there.
     *
     * @param n Long number to add
     * @return index of entry
     */
    public int addLong(final long n) {
        int ret;
        if ((ret = lookupLong(n)) != -1) {
            return ret; // Already in CP
        }
        adjustSize();
        ret = index;
        constants[index] = new ConstantLong(n);
        index += 2; // Wastes one entry according to spec
        return ret;
    }

    /**
     * Look for ConstantDouble in ConstantPool.
     *
     * @param n Double number to look for
     * @return index on success, -1 otherwise
     */
    public int lookupDouble(final double n) {
        final long bits = Double.doubleToLongBits(n);
        for (int i = 1; i < index; i++) {
            if (constants[i] instanceof ConstantDouble) {
                final ConstantDouble c = (ConstantDouble) constants[i];
                if (Double.doubleToLongBits(c.doubleValue) == bits) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Add a new double constant to the ConstantPool, if it is not already in there.
     *
     * @param n Double number to add
     * @return index of entry
     */
    public int addDouble(final double n) {
        int ret;
        if ((ret = lookupDouble(n)) != -1) {
            return ret; // Already in CP
        }
        adjustSize();
        ret = index;
        constants[index] = new ConstantDouble(n);
        index += 2; // Wastes one entry according to spec
        return ret;
    }

    /**
     * Look for ConstantClass in ConstantPool named `str'.
     *
     * @param internal_name String to search for
     * @return index on success, -1 otherwise
     */
    public int lookupClass(final String internal_name) {
        final Index index = class_table.get(internal_name.replace('.', '/'));
        return (index != null) ? index.index : -1;
    }

    /**
     * Add a new Class reference to the ConstantPool, if it is not already in there.
     *
     * @param class_name Class to add
     * @return index of entry
     */
    public int addClass(final String class_name) {
        final String internal_name = class_name.replace('.', '/');
        int ret;
        if ((ret = lookupClass(internal_name)) != -1) {
            return ret; // Already in CP
        }
        adjustSize();
        final ConstantClass c = new ConstantClass(addUtf8(internal_name));
        ret = index;
        constants[index++] = c;
        if (!class_table.containsKey(internal_name)) {
            class_table.put(internal_name, new Index(ret));
        }
        return ret;
    }

    /**
     * Look for ConstantString in ConstantPool containing String `str'.
     *
     * @param str String to search for
     * @return index on success, -1 otherwise
     */
    public int lookupString(final String str) {
        final Index index = string_table.get(str);
        return (index != null) ? index.index : -1;
    }

    /**
     * Add a new String constant to the ConstantPool, if it is not already in there.
     *
     * @param str String to add
     * @return index of entry
     */
    public int addString(final String str) {
        int ret;
        if ((ret = lookupString(str)) != -1) {
            return ret; // Already in CP
        }
        final int utf8 = addUtf8(str);
        adjustSize();
        final ConstantString s = new ConstantString(utf8);
        ret = index;
        constants[index++] = s;
        if (!string_table.containsKey(str)) {
            string_table.put(str, new Index(ret));
        }
        return ret;
    }

    /**
     * Look for ConstantNameAndType in ConstantPool.
     *
     * @param name      of variable/method
     * @param signature of variable/method
     * @return index on success, -1 otherwise
     */
    public int lookupNameAndType(final String name, final String signature) {
        final Index _index = n_a_t_table.get(name + NAT_DELIM + signature);
        return (_index != null) ? _index.index : -1;
    }

    /**
     * Add a new NameAndType constant to the ConstantPool if it is not already
     * in there.
     *
     * @param name      Name string to add
     * @param signature signature string to add
     * @return index of entry
     */
    public int addNameAndType(final String name, final String signature) {
        int ret = lookupNameAndType(name, signature);
        if (ret != -1) {
            return ret; // Already in CP
        }

        adjustSize();

        int name_index = addUtf8(name);
        int signature_index = addUtf8(signature);
        ret = index;
        constants[index++] = new ConstantNameAndType(name_index, signature_index);

        final String key = name + NAT_DELIM + signature;
        if (!n_a_t_table.containsKey(key)) {
            n_a_t_table.put(key, new Index(ret));
        }
        return ret;
    }

    /**
     * Look for ConstantFieldref in ConstantPool.
     *
     * @param class_name Where to find method
     * @param field_name Guess what
     * @param signature  return and argument types
     * @return index on success, -1 otherwise
     */
    public int lookupFieldref(final String class_name, final String field_name, final String signature) {
        final Index index = cp_table.get(class_name + FIELDREF_DELIM + field_name + FIELDREF_DELIM + signature);
        return (index != null) ? index.index : -1;
    }

    /**
     * Add a new Fieldref constant to the ConstantPool, if it is not already
     * in there.
     *
     * @param class_name class name string to add
     * @param field_name field name string to add
     * @param signature  signature string to add
     * @return index of entry
     */
    public int addFieldref(final String class_name, final String field_name, final String signature) {
        int ret;
        int class_index;
        int name_and_type_index;
        if ((ret = lookupFieldref(class_name, field_name, signature)) != -1) {
            return ret; // Already in CP
        }
        adjustSize();
        class_index = addClass(class_name);
        name_and_type_index = addNameAndType(field_name, signature);
        ret = index;
        constants[index++] = new ConstantFieldref(class_index, name_and_type_index);

        final String key = class_name + FIELDREF_DELIM + field_name + FIELDREF_DELIM + signature;
        if (!cp_table.containsKey(key)) {
            cp_table.put(key, new Index(ret));
        }
        return ret;
    }

    /**
     * Look for ConstantMethodref in ConstantPool.
     *
     * @param class_name  Where to find method
     * @param method_name Guess what
     * @param signature   return and argument types
     * @return index on success, -1 otherwise
     */
    public int lookupMethodref(final String class_name, final String method_name, final String signature) {
        final Index index = cp_table.get(class_name + METHODREF_DELIM + method_name + METHODREF_DELIM + signature);
        return (index != null) ? index.index : -1;
    }

    /**
     * Add a new Methodref constant to the ConstantPool, if it is not already
     * in there.
     *
     * @param class_name  class name string to add
     * @param method_name method name string to add
     * @param signature   method signature string to add
     * @return index of entry
     */
    public int addMethodref(final String class_name, final String method_name, final String signature) {
        int ret;
        int class_index;
        int name_and_type_index;
        if ((ret = lookupMethodref(class_name, method_name, signature)) != -1) {
            return ret; // Already in CP
        }
        adjustSize();
        name_and_type_index = addNameAndType(method_name, signature);
        class_index = addClass(class_name);
        ret = index;
        constants[index++] = new ConstantMethodref(class_index, name_and_type_index);
        final String key = class_name + METHODREF_DELIM + method_name + METHODREF_DELIM + signature;
        if (!cp_table.containsKey(key)) {
            cp_table.put(key, new Index(ret));
        }
        return ret;
    }

    /**
     * Look for ConstantInterfaceMethodref in ConstantPool.
     *
     * @param class_name  Where to find method
     * @param method_name Guess what
     * @param signature   return and argument types
     * @return index on success, -1 otherwise
     */
    public int lookupInterfaceMethodref(final String class_name, final String method_name, final String signature) {
        final Index index = cp_table.get(class_name + IMETHODREF_DELIM + method_name + IMETHODREF_DELIM + signature);
        return (index != null) ? index.index : -1;
    }

    /**
     * Add a new InterfaceMethodref constant to the ConstantPool, if it is not already
     * in there.
     *
     * @param class_name  class name string to add
     * @param method_name method name string to add
     * @param signature   signature string to add
     * @return index of entry
     */
    public int addInterfaceMethodref(final String class_name, final String method_name, final String signature) {
        int ret;
        int class_index;
        int name_and_type_index;
        if ((ret = lookupInterfaceMethodref(class_name, method_name, signature)) != -1) {
            return ret; // Already in CP
        }
        adjustSize();
        class_index = addClass(class_name);
        name_and_type_index = addNameAndType(method_name, signature);
        ret = index;
        constants[index++] = new ConstantInterfaceMethodref(class_index, name_and_type_index);
        final String key = class_name + IMETHODREF_DELIM + method_name + IMETHODREF_DELIM + signature;
        if (!cp_table.containsKey(key)) {
            cp_table.put(key, new Index(ret));
        }
        return ret;
    }
    // endregion

    // region constant pool
    /**
     * @return intermediate constant pool
     */
    public ConstantPool getConstantPool() {
        return new ConstantPool(constants);
    }

    /**
     * @return constant pool with proper length
     */
    public ConstantPool getFinalConstantPool() {
        final Constant[] cs = new Constant[index];
        System.arraycopy(constants, 0, cs, 0, index);
        return new ConstantPool(cs);
    }
    // endregion

}
```

