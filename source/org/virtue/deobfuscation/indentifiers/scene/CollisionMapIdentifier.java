package org.virtue.deobfuscation.indentifiers.scene;

import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;

/**
 * @author : const_
 */
public class CollisionMapIdentifier extends AbstractClassIdentifier {

    public CollisionMapIdentifier() {
        add(new Flags());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("[[I", true, 1).hasField("I", true, 4).fieldCount(5, true).first();
    }

    public class Flags extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            return new FieldQuery(CollisionMapIdentifier.this.identified()).desc("[[I").member().first();
        }
    }
}
