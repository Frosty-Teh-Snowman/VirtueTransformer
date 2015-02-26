package org.virtue.deobfuscation.identifiers.renderable;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;

/**
 * @author : const_
 */
public class RenderableIdentifier extends AbstractClassIdentifier {

    public RenderableIdentifier(Injector injector) {
    	super(injector);
        add(new Height());
    }

    @Override
    public ClassElement identify() {
        ClassElement player = getInjector().get(PlayerIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(5).onBranchAt(player, 0).firstOnBranch(2);
    }

    public class Height extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            return new FieldQuery(RenderableIdentifier.this.identified()).member().desc("I").first();
        }
    }
}
