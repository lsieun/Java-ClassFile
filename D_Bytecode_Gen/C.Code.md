# Code

## max_locals

这是从两方面考虑：

- （1）从method的parameter（或argument）角度来进行考虑
- （2）从method的code角度进行考虑

```java
/**
* Compute maximum number of local variables.
*/
public static int computeMaxLocals(final int access_flags, final Type[] arg_types, final InstructionList il) {
    int max_locals = 0;
    if (il != null) {
        int max = AccessFlagUtils.isStatic(access_flags) ? 0 : 1;
        if (arg_types != null) {
            for (final Type arg_type : arg_types) {
                max += arg_type.getSize();
            }
        }

        for (Instruction ins = il.start; ins != null; ins = ins.next) {
            if (ins instanceof LocalVariableInstruction) {
                LocalVariableInstruction item = (LocalVariableInstruction) ins;
                int index = item.index;

                // Long或Double类型的数据要占用2个slot
                if (item.getType(null).getSize() == 2) {
                    index += 1;
                }

                if (index > max) {
                    max = index;
                }
            }
        }
        max_locals = max;
    } else {
        max_locals = 0;
    }
    return max_locals;
}
```

## max_stack

处理两件重要的事情：

- （1）每一条opcode对应的instruction对于operand stack产生的影响，即增加了几个slot或减少了几个slot。
- （2）对于代码分支的处理
  - if/switch
  - for/while
  - try...catch的处理

```java
public static int computeMaxStack(final ConstantPoolGen cpg, final InstructionList il, final CodeExceptionGen[] exception_array) {
    int max_stack = 0;

    final BranchStack branchTargets = new BranchStack();
    /* Initially, populate the branch stack with the exception
        * handlers, because these aren't (necessarily) branched to
        * explicitly. in each case, the stack will have depth 1,
        * containing the exception object.
        */
    for (final CodeExceptionGen element : exception_array) {
        final Instruction handler_pc = element.start_pc;
        if (handler_pc != null) {
            branchTargets.push(handler_pc, 1);
        }
    }

    int stackDepth = 0;
    Instruction current = il.start;
    while(current != null) {
        final short opcode = current.opcode;
        final int delta = current.produceStack(cpg) - current.consumeStack(cpg);
        stackDepth += delta;
        if (stackDepth > max_stack) {
            max_stack = stackDepth;
        }

        // choose the next instruction based on whether current is a branch.
        if (current instanceof BranchInstruction) {
            final BranchInstruction bi = (BranchInstruction) current;
            if (bi instanceof SelectInstruction) {
                // explore all of the select's targets. the default target is handled below.
                final SelectInstruction select = (SelectInstruction) bi;
                final Instruction[] targets = select.targets;
                for (final Instruction t : targets) {
                    branchTargets.push(t, stackDepth);
                }
                // nothing to fall through to.
                current = null;
            } else if (!(bi instanceof IfInstruction)) {
                // if an instruction that comes back to following PC,
                // push next instruction, with stack depth reduced by 1.
                if (opcode == OpcodeConst.JSR || opcode == OpcodeConst.JSR_W) {
                    branchTargets.push(bi.next, stackDepth - 1);
                }
                current = null;
            }
            // for all branches, the target of the branch is pushed on the branch stack.
            // conditional branches have a fall through case, selects don't, and
            // jsr/jsr_w return to the next instruction.
            branchTargets.push(bi.target, stackDepth);
        }
        else {
            // check for instructions that terminate the method.
            if (opcode == OpcodeConst.ATHROW || opcode == OpcodeConst.RET
                    || (opcode >= OpcodeConst.IRETURN && opcode <= OpcodeConst.RETURN)) {
                current = null;
            }
        }

        // normal case, go to the next instruction.
        if (current != null) {
            current = current.next;
        }
        // if we have no more instructions, see if there are any deferred branches to explore.
        if (current == null) {
            final BranchTarget bt = branchTargets.pop();
            if (bt != null) {
                current = bt.target;
                stackDepth = bt.stackDepth;
            }
        }
    }

    return max_stack;
}
```



