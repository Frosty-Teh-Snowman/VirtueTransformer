package org.virtue.deobfuscate.identifiers.node;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;

/**
 * @author : const_
 */
public class QueueIdentifier extends AbstractClassIdentifier {

    public QueueIdentifier() {
        add(new Head());
    }

    @Override
    public ClassElement identify() {
        ClassElement cacheableNode = VirtueTransformer.getInstance().getInjector().get(CacheableNodeIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("L" + cacheableNode.name() + ";", true, 1)
                .notImplement("java/lang/Iterable").fieldCount(1).first();
    }

    public class Head extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            return new FieldQuery(QueueIdentifier.this.identified()).member().
                    desc("L" + VirtueTransformer.getInstance().getInjector().get(CacheableNodeIdentifier.class).identified().name() + ";").first();
        }
    }
}