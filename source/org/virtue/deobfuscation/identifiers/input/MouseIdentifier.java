package org.virtue.deobfuscation.identifiers.input;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class MouseIdentifier extends AbstractClassIdentifier {
    /**
	 * @param injector
	 */
	public MouseIdentifier(Injector injector) {
		super(injector);
		// TODO Auto-generated constructor stub
	}

	@Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).implement("java/awt/event/MouseListener")
                .implement("java/awt/event/MouseMotionListener").first();
    }
}
