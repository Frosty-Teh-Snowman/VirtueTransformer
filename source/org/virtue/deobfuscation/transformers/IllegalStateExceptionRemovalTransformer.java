package org.virtue.deobfuscation.transformers;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.deobfuscation.Transformer;

/**
 * @author : const_
 */
public class IllegalStateExceptionRemovalTransformer extends Transformer {

    /**
	 * @param injector
	 */
	public IllegalStateExceptionRemovalTransformer(Injector injector) {
		super(injector);
	}

	@Override
    public void transform(List<ClassElement> elements) {
        List<AbstractInsnNode> remove = new LinkedList<>();
        for (ClassElement element : elements) {
            for (MethodElement method : element.methods()) {
                for (AbstractInsnNode ain : method.instructionList()) {
                    if (ain.getOpcode() == Opcodes.NEW) {
                        TypeInsnNode node = (TypeInsnNode) ain;
                        if (node.desc.equals("java/lang/IllegalStateException")) {
                            remove.clear();
                            remove.add(node);
                            AbstractInsnNode next = node;
                            boolean dup = false, invoke = false, athrow = false;
                            for (int i = 0; i < 5; i++) {
                                next = next.getNext();
                                if (next == null || dup && athrow && invoke) {
                                    break;
                                }
                                switch (next.getOpcode()) {
                                    case Opcodes.DUP:
                                        if (!dup) {
                                            remove.add(next);
                                            dup = true;
                                        }
                                        break;
                                    case Opcodes.INVOKESPECIAL:
                                        MethodInsnNode min = (MethodInsnNode) next;
                                        if (!invoke && min.owner.equals("java/lang/IllegalStateException") &&
                                                min.name.equals("<init>") &&
                                                min.desc.equals("()V")) {
                                            remove.add(next);
                                            invoke = true;
                                        }
                                        break;
                                    case Opcodes.ATHROW:
                                        if (!athrow) {
                                            remove.add(next);
                                            athrow = true;
                                        }
                                        break;
                                }
                            }
                            if (remove.size() == 4) {
                                for (AbstractInsnNode rem : remove) {
                                    method.node().instructions.remove(rem);
                                }
                                add();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String result() {
        StringBuilder builder = new StringBuilder("\t\t\tExecuted ");
        builder.append(name()).append(" in ").append(exec()).append("ms\n\t\t\t\tRemoved ")
                .append(counter()).append(" twigs");
        return builder.toString();
    }
}
