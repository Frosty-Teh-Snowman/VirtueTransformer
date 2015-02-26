package org.virtue.deobfuscate.transformers;

import java.util.LinkedList;
import java.util.List;

import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.impl.field.StaticFieldCallNode;
import org.virtue.bytecode.node.impl.field.StaticFieldStoreNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldCallNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldStoreNode;
import org.virtue.bytecode.node.impl.method.DynamicMethodCallNode;
import org.virtue.bytecode.node.impl.method.InterfaceMethodCallNode;
import org.virtue.bytecode.node.impl.method.SpecialMethodCallNode;
import org.virtue.bytecode.node.impl.method.StaticMethodCallNode;
import org.virtue.bytecode.node.impl.method.VirtualMethodCallNode;
import org.virtue.bytecode.node.impl.type.ANewArrayNode;
import org.virtue.bytecode.node.impl.type.CastNode;
import org.virtue.bytecode.node.impl.type.InstanceOfNode;
import org.virtue.bytecode.node.impl.type.NewNode;
import org.virtue.bytecode.tree.MethodVisitor;
import org.virtue.bytecode.utility.OpcodeUtility;
import org.virtue.deobfuscate.Transformer;

/**
 * @author : const_
 */
public class UnusedClassRemovalTransform extends Transformer {

    @SuppressWarnings("unused")
	@Override
    public void transform(List<ClassElement> elements) {
        final List<String> used = new LinkedList<>();
        for(ClassElement element : elements) {
            for(MethodElement method : element.methods()) {
                MethodVisitor visitor = new MethodVisitor(method) {

                    @Override
                    public void visitCast(CastNode node) {
                        String name = OpcodeUtility.stripDesc(node.desc());
                        if(!used.contains(name)) {
                            used.add(name);
                        }
                    }

                    @Override
                    public void visitNew(NewNode node) {
                        String name = OpcodeUtility.stripDesc(node.desc());
                        if(!used.contains(name)) {
                            used.add(name);
                        }
                    }

                    @Override
                    public void visitInstanceOf(InstanceOfNode node) {
                        String name = OpcodeUtility.stripDesc(node.desc());
                        if(!used.contains(name)) {
                            used.add(name);
                        }
                    }

                    @Override
                    public void visitNewArray(ANewArrayNode node) {
                        String name = OpcodeUtility.stripDesc(node.desc());
                        if(!used.contains(name)) {
                            used.add(name);
                        }
                    }

                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        used.add(node.owner());
                    }

                    @Override
                    public void visitStaticFieldStore(StaticFieldStoreNode node) {
                        used.add(node.owner());
                    }

                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        used.add(node.owner());
                    }

                    @Override
                    public void visitVirtualFieldStore(VirtualFieldStoreNode node) {
                        used.add(node.owner());
                    }

                    @Override
                    public void visitStaticMethodCall(StaticMethodCallNode node) {
                        used.add(node.owner());
                    }

                    @Override
                    public void visitVirtualMethodCall(VirtualMethodCallNode node) {
                        used.add(node.owner());
                    }

                    @Override
                    public void visitInterfaceMethodCall(InterfaceMethodCallNode node) {
                        used.add(node.owner());
                    }

                    @Override
                    public void visitDynamicMethodCall(DynamicMethodCallNode node) {
                        used.add(node.owner());
                    }

                    @Override
                    public void visitSpecialMethodCall(SpecialMethodCallNode node) {
                        used.add(node.owner());
                    }
                };
            }
        }
        List<ClassElement> remove = new LinkedList<>();
        for(ClassElement element : elements) {
            if(!used.contains(element.name())) {
                remove.add(element);
                add();
                continue;
            }
            tAdd();
        }
        elements.removeAll(remove);
    }

    @Override
    public String result() {
        StringBuilder builder = new StringBuilder("\t\t\t↔ Executed ");
        builder.append(name()).append(" in ").append(exec()).append("ms\n\t\t\t\tRemoved ")
                .append(counter()).append(" sour lemons : kept ").append(total() - counter())
                .append(" lemons.");
        return builder.toString();
    }
}
