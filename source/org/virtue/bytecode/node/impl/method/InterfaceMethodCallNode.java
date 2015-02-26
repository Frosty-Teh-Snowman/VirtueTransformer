package org.virtue.bytecode.node.impl.method;

import org.objectweb.asm.tree.MethodInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class InterfaceMethodCallNode extends AbstractMethodCallNode{

    public InterfaceMethodCallNode(MethodInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.INTERFACE_METHOD_CALL_NODE;
    }
}
