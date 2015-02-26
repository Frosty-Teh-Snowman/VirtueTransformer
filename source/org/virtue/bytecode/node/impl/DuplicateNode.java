package org.virtue.bytecode.node.impl;

import org.objectweb.asm.tree.InsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class DuplicateNode extends BasicNode<InsnNode> {

    public DuplicateNode(InsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.DUPLICATE_NODE;
    }
}
