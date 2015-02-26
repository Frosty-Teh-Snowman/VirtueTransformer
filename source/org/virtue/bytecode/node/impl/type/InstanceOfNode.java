package org.virtue.bytecode.node.impl.type;

import org.objectweb.asm.tree.TypeInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class InstanceOfNode extends AbstractTypeNode {

    public InstanceOfNode(TypeInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.INSTANCE_OF_NODE;
    }
}
