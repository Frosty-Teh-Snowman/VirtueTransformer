package org.virtue.deobfuscation.indentifiers.scene.object;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.deobfuscation.indentifiers.renderable.RenderableIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.TileIdentifier;

/**
 * @author : const_
 */
public class GroundObjectIdentifier extends AbstractClassIdentifier {

    public GroundObjectIdentifier() {
        add(new Renderable());
    }
    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        ClassElement renderable = Injector.get(RenderableIdentifier.class).identified();
        ClassElement tile = Injector.get(TileIdentifier.class).identified();
        return query.branchSize(1).hasField("L" + renderable.name() + ";", true, 1)
                .hasField("I", true, 5).classHasField(tile, "", true).first();
    }

    public class Renderable extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = Injector.get(RenderableIdentifier.class).identified();
            return new FieldQuery(GroundObjectIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().first();
        }
    }
}
