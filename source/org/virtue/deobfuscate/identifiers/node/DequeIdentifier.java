package org.virtue.deobfuscate.identifiers.node;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldCallNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.MethodVisitor;
import org.virtue.bytecode.utility.Value;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;

/**
 * @author : const_
 */
public class DequeIdentifier extends AbstractClassIdentifier {

    private AbstractFieldIdentifier head;

    public DequeIdentifier() {
        add(head = new Head());
        add(new Current());
    }

    @Override
    public ClassElement identify() {
        ClassElement node = VirtueTransformer.getInstance().getInjector().get(NodeIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("L" + node.name() + ";", true, 2).fieldCount(2).first();
    }

    public class Head extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final ClassElement nodeElement = VirtueTransformer.getInstance().getInjector().get(NodeIdentifier.class).identified();
            for(MethodElement element : new MethodQuery(DequeIdentifier.this.identified()).member()
                    .returns("L" + nodeElement.name() + ";")) {
                final Value<FieldElement> value = new Value<>();
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        if(value.set()) {
                            return;
                        }
                        if(node.desc().equals("L" + nodeElement.name() + ";") &&
                                node.next().type() == AbstractNode.VIRTUAL_FIELD_CALL_NODE &&
                                node.ownerClass().name().equals(DequeIdentifier.this.identified().name())) {
                            VirtualFieldCallNode second = node.next();
                            if(second.desc().equals("L" + nodeElement.name() + ";")) {
                                value.set(node.field());
                            }
                        }
                    }
                };
                if(value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class Current extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement identified= VirtueTransformer.getInstance().getInjector().get(NodeIdentifier.class).identified();
            return new FieldQuery(DequeIdentifier.this.identified()).member().notNamed(head.identified().name())
                    .desc("L" + identified.name() + ";").first();
        }
    }
}
