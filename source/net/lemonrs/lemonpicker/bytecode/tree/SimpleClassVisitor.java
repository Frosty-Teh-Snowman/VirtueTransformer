package net.lemonrs.lemonpicker.bytecode.tree;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author : const_
 */
public class SimpleClassVisitor extends ClassVisitor {

    public SimpleClassVisitor() {
        super(Opcodes.ASM5);
    }

}
