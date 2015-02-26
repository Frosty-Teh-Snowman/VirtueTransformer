package org.virtue.bytecode.element;

import java.lang.reflect.Modifier;

import org.objectweb.asm.tree.FieldNode;

/**
 * @author : const_
 */
public class FieldElement {

    private ClassElement parent;
    private FieldNode node;

    public FieldElement(ClassElement parent, FieldNode node) {
        this.parent = parent;
        this.node = node;
    }

    public boolean member() {
        return !Modifier.isStatic(access());
    }

    public ClassElement parent() {
        return parent;
    }

    public FieldNode node() {
        return node;
    }

    public String desc() {
        return node().desc;
    }

    public String name() {
        return node().name;
    }

    public int access() {
        return node().access;
    }
}
