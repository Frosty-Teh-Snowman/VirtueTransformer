package org.virtue.bytecode.node.impl.method;

import org.objectweb.asm.tree.MethodInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class StaticMethodCallNode extends AbstractMethodCallNode {

    public StaticMethodCallNode(MethodInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.STATIC_METHOD_CALL_NODE;
    }
}
