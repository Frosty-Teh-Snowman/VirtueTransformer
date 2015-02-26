package org.virtue.deobfuscation.identifiers.renderable;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.impl.field.StaticFieldCallNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.bytecode.tree.method.MethodVisitor;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.deobfuscation.identifiers.ClientIdentifier;
import org.virtue.deobfuscation.identifiers.cache.definition.NpcDefinitionIdentifier;
import org.virtue.utility.Flag;

/**
 * @author : const_
 */
public class NpcIdentifier extends AbstractClassIdentifier {

    public NpcIdentifier(Injector injector) {
    	super(injector);
        add(new Definition());
    }

    @Override
    public ClassElement identify() {
        ClassElement client = getInjector().get(ClientIdentifier.class).identified();
        ClassQuery npcQuery = new ClassQuery();
        final Flag npcArray = new Flag();
        for (final ClassElement possibleNpc : npcQuery.branchSize(5)
                .notNamed(getInjector().get(PlayerIdentifier.class).identified().name()).allOnBranch(0)) {
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
            ClassElement definition = getInjector().get(NpcDefinitionIdentifier.class).identified();
            return query.desc("L" + definition.name() + ";").member().first();
        }
    }
}
