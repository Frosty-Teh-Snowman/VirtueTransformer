package org.virtue.bytecode.node.impl.field;

import org.objectweb.asm.tree.FieldInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class VirtualFieldCallNode extends AbstractFieldNode {

    public VirtualFieldCallNode(FieldInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.VIRTUAL_FIELD_CALL_NODE;
    }
}
