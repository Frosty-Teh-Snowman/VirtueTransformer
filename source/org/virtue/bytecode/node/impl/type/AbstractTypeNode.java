package org.virtue.bytecode.node.impl.type;

import org.objectweb.asm.tree.TypeInsnNode;
import org.virtue.bytecode.node.impl.BasicNode;

/**
 * @author : const_
 */
public abstract class AbstractTypeNode extends BasicNode<TypeInsnNode> {

    public AbstractTypeNode(TypeInsnNode node) {
        super(node);
    }

    public String desc() {
        return node().desc;
    }
}
