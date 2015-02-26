package org.virtue.deobfuscate.identifiers.cache.definition;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscate.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class ItemDefinitionIdentifier extends AbstractClassIdentifier {
    @Override
    public ClassElement identify() {
        ClassElement playerDefinition = VirtueTransformer.getInstance().getInjector().get(PlayerDefinitionIdentifier.class).identified();
        ClassElement npcDefinition = VirtueTransformer.getInstance().getInjector().get(NpcDefinitionIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(3).notNamed(playerDefinition.name()).notNamed(npcDefinition.name())
                .hasField("[Ljava/lang/String;", true, 2).hasField("[S", true).firstOnBranch(0);
    }
}
