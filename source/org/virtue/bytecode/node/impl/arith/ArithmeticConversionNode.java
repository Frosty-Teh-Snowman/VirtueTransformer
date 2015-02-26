package org.virtue.bytecode.node.impl.arith;

import org.objectweb.asm.tree.InsnNode;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.BasicNode;

/**
 * @author : const_
 */
public class ArithmeticConversionNode extends BasicNode<InsnNode> {

    public ArithmeticConversionNode(InsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.ARITHMETIC_CONVERSION_NODE;
    }

}
