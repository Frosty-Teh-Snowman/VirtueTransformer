package org.virtue.deobfuscate.identifiers.cache.definition;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscate.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class ObjectDefinitionIdentifier extends AbstractClassIdentifier {

    @Override
    public ClassElement identify() {
        ClassElement playerDefinition = VirtueTransformer.getInstance().getInjector().get(PlayerDefinitionIdentifier.class).identified();
        ClassElement npcDefinition = VirtueTransformer.getInstance().getInjector().get(NpcDefinitionIdentifier.class).identified();
        ClassElement itemDefinition = VirtueTransformer.getInstance().getInjector().get(ItemDefinitionIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(3).notNamed(playerDefinition.name()).notNamed(npcDefinition.name())
                .notNamed(itemDefinition.name()).hasField("[Ljava/lang/String;", true).hasField("[S", true).firstOnBranch(0);
    }
}
