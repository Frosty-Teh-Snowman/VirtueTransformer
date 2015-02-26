package org.virtue.deobfuscation.identifiers;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class FacadeIdentifier extends AbstractClassIdentifier {
    /**
	 * @param injector
	 */
	public FacadeIdentifier(Injector injector) {
		super(injector);
		// TODO Auto-generated constructor stub
	}

	@Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("[I", false, 3).hasMethodReturns("I", false).first();
    }
}
