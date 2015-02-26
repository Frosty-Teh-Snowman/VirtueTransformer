package org.virtue.bytecode.node.impl;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class BasicNode<T extends AbstractInsnNode> extends AbstractNode<T> {

    public BasicNode(T node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.BASIC_NODE;
    }
}
