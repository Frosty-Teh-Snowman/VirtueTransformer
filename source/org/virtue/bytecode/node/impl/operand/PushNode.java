package org.virtue.bytecode.node.impl.operand;

import org.objectweb.asm.tree.IntInsnNode;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.BasicNode;

/**
 * @author : const_
 */
public class PushNode extends BasicNode<IntInsnNode> {

    public PushNode(IntInsnNode node) {
        super(node);
    }

    public int push() {
        return node().operand;
    }

    @Override
    public int type() {
        return AbstractNode.PUSH_NODE;
    }


}
