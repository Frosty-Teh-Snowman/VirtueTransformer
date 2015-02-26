package org.virtue.bytecode.tree.method;

import org.objectweb.asm.tree.MethodInsnNode;
import org.virtue.bytecode.tree.AbstractMethodVisitor;

/**
 * @author : const_
 */
public class MethodInsnVisitor extends AbstractMethodVisitor<MethodInsnNode> {

    @Override
    public void visitMethodInsn(int i, String s, String s2, String s3, boolean b) {
        super.visitMethodInsn(i, s, s2, s3, b);
        add(new MethodInsnNode(i, s, s2, s3));
    }
}
