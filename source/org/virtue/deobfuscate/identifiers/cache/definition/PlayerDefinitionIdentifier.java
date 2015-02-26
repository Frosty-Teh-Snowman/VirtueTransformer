package org.virtue.deobfuscate.identifiers.cache.definition;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.identifiers.renderable.PlayerIdentifier;

/**
 * @author : const_
 */
public class PlayerDefinitionIdentifier extends AbstractClassIdentifier {
    @Override
    public ClassElement identify() {
        ClassElement player = VirtueTransformer.getInstance().getInjector().get(PlayerIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("[I", true, 2)
                .hasField("J", true, 2).classHasField(player, "", true).first();
    }
}
