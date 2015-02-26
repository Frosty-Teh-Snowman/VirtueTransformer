package org.virtue.bytecode.node.impl.method;

import org.objectweb.asm.tree.MethodInsnNode;
import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.impl.BasicNode;

/**
 * @author : const_
 */
public abstract class AbstractMethodCallNode extends BasicNode<MethodInsnNode> {

    public AbstractMethodCallNode(MethodInsnNode node) {
        super(node);
    }

    public String owner() {
        return node().owner;
    }

    public String desc() {
        return node().desc;
    }

    public String name() {
        return node().name;
    }

    public ClassElement ownerClass() {
        return VirtueTransformer.getInstance().getInjector().get(owner());
    }

    public MethodElement method() {
        ClassElement element = ownerClass();
        if (element != null) {
            return element.findMethod(name(), desc());
        }
        return null;
    }
}
