package org.virtue.bytecode.node.impl.field;

import org.objectweb.asm.tree.FieldInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class VirtualFieldStoreNode extends AbstractFieldNode {

    public VirtualFieldStoreNode(FieldInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.VIRTUAL_FIELD_STORE_NODE;
    }

}
