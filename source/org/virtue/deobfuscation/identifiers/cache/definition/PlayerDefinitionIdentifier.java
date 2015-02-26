package org.virtue.deobfuscation.identifiers.cache.definition;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.PlayerIdentifier;

/**
 * @author : const_
 */
public class PlayerDefinitionIdentifier extends AbstractClassIdentifier {
    /**
	 * @param injector
	 */
	public PlayerDefinitionIdentifier(Injector injector) {
		super(injector);
		// TODO Auto-generated constructor stub
	}

	@Override
    public ClassElement identify() {
        ClassElement player = getInjector().get(PlayerIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("[I", true, 2)
                .hasField("J", true, 2).classHasField(player, "", true).first();
    }
}
