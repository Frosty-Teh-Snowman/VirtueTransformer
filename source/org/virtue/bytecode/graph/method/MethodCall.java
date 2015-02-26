package org.virtue.bytecode.graph.method;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.tree.MethodInsnNode;
import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.impl.method.DynamicMethodCallNode;
import org.virtue.bytecode.node.impl.method.InterfaceMethodCallNode;
import org.virtue.bytecode.node.impl.method.SpecialMethodCallNode;
import org.virtue.bytecode.node.impl.method.StaticMethodCallNode;
import org.virtue.bytecode.node.impl.method.VirtualMethodCallNode;
import org.virtue.bytecode.tree.MethodVisitor;

/**
 * @author : const_
 */
public class MethodCall {

    private MethodInsnNode node;
    private MethodCall parent;
    private MethodElement element;
    private List<MethodCall> calls;

    public MethodCall(MethodInsnNode node, MethodCall parent) {
        this.node = node;
        this.parent = parent;
        ClassElement super_ = VirtueTransformer.getInstance().getInjector().get(node.owner);
        while (element == null && super_ != null) {
            element = super_.findMethod(node.name, node.desc);
            super_ = VirtueTransformer.getInstance().getInjector().get(super_.node().superName);
        }
    }

    public MethodCall(MethodInsnNode node) {
        this.node = node;
        ClassElement super_ = VirtueTransformer.getInstance().getInjector().get(node.owner);
        while (element == null && super_ != null) {
            element = super_.findMethod(node.name, node.desc);
            super_ = VirtueTransformer.getInstance().getInjector().get(super_.node().superName);
        }
    }

    public List<MethodCall> calls() {
        if (calls == null) {
            calls = new LinkedList<>();
            if (element != null) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitDynamicMethodCall(DynamicMethodCallNode node) {
                        calls.add(new MethodCall(node(), MethodCall.this));
                    }

                    @Override
                    public void visitInterfaceMethodCall(InterfaceMethodCallNode node) {
                        calls.add(new MethodCall(node(), MethodCall.this));
                    }

                    @Override
                    public void visitSpecialMethodCall(SpecialMethodCallNode node) {
                        calls.add(new MethodCall(node(), MethodCall.this));
                    }

                    @Override
                    public void visitStaticMethodCall(StaticMethodCallNode node) {
                        calls.add(new MethodCall(node(), MethodCall.this));
                    }

                    @Override
                    public void visitVirtualMethodCall(VirtualMethodCallNode node) {
                        calls.add(new MethodCall(node(), MethodCall.this));
                    }
                };
            }
        }
        return calls;
    }

    public boolean fromEntry() {
        return parent == null;
    }

    public MethodCall parent() {
        return parent;
    }

    public MethodInsnNode node() {
        return node;
    }
}
