package org.virtue.deobfuscation.identifiers.scene.object;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.RenderableIdentifier;
import org.virtue.deobfuscation.identifiers.scene.TileIdentifier;

/**
 * @author : const_
 */
public class GroundObjectIdentifier extends AbstractClassIdentifier {

    public GroundObjectIdentifier(Injector injector) {
    	super(injector);
        add(new Renderable());
    }
    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        ClassElement renderable = getInjector().get(RenderableIdentifier.class).identified();
        ClassElement tile = getInjector().get(TileIdentifier.class).identified();
        return query.branchSize(1).hasField("L" + renderable.name() + ";", true, 1)
                .hasField("I", true, 5).classHasField(tile, "", true).first();
    }

    public class Renderable extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = getInjector().get(RenderableIdentifier.class).identified();
            return new FieldQuery(GroundObjectIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().first();
        }
    }
}
