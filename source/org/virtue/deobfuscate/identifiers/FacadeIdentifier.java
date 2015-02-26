package org.virtue.deobfuscate.identifiers;

import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscate.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class FacadeIdentifier extends AbstractClassIdentifier {
    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("[I", false, 3).hasMethodReturns("I", false).first();
    }
}
