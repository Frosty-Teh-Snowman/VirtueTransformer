package net.lemonrs.lemonpicker.node.impl.field;

import net.lemonrs.lemonpicker.node.AbstractNode;

import org.objectweb.asm.tree.FieldInsnNode;

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
