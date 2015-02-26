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
public class GroundLayerIdentifier extends AbstractClassIdentifier {

    private AbstractFieldIdentifier layer1, layer2;


    public GroundLayerIdentifier(Injector injector) {
    	super(injector);
        add(layer1 = new Layer1());
        add(layer2 = new Layer2());
        add(new Layer3());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        ClassElement renderable = getInjector().get(RenderableIdentifier.class).identified();
        ClassElement tile = getInjector().get(TileIdentifier.class).identified();
        return query.branchSize(1).hasField("L" + renderable.name() + ";", true, 3)
                .hasField("I", true, 5).classHasField(tile, "", true).first();
    }

    public class Layer1 extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = getInjector().get(RenderableIdentifier.class).identified();
            return new FieldQuery(GroundLayerIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().first();
        }
    }

    public class Layer2 extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = getInjector().get(RenderableIdentifier.class).identified();
            return new FieldQuery(GroundLayerIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().notNamed(layer1.identified().name()).first();
        }
    }

    public class Layer3 extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement renderable = getInjector().get(RenderableIdentifier.class).identified();
            return new FieldQuery(GroundLayerIdentifier.this.identified()).desc("L" + renderable.name() + ";").
                    member().notNamed(layer1.identified().name()).notNamed(layer2.identified().name()).first();
        }
    }
}
