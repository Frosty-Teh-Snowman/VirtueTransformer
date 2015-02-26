package org.virtue.bytecode.graph.method;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.tree.MethodInsnNode;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.tree.method.MethodInsnVisitor;

/**
 * @author : const_
 */
public class EntryPoint {

    private MethodElement element;
    private List<MethodCall> calls;

    public EntryPoint(MethodElement element) {
        this.element = element;
    }

    public List<MethodCall> calls() {
        if(calls == null) {
            calls = new LinkedList<>();
            MethodInsnVisitor visitor = element.graph().visit(MethodInsnVisitor.class);
            for(MethodInsnNode node : visitor.instructions()) {
                calls.add(new MethodCall(node));
            }
        }
        return calls;
    }
}
