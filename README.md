# Java-ClassFile

:bouquet:

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

