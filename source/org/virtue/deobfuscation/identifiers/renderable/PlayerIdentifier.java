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
import org.virtue.deobfuscation.identifiers.cache.definition.PlayerDefinitionIdentifier;
import org.virtue.utility.Flag;

/**
 * @author : const_
 */
public class PlayerIdentifier extends AbstractClassIdentifier {

    public PlayerIdentifier(Injector injector) {
    	super(injector);
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
        ClassElement client = getInjector().get(ClientIdentifier.class).identified();
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
            ClassElement definition = getInjector().get(PlayerDefinitionIdentifier.class).identified();
            return query.desc("L" + definition.name() + ";").member().first();
        }
    }
}
