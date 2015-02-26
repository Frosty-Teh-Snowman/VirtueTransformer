package org.virtue.bytecode.tree.method;

import org.objectweb.asm.tree.TypeInsnNode;
import org.virtue.bytecode.tree.AbstractMethodVisitor;

/**
 * @author : const_
 */
public class TypeInsnVisitor extends AbstractMethodVisitor<TypeInsnNode> {

    @Override
    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, type);
        add(new TypeInsnNode(opcode, type));
    }
}
