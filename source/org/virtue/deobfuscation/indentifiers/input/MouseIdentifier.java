package org.virtue.deobfuscation.indentifiers.input;

import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;

/**
 * @author : const_
 */
public class MouseIdentifier extends AbstractClassIdentifier {
    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).implement("java/awt/event/MouseListener")
                .implement("java/awt/event/MouseMotionListener").first();
    }
}
