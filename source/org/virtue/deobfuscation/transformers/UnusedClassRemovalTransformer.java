package org.virtue.deobfuscation.transformers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
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
import org.virtue.bytecode.tree.method.MethodVisitor;
import org.virtue.deobfuscation.Transformer;
import org.virtue.utility.ASMUtility;
import org.virtue.utility.RegexInsnFinder;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

/**
 * @author : const_
 */
public class UnusedClassRemovalTransformer extends Transformer {

    @Override
    public void transform(Map<String, ClassElement> map) {
        final List<String> used = new LinkedList<>();
        for(ClassElement element : map.values()) {
            for(MethodElement method : element.methods()) {
            	RegexInsnFinder finder = new RegexInsnFinder(method.node());
            	AbstractInsnNode[] nodes = finder.find("ICONST_0 ((LSHL)|(LSHR))");
            	    Printer printer = new Textifier();
            	    TraceMethodVisitor mp = new TraceMethodVisitor(printer); 
            	for (AbstractInsnNode node : nodes) {
        	        node.accept(mp);
        	        StringWriter sw = new StringWriter();
        	        printer.print(new PrintWriter(sw));
        	        printer.getText().clear();
        	        System.out.println("Node: " + sw.toString());
            	}
                MethodVisitor visitor = new MethodVisitor(method) {

                    @Override
                    public void visitCast(CastNode node) {
                        String name = ASMUtility.stripDesc(node.desc());
                        if(!used.contains(name)) {
                            used.add(name);
                        }
                    }

                    @Override
                    public void visitNew(NewNode node) {
                        String name = ASMUtility.stripDesc(node.desc());
                        if(!used.contains(name)) {
                            used.add(name);
                        }
                    }

                    @Override
                    public void visitInstanceOf(InstanceOfNode node) {
                        String name = ASMUtility.stripDesc(node.desc());
                        if(!used.contains(name)) {
                            used.add(name);
                        }
                    }

                    @Override
                    public void visitNewArray(ANewArrayNode node) {
                        String name = ASMUtility.stripDesc(node.desc());
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
        for(ClassElement element : map.values()) {
            if(!used.contains(element.name())) {
                remove.add(element);
                add();
                continue;
            }
            tAdd();
        }
        for (ClassElement el : remove) {
        	map.remove(el.name());
        }
    }

    @Override
    public String result() {
        StringBuilder builder = new StringBuilder("\t\t\tExecuted ");
        builder.append(name()).append(" in ").append(exec()).append("ms\n\t\t\t\tRemoved ")
                .append(counter()).append(" class(es) : kept ").append(total() - counter())
                .append(" class(es).");
        return builder.toString();
    }
}
