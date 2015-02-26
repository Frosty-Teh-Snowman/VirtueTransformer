package org.virtue.deobfuscate.identifiers.cache;

import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;

/**
 * @author : const_
 */
public class WidgetIdentifier extends AbstractClassIdentifier {

    public WidgetIdentifier() {
        add(new Children());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        for (ClassElement possible : query.branchSize(2).hasField("[[I", true).hasFieldLeast("[Ljava/lang/Object;", true, 12).allOnBranch(0)) {
            if (possible.hasField("[L" + possible.name() + ";", true)) {
                return possible;
            }
        }
        return null;
    }

    public class Children extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            return new FieldQuery(WidgetIdentifier.this.identified()).member()
                    .desc("[L" + WidgetIdentifier.this.identified().name() + ";").first();
        }
    }
}
