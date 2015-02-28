package org.virtue.deobfuscation.indentifiers.cache.definition;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class ObjectDefinitionIdentifier extends AbstractClassIdentifier {

    @Override
    public ClassElement identify() {
        ClassElement playerDefinition = Injector.get(PlayerDefinitionIdentifier.class).identified();
        ClassElement npcDefinition = Injector.get(NpcDefinitionIdentifier.class).identified();
        ClassElement itemDefinition = Injector.get(ItemDefinitionIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(3).notNamed(playerDefinition.name()).notNamed(npcDefinition.name())
                .notNamed(itemDefinition.name()).hasField("[Ljava/lang/String;", true).hasField("[S", true).firstOnBranch(0);
    }
}
