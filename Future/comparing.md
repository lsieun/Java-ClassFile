# comparing

这个方法，其实是来自于`java.util.Comparator.comparing`方法

```java
import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;

public class HelloWorld {
    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor) {
        return (Comparator<T> & Serializable)
                (c1, c2) -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
    }
}
```

Output:

```txt
methods_count='0004' (4)
methods
    MethodInfo {Value='<init>:()V', AccessFlags='[ACC_PUBLIC]', Attrs='[Code]', HexCode='...'}
    MethodInfo {Value='comparing:(Ljava/util/function/Function;)Ljava/util/Comparator;', AccessFlags='[ACC_PUBLIC,ACC_STATIC]', Attrs='[Code, MethodParameters, Signature]', HexCode='...'}
    MethodInfo {Value='$deserializeLambda$:(Ljava/lang/invoke/SerializedLambda;)Ljava/lang/Object;', AccessFlags='[ACC_PRIVATE,ACC_STATIC,ACC_SYNTHETIC]', Attrs='[Code]', HexCode='...'}
    MethodInfo {Value='lambda$comparing$77a9974f$1:(Ljava/util/function/Function;Ljava/lang/Object;Ljava/lang/Object;)I', AccessFlags='[ACC_PRIVATE,ACC_STATIC,ACC_SYNTHETIC]', Attrs='[Code, MethodParameters]', HexCode='...'}
```

我不明白的地方，是`$deserializeLambda$`这个方法。
