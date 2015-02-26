package org.virtue.deobfuscate.identifiers.cache.definition;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldStoreNode;
import org.virtue.bytecode.node.impl.jump.IfConditionNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.MethodVisitor;
import org.virtue.bytecode.utility.Value;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;
import org.virtue.deobfuscate.identifiers.net.StreamIdentifier;
import org.virtue.deobfuscate.identifiers.renderable.NpcIdentifier;

/**
 * @author : const_
 */
public class NpcDefinitionIdentifier extends AbstractClassIdentifier {

    public NpcDefinitionIdentifier() {
        add(new ModelIds());
        add(new Name());
    }

    @Override
    public ClassElement identify() {
        ClassElement npc = VirtueTransformer.getInstance().getInjector().get(NpcIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(3).hasField("[Ljava/lang/String;", true)
                .hasField("[S", true).classHasField(npc, "", true).firstOnBranch(0);
    }

    public class Name extends AbstractFieldIdentifier{
        @Override
        public FieldElement identify() {
            return new FieldQuery(NpcDefinitionIdentifier.this.identified()).desc("Ljava/lang/String;").member().first();
        }
    }

    public class ModelIds extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement stream = VirtueTransformer.getInstance().getInjector().get(StreamIdentifier.class).identified();
            for (final MethodElement element : new MethodQuery(NpcDefinitionIdentifier.this.identified().methods())
                    .takes("L" + stream.name() + ";").takes("I")) {
                final Value<FieldElement> field = new Value<>();
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitIfCondition(IfConditionNode node) {
                        if (field.set()) {
                            return;
                        }
                        if (node.conditon().hasConstant() && node.conditon().comparison().constant().value().equals(1)) {
                            AbstractNode target = node.conditon().trueTarget();
                            VirtualFieldStoreNode store = (VirtualFieldStoreNode) target.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE);
                            while (store != null) {
                                if (field.set()) {
                                    break;
                                }
                                if (store.field().desc().equals("[I")) {
                                    field.set(store.field());
                                }
                                store = (VirtualFieldStoreNode) store.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE);
                            }
                            if (store != null && store.field().desc().equals("[I")) {
                                field.set(store.field());
                            }
                        }
                    }
                };
                if (field.set()) {
                    return field.value();
                }
            }
            return null;
        }
    }
}
