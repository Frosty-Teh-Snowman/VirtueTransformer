package org.virtue.bytecode.node.impl;

import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class LabelNode extends BasicNode<org.objectweb.asm.tree.LabelNode> {

    public LabelNode(org.objectweb.asm.tree.LabelNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.LABEL_NODE;
    }
}
