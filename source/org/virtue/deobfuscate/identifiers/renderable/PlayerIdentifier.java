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
import org.virtue.deobfuscate.identifiers.cache.definition.PlayerDefinitionIdentifier;

/**
 * @author : const_
 */
public class PlayerIdentifier extends AbstractClassIdentifier {

    public PlayerIdentifier() {
        add(new Definition());
        add(new Name());
    }

    public class Name extends AbstractFieldIdentifier{
        @Override
        public FieldElement identify() {
            return new FieldQuery(PlayerIdentifier.this.identified()).desc("Ljava/lang/String;").member().first();
        }
    }

    @Override
    public ClassElement identify() {
        ClassElement client = VirtueTransformer.getInstance().getInjector().get(ClientIdentifier.class).identified();
        ClassQuery playerQuery = new ClassQuery();
        final Flag playerArray = new Flag();
        final Flag localPlayer = new Flag();
        for (final ClassElement possiblePlayer : playerQuery.branchSize(5).allOnBranch(0)) {
            playerArray.unflag();
            localPlayer.unflag();
            for (MethodElement element : client.methods()) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (node.desc().equals("[L" + possiblePlayer.name() + ";")) {
                            playerArray.flag();
                        }
                        if (node.desc().equals("L" + possiblePlayer.name() + ";")) {
                            localPlayer.flag();
                        }
                    }
                };
                if (localPlayer.flagged() && playerArray.flagged()) {
                    return possiblePlayer;
                }
            }
        }
        return null;
    }

    private class Definition extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            FieldQuery query = new FieldQuery(PlayerIdentifier.this.identified());
            ClassElement definition = VirtueTransformer.getInstance().getInjector().get(PlayerDefinitionIdentifier.class).identified();
            return query.desc("L" + definition.name() + ";").member().first();
        }
    }
}
