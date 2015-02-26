package org.virtue.bytecode.node.impl.method;

import org.objectweb.asm.tree.MethodInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class SpecialMethodCallNode extends AbstractMethodCallNode {

    public SpecialMethodCallNode(MethodInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.SPECIAL_METHOD_CALL_NODE;
    }
}
