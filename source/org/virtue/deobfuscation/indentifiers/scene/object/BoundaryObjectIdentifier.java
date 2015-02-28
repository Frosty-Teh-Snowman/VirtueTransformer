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
public class BoundaryObjectIdentifier extends AbstractClassIdentifier {

    private AbstractFieldIdentifier first;

    public BoundaryObjectIdentifier() {
        add(first = new Renderable());
        add(new BackupRenderable());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        ClassElement renderable = Injector.get(RenderableIdentifier.class).identified();
        ClassElement tile = Injector.get(TileIdentifier.class).identified();
        return query.branchSize(1).hasField("L" + renderable.name() + ";", true, 2)
                .hasField("I", true, 7).classHasField(tile, "", true).first();
    }

    public class Renderable extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = Injector.get(RenderableIdentifier.class).identified();
            return new FieldQuery(BoundaryObjectIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().first();
        }
    }

    public class BackupRenderable extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = Injector.get(RenderableIdentifier.class).identified();
            return new FieldQuery(BoundaryObjectIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().notNamed(first.identified().name()).first();
        }
    }
}
