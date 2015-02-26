package org.virtue.bytecode.tree.field;

import org.objectweb.asm.tree.FieldInsnNode;
import org.virtue.bytecode.tree.AbstractMethodVisitor;

/**
 * @author : const_
 */
public class FieldInsnVisitor extends AbstractMethodVisitor<FieldInsnNode> {

    @Override
    public void visitFieldInsn(int i, String s, String s2, String s3) {
        super.visitFieldInsn(i, s, s2, s3);
        add(new FieldInsnNode(i, s, s2, s3));
    }
}
