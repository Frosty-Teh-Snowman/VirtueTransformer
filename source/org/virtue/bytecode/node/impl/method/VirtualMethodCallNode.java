package org.virtue.bytecode.node.impl.method;

import org.objectweb.asm.tree.MethodInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class VirtualMethodCallNode extends AbstractMethodCallNode {

    public VirtualMethodCallNode(MethodInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.VIRTUAL_METHOD_CALL_NODE;
    }
}
