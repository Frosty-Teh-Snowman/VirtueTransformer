package org.virtue.utility.refactor;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.utility.ClassContainer;

/**
 * @author sc4re
 */
public class Refactorer {

    protected HookMap hooks;
    protected ClassContainer container;

    public Refactorer(HookMap hooks) {
        setHooks(hooks);
    }

    public Refactorer(ClassContainer ct) {
        setContainer(ct);
    }

    public Refactorer(HookMap hooks, ClassContainer ct) {
        setHooks(hooks);
        setContainer(ct);
    }

    public HookMap getHooks() {
        return hooks;
    }

    public void setHooks(HookMap hooks) {
        this.hooks = hooks;
    }

    public ClassContainer getContainer() {
        return container;
    }

    public void setContainer(ClassContainer ct) {
        this.container = ct;
    }

    public void run() {
        if (getHooks() == null)
            return;
        
        if (getContainer() == null)
            return;
        
        RefactorMapper mapper = new RefactorMapper(getHooks());
        Map<String, ClassElement> refactored = new HashMap<>();
        for (ClassNode cn : getContainer().getNodes().values()) {
            String oldName = cn.name;
            ClassReader cr = new ClassReader(getClassNodeBytes(cn));
            ClassWriter cw = new ClassWriter(cr, 0);
            RemappingClassAdapter rca = new RemappingClassAdapter(cw, mapper);
            cr.accept(rca, ClassReader.EXPAND_FRAMES);
            cr = new ClassReader(cw.toByteArray());
            cn  = new ClassNode();
            cr.accept(cn, 0);
            refactored.put(oldName, new ClassElement(cn));
        }
        for (Map.Entry<String, ClassElement> factor : refactored.entrySet()) {
            getContainer().relocate(factor.getKey(), factor.getValue());
        }
    }

    private byte[] getClassNodeBytes(ClassNode cn) {
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        byte[] b = cw.toByteArray();
        return b;
    }
}
