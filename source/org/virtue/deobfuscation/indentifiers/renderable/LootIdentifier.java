package org.virtue.deobfuscation.indentifiers.renderable;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldCallNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.method.MethodVisitor;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.deobfuscation.indentifiers.cache.ModelIdentifier;
import org.virtue.utility.Value;

/**
 * @author : const_
 */
public class LootIdentifier extends AbstractClassIdentifier {

    public LootIdentifier() {
        add(new Id());
        add(new StackSize());
    }

    @Override
    public ClassElement identify() {
        ClassElement renderable = Injector.get(RenderableIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(4).hasField("I", true, 2).fieldCount(2).extend(renderable.name()).firstOnBranch(0);
    }

    public class Id extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement model = Injector.get(ModelIdentifier.class).identified();
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(LootIdentifier.this.identified()).
                    returns("L" + model.name() + ";").member()) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.ownerClass().name().equals(LootIdentifier.this.identified().name()) &&
                                node.desc().equals("I")) {
                            VirtualFieldCallNode next = node.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                            if (next != null && next.desc().equals("I")
                                    && next.ownerClass().name().equals(LootIdentifier.this.identified().name())) {
                                value.set(node.field());
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class StackSize extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement model = Injector.get(ModelIdentifier.class).identified();
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(LootIdentifier.this.identified()).
                    returns("L" + model.name() + ";").member()) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.ownerClass().name().equals(LootIdentifier.this.identified().name()) &&
                                node.desc().equals("I") && node.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE) == null) {
                            value.set(node.field());
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }
}
