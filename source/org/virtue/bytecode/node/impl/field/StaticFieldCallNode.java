package org.virtue.bytecode.node.impl.field;

import org.objectweb.asm.tree.FieldInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class StaticFieldCallNode extends AbstractFieldNode {

    public StaticFieldCallNode(FieldInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.STATIC_FIELD_CALL_NODE;
    }
}
