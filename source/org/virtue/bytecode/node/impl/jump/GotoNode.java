package org.virtue.bytecode.node.impl.jump;

import org.objectweb.asm.tree.JumpInsnNode;
import org.virtue.bytecode.node.AbstractNode;

/**
 * @author : const_
 */
public class GotoNode extends AbstractJumpNode {

    public GotoNode(JumpInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.GOTO_NODE;
    }
}
