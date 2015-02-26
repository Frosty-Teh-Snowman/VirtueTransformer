package org.virtue.bytecode.node.impl.operand;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.BasicNode;

/**
 * @author : const_
 */
public class LocalVariableStoreNode extends BasicNode<VarInsnNode> {

    public LocalVariableStoreNode(VarInsnNode node) {
        super(node);
    }

    public int index() {
        return node().var;
    }

    public String desc() {
        switch (opcode()) {
            case Opcodes.ISTORE:
                return "I";
            case Opcodes.ASTORE:
                return "Ljava/lang/Object;";
            case Opcodes.DSTORE:
                return "D";
            case Opcodes.FSTORE:
                return "F";
            case Opcodes.LSTORE:
                return "J";
        }
        return "null";
    }

    @Override
    public int type() {
        return AbstractNode.LOCAL_VARIABLE_STORE_NODE;
    }

}
