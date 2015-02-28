package org.virtue.bytecode.node.impl.field;

import org.objectweb.asm.tree.FieldInsnNode;
import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.node.impl.BasicNode;

/**
 * @author : const_
 */
public abstract class AbstractFieldNode extends BasicNode<FieldInsnNode> {

    public AbstractFieldNode(FieldInsnNode node) {
        super(node);
    }

    public String desc() {
        if (field() == null) {
            return node().desc;
        }
        return field().desc();
    }

    public String name() {
        return node().name;
    }

    public String owner() {
        return node().owner;
    }

    public ClassElement ownerClass() {
        return Injector.get(owner());
    }

    public FieldElement field() {
        ClassElement element = ownerClass();
        if (element != null) {
            return element.findField(name());
        }
        return null;
    }
}
