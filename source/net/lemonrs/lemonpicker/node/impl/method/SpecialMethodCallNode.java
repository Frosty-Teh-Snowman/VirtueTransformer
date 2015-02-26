package net.lemonrs.lemonpicker.node.impl.method;

import net.lemonrs.lemonpicker.node.AbstractNode;

import org.objectweb.asm.tree.MethodInsnNode;

/**
 * @author : const_
 */
public class SpecialMethodCallNode extends AbstractMethodCallNode {

    public SpecialMethodCallNode(MethodInsnNode node) {
        super(node);
    }

    @Override
    public int type() {
        return AbstractNode.SPECIAL_METHOD_CALL_NODE;
    }
}
