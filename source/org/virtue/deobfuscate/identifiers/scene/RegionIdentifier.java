package org.virtue.deobfuscate.identifiers.scene;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;

/**
 * @author : const_
 */
public class RegionIdentifier extends AbstractClassIdentifier {

    public RegionIdentifier() {
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
            ClassElement tile = VirtueTransformer.getInstance().getInjector().get(TileIdentifier.class).identified();
            return new FieldQuery(RegionIdentifier.this.identified()).desc("[[[L" + tile.name() + ";").member().first();
        }
    }
}
