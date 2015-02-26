package org.virtue.bytecode.node.impl.type;

import org.objectweb.asm.tree.TypeInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class ANewArrayNode extends AbstractTypeNode {

    public ANewArrayNode(TypeInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.ANEW_ARRAY_NODE;
    }
}
