package org.virtue.deobfuscation.indentifiers.scene;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.BoundaryObjectIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.GameObjectIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.GroundLayerIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.GroundObjectIdentifier;
import org.virtue.deobfuscation.indentifiers.scene.object.WallObjectIdentifier;

/**
 * @author : const_
 */
public class TileIdentifier extends AbstractClassIdentifier {

    public TileIdentifier() {
        add(new GameObjects());
        add(new BoundaryObject());
        add(new GroundLayer());
        add(new GroundObject());
        add(new WallObject());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        ClassElement region = Injector.get(RegionIdentifier.class).identified();
        return query.branchSize(2).classHasField(region, "[[[", true).firstOnBranch(0);
    }

    public class GameObjects extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            ClassElement gameObject = Injector.get(GameObjectIdentifier.class).identified();
            return new FieldQuery(TileIdentifier.this.identified()).desc("[L" + gameObject.name() + ";").member().first();
        }
    }

    public class BoundaryObject extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            ClassElement object = Injector.get(BoundaryObjectIdentifier.class).identified();
            return new FieldQuery(TileIdentifier.this.identified()).desc("L" + object.name() + ";").member().first();
        }
    }

    public class GroundLayer extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            ClassElement object = Injector.get(GroundLayerIdentifier.class).identified();
            return new FieldQuery(TileIdentifier.this.identified()).desc("L" + object.name() + ";").member().first();
        }
    }

    public class GroundObject extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            ClassElement object = Injector.get(GroundObjectIdentifier.class).identified();
            return new FieldQuery(TileIdentifier.this.identified()).desc("L" + object.name() + ";").member().first();
        }
    }

    public class WallObject extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            ClassElement object = Injector.get(WallObjectIdentifier.class).identified();
            return new FieldQuery(TileIdentifier.this.identified()).desc("L" + object.name() + ";").member().first();
        }
    }
}
