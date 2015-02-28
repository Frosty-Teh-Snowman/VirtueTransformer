package org.virtue.deobfuscation.transformers;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.MethodNode;
import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.graph.MethodCallGraph;
import org.virtue.bytecode.graph.method.EntryPoint;
import org.virtue.bytecode.graph.method.MethodCall;
import org.virtue.deobfuscation.Transformer;

/**
 * @author : const_
 */
public class UnusedMethodRemovalTransformer extends Transformer {
    @Override
    public void transform(Map<String, ClassElement> map) {

        List<EntryPoint> entries = new LinkedList<>();
        for (ClassElement element : map.values()) {
            MethodElement method = element.findMethod("<clinit>", "()V");
            if (method != null) {
                entries.add(new EntryPoint(method));
            }
        }
        entries.add(new EntryPoint(Injector.get("client").findMethod("init", "()V")));
        entries.add(new EntryPoint(Injector.get(Injector.get("client").node().superName).findMethod("start", "()V")));
        MethodCallGraph graph = new MethodCallGraph(entries);
        graph.build();
        List<MethodNode> remove = new LinkedList<>();
        List<MethodCall> called = graph.calls();
        for (ClassElement element : map.values()) {
            remove.clear();
            methods:
            for (MethodElement method : element.methods()) {
                if (method.isInherited() || method.isAbstract() || method.isNative() ||
                        method.name().equals("<init>") || method.name().equals("<clinit>")) {
                    tAdd();
                    continue;
                }
                if (element.node().superName.startsWith("java/")) {
                    try {
                        Class<?> super_ = ClassLoader.getSystemClassLoader().loadClass(element.node().superName.replaceAll("/", "."));
                        for(Method m : super_.getMethods()) {
                            if(m.getName().equals(method.name())) {
                                tAdd();
                                continue methods;
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                for (MethodCall call : called) {
                    if (call.node().name.equals(method.name()) &&
                            call.node().owner.equals(method.parent().name()) &&
                            call.node().desc.equals(method.desc())) {
                        tAdd();
                        continue methods;
                    }
                }
                remove.add(method.node());
                add();
            }
            element.node().methods.removeAll(remove);
        }
    }


    @Override
    public String result() {
        StringBuilder builder = new StringBuilder("\t\t\tExecuted ");
        builder.append(name()).append(" in ").append(exec()).append("ms\n\t\t\t\tRemoved ")
                .append(counter()).append(" unused lemon branches : kept ").append(total() - counter())
                .append(" branches.");
        return builder.toString();
    }
}
