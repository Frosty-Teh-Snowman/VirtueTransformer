package org.virtue.utility;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.operand.PushNode;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.method.MethodVisitor;

/**
 * @author : const_
 */
public class RevisionFinder {

    public static int find() {
        MethodQuery query = new MethodQuery(VirtueTransformer.getInstance().getInjector().get("client"));
        final Value<Integer> value = new Value<>();
        for (MethodElement element : query.constant(765, 503)) {
            final Flag width = new Flag();
            MethodVisitor visitor = new MethodVisitor(element) {
                @Override
                public void visitPush(PushNode node) {
                    super.visitPush(node);
                    if (node.push() == 765) {
                        width.flag();
                    }
                    if (width.flagged() && node.push() == 503 && !value.set()) {
                        PushNode next = node.next(AbstractNode.PUSH_NODE);
                        if (next != null) {
                            value.set(next.push());
                        }
                    }
                }
            };
            if (value.set()) {
                return value.value();
            }
        }
        return value.set() ? value.value() : -1;
    }
}
