package org.virtue.deobfuscation.indentifiers.cache.definition;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.indentifiers.renderable.PlayerIdentifier;

/**
 * @author : const_
 */
public class PlayerDefinitionIdentifier extends AbstractClassIdentifier {
    @Override
    public ClassElement identify() {
        ClassElement player = Injector.get(PlayerIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("[I", true, 2)
                .hasField("J", true, 2).classHasField(player, "", true).first();
    }
}
