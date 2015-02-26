package org.virtue.deobfuscate.identifiers.renderable;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.impl.field.StaticFieldCallNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.bytecode.tree.MethodVisitor;
import org.virtue.bytecode.utility.Flag;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;
import org.virtue.deobfuscate.identifiers.ClientIdentifier;
import org.virtue.deobfuscate.identifiers.cache.definition.NpcDefinitionIdentifier;

/**
 * @author : const_
 */
public class NpcIdentifier extends AbstractClassIdentifier {

    public NpcIdentifier() {
        add(new Definition());
    }

    @Override
    public ClassElement identify() {
        ClassElement client = VirtueTransformer.getInstance().getInjector().get(ClientIdentifier.class).identified();
        ClassQuery npcQuery = new ClassQuery();
        final Flag npcArray = new Flag();
        for (final ClassElement possibleNpc : npcQuery.branchSize(5)
                .notNamed(VirtueTransformer.getInstance().getInjector().get(PlayerIdentifier.class).identified().name()).allOnBranch(0)) {
            npcArray.unflag();
            for (MethodElement element : client.methods()) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (node.desc().equals("[L" + possibleNpc.name() + ";")) {
                            npcArray.flag();
                        }
                    }
                };
                if (npcArray.flagged()) {
                    return possibleNpc;
                }
            }
        }
        return null;
    }


    private class Definition extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            FieldQuery query = new FieldQuery(NpcIdentifier.this.identified());
            ClassElement definition = VirtueTransformer.getInstance().getInjector().get(NpcDefinitionIdentifier.class).identified();
            return query.desc("L" + definition.name() + ";").member().first();
        }
    }
}
