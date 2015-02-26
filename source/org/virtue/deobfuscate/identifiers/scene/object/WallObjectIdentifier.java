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
public class WallObjectIdentifier extends AbstractClassIdentifier {


    private AbstractFieldIdentifier first;


    public WallObjectIdentifier() {
        add(first = new Renderable());
        add(new BackupRenderable());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        ClassElement renderable = VirtueTransformer.getInstance().getInjector().get(RenderableIdentifier.class).identified();
        ClassElement tile = VirtueTransformer.getInstance().getInjector().get(TileIdentifier.class).identified();
        return query.branchSize(1).hasField("L" + renderable.name() + ";", true, 2)
                .hasField("I", true, 9).classHasField(tile, "", true).first();
    }

    public class Renderable extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = VirtueTransformer.getInstance().getInjector().get(RenderableIdentifier.class).identified();
            return new FieldQuery(WallObjectIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().first();
        }
    }

    public class BackupRenderable extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = VirtueTransformer.getInstance().getInjector().get(RenderableIdentifier.class).identified();
            return new FieldQuery(WallObjectIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().notNamed(first.identified().name()).first();
        }
    }
}
