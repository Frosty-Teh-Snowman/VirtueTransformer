package org.virtue.bytecode.node.impl.operand;

import org.objectweb.asm.tree.LdcInsnNode;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.BasicNode;

/**
 * @author : const_
 */
public class LdcNode extends BasicNode<LdcInsnNode> {

    public LdcNode(LdcInsnNode node) {
        super(node);
    }

    public Object value() {
        return node().cst;
    }

    @Override
    public int type() {
        return AbstractNode.LDC_NODE;
    }

}
