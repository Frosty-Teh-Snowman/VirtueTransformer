package org.virtue.bytecode.node.impl.operand;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.BasicNode;

/**
 * @author : const_
 */
public class LocalVariableCallNode extends BasicNode<VarInsnNode> {

    public LocalVariableCallNode(VarInsnNode node) {
        super(node);
    }

    public int index() {
        return node().var;
    }

    public String desc() {
        switch (opcode()) {
            case Opcodes.ILOAD:
                return "I";
            case Opcodes.ALOAD:
                return "Ljava/lang/Object;";
            case Opcodes.DLOAD:
                return "D";
            case Opcodes.FLOAD:
                return "F";
            case Opcodes.LLOAD:
                return "J";
        }
        return "null";
    }

    @Override
    public int type() {
        return AbstractNode.LOCAL_VARIABLE_CALL_NODE;
    }

}
