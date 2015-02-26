package org.virtue.deobfuscation.identifiers.cache.definition;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class ObjectDefinitionIdentifier extends AbstractClassIdentifier {

    /**
	 * @param injector
	 */
	public ObjectDefinitionIdentifier(Injector injector) {
		super(injector);
		// TODO Auto-generated constructor stub
	}

	@Override
    public ClassElement identify() {
        ClassElement playerDefinition = getInjector().get(PlayerDefinitionIdentifier.class).identified();
        ClassElement npcDefinition = getInjector().get(NpcDefinitionIdentifier.class).identified();
        ClassElement itemDefinition = getInjector().get(ItemDefinitionIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(3).notNamed(playerDefinition.name()).notNamed(npcDefinition.name())
                .notNamed(itemDefinition.name()).hasField("[Ljava/lang/String;", true).hasField("[S", true).firstOnBranch(0);
    }
}
