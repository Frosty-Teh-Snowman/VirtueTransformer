package org.virtue.bytecode.node.impl.jump;

import org.objectweb.asm.tree.JumpInsnNode;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.BasicNode;
import org.virtue.bytecode.node.impl.LabelNode;

/**
 * @author : const_
 */
public abstract class AbstractJumpNode extends BasicNode<JumpInsnNode> {

    public AbstractJumpNode(JumpInsnNode node) {
        super(node);
    }

    public <N extends BasicNode> N target() {
        org.objectweb.asm.tree.LabelNode target = node().label;
        LabelNode next = next(AbstractNode.LABEL_NODE);
        if (next != null) {
            if (next.node().equals(target)) {
                return (N) next;
            }
            while ((next = next.next(AbstractNode.LABEL_NODE)) != null) {
                if (next.node().equals(target)) {
                    return (N) next;
                }
            }
        }
        LabelNode prev = prev(AbstractNode.LABEL_NODE);
        if (prev != null) {
            if (prev.node().equals(target)) {
                return (N) prev;
            }
            while ((prev = prev.prev(AbstractNode.LABEL_NODE)) != null) {
                if (prev.node().equals(target)) {
                    return (N) prev;
                }
            }
        }
        return (N) new LabelNode(target);
    }
}
