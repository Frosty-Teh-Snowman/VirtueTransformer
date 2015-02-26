package org.virtue.bytecode.node.impl.type;

import org.objectweb.asm.tree.TypeInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class NewNode extends AbstractTypeNode {

    public NewNode(TypeInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.NEW_NODE;
    }
}
