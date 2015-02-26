package org.virtue.deobfuscation.identifiers.node;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.impl.jump.IfConditionNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.method.MethodVisitor;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.utility.Value;

/**
 * @author : const_
 */
public class BagIdentifier extends AbstractClassIdentifier {

    private AbstractFieldIdentifier next;
    public BagIdentifier(Injector injector) {
    	super(injector);
        add(new Cache());
        add(next = new Next());
        add(new Previous());
    }

    @Override
    public ClassElement identify() {
        ClassElement node = getInjector().get(NodeIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(1).hasField("[L" + node.name() + ";", true, 1).hasField("L" + node.name() + ";", true, 2)
                .fieldCount(5).hasField("I", true, 2).first();
    }

    public class Cache extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement node = getInjector().get(NodeIdentifier.class).identified();
            return new FieldQuery(BagIdentifier.this.identified()).desc("[L" + node.name() + ";").member().first();
        }
    }

    public class Next extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final ClassElement identified = getInjector().get(NodeIdentifier.class).identified();
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(BagIdentifier.this.identified()).member()
                    .returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitIfCondition(IfConditionNode node) {
                        if(value.set()) {
                            return;
                        }
                        if (node.conditon().hasField() && node.conditon().comparison().field().member() &&
                                node.conditon().comparison().field().desc().equals("L" + identified.name() + ";")) {
                            value.set(node.conditon().comparison().field());
                        }
                    }
                };
                if(value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class Previous extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement identified= getInjector().get(NodeIdentifier.class).identified();
            return new FieldQuery(BagIdentifier.this.identified()).member().notNamed(next.identified().name())
                    .desc("L" + identified.name() + ";").first();
        }
    }
}
