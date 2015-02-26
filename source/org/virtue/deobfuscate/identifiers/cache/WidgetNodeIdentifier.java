package org.virtue.deobfuscate.identifiers.cache;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.impl.field.VirtualFieldCallNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.MethodVisitor;
import org.virtue.bytecode.utility.Value;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;
import org.virtue.deobfuscate.identifiers.ClientIdentifier;

/**
 * @author : const_
 */
public class WidgetNodeIdentifier extends AbstractClassIdentifier {

    public WidgetNodeIdentifier() {
        add(new Id());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(2).hasField("Z", true, 1).hasField("I", true, 2).firstOnBranch(0);
    }

    public class Id extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            final FieldElement widgetNodeBag = VirtueTransformer.getInstance().getInjector().get(ClientIdentifier.class).widgetNodeBag.identified();
            final ClassElement widgetNode = VirtueTransformer.getInstance().getInjector().get(WidgetNodeIdentifier.class).identified();
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().hasCast(widgetNode.name()).
                    references(widgetNodeBag).returns("V").notMember()) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        if(value.set()) {
                            return;
                        }
                        if(node.owner().equals(widgetNode.name()) && node.desc().equals("I")) {
                            value.set(node.field());
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
}
