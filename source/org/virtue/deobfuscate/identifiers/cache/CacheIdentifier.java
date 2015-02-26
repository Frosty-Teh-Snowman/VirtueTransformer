package org.virtue.deobfuscate.identifiers.cache;

import org.virtue.VirtueTransformer;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;
import org.virtue.deobfuscate.identifiers.node.BagIdentifier;
import org.virtue.deobfuscate.identifiers.node.CacheableNodeIdentifier;
import org.virtue.deobfuscate.identifiers.node.QueueIdentifier;

/**
 * @author : const_
 */
public class CacheIdentifier extends AbstractClassIdentifier {

    public CacheIdentifier() {
        add(new Bag());
        add(new Queue());
        add(new EmptyCacheableNode());
    }

    @Override
    public ClassElement identify() {
        ClassElement cacheableNode = VirtueTransformer.getInstance().getInjector().get(CacheableNodeIdentifier.class).identified();
        ClassElement bag = VirtueTransformer.getInstance().getInjector().get(BagIdentifier.class).identified();
        ClassElement queue = VirtueTransformer.getInstance().getInjector().get(QueueIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("L" + cacheableNode.name() + ";", true, 1).hasField("L" + bag.name() + ";", true, 1)
                .fieldCount(5).hasField("L" + queue.name() + ";", true, 1).hasField("I", true, 2).first();
    }

    public class Bag extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement bag = VirtueTransformer.getInstance().getInjector().get(BagIdentifier.class).identified();
            return new FieldQuery(CacheIdentifier.this.identified()).member().desc("L" + bag.name() + ";").first();
        }
    }

    public class Queue extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement queue = VirtueTransformer.getInstance().getInjector().get(QueueIdentifier.class).identified();
            return new FieldQuery(CacheIdentifier.this.identified()).member().desc("L" + queue.name() + ";").first();
        }
    }

    public class EmptyCacheableNode extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement cacheableNode = VirtueTransformer.getInstance().getInjector().get(CacheableNodeIdentifier.class).identified();
            return new FieldQuery(CacheIdentifier.this.identified()).member().desc("L" + cacheableNode.name() + ";").first();
        }
    }
}
