# Java-ClassFile

:bouquet:

必须写一个程序，让人自己能够读取`.class`文件，然后解析内容。

TODO

- 分支：读取CAFEBABE，读取version信息
- 分支：读取常量池
- 分支：读取类信息
- 分支：读取Field信息
- 分支：读取Method信息
- 分支：读取属性信息

## 问题

```java
public class HelloWorld {
    public void testSimple() {
        List<String> list = new ArrayList();
        for(final String item : list) {
            // item = "abc"; 这里是不对的：被final修饰的item是不能再赋值的，但是final是如何在ClassFile当中实现的呢？
            System.out.println(item);
        }
    }

public static int getMaxStack(final ConstantPoolGen cp, final InstructionList il, final CodeExceptionGen[] et ) {
        final BranchStack branchTargets = new BranchStack();
}
}
```

