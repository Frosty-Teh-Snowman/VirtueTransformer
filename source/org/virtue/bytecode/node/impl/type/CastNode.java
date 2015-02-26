package org.virtue.bytecode.node.impl.type;

import org.objectweb.asm.tree.TypeInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class CastNode extends AbstractTypeNode {

    public CastNode(TypeInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.CAST_NODE;
    }
}
