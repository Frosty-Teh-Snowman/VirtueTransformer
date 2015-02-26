package org.virtue.bytecode.tree.method;

import org.objectweb.asm.tree.VarInsnNode;
import org.virtue.bytecode.tree.AbstractMethodVisitor;

/**
 * @author : const_
 */
public class LocalVariableVisitor extends AbstractMethodVisitor<VarInsnNode> {

    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var);
        add(new VarInsnNode(opcode, var));
    }
}
