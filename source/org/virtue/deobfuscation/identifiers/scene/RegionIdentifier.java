package org.virtue.deobfuscation.identifiers.scene;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;

/**
 * @author : const_
 */
public class RegionIdentifier extends AbstractClassIdentifier {

    public RegionIdentifier(Injector injector) {
    	super(injector);
        add(new Tiles());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasFieldDescStartWith("[[[L", true).hasFieldDescStartWith("[L", true).first();
    }

    public class Tiles extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            ClassElement tile = getInjector().get(TileIdentifier.class).identified();
            return new FieldQuery(RegionIdentifier.this.identified()).desc("[[[L" + tile.name() + ";").member().first();
        }
    }
}
