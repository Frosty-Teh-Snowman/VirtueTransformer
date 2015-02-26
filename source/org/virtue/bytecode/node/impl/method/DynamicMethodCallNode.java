package org.virtue.bytecode.node.impl.method;

import org.objectweb.asm.tree.MethodInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class DynamicMethodCallNode extends AbstractMethodCallNode {

    public DynamicMethodCallNode(MethodInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.DYNAMIC_METHOD_CALL_NODE;
    }
}
