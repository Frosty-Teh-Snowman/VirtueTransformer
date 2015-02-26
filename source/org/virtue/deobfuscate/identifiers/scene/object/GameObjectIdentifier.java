package org.virtue.deobfuscate.identifiers.scene.object;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;
import org.virtue.deobfuscate.identifiers.renderable.RenderableIdentifier;
import org.virtue.deobfuscate.identifiers.scene.TileIdentifier;

/**
 * @author : const_
 */
public class GameObjectIdentifier extends AbstractClassIdentifier {

    public GameObjectIdentifier() {
        add(new Renderable());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        ClassElement tile = VirtueTransformer.getInstance().getInjector().get(TileIdentifier.class).identified();
        return query.branchSize(1).classHasField(tile, "[", true).first();
    }


    public class Renderable extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = VirtueTransformer.getInstance().getInjector().get(RenderableIdentifier.class).identified();
            return new FieldQuery(GameObjectIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().first();
        }
    }
}
