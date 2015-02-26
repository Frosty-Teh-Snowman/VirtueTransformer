package org.virtue.deobfuscate.identifiers.node;

import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.impl.jump.IfConditionNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.MethodVisitor;
import org.virtue.bytecode.utility.Value;
import org.virtue.deobfuscate.AbstractClassIdentifier;
import org.virtue.deobfuscate.AbstractFieldIdentifier;

/**
 * @author : const_
 */
public class CacheableNodeIdentifier extends AbstractClassIdentifier {

    private AbstractFieldIdentifier next;
    public CacheableNodeIdentifier() {
        add(next = new Next());
        add(new Previous());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.branchSize(5).firstOnBranch(3);
    }

    public class Next extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final ClassElement identified = CacheableNodeIdentifier.this.identified();
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(identified).member()
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
            ClassElement identified= CacheableNodeIdentifier.this.identified();
            return new FieldQuery(identified).member().notNamed(next.identified().name())
                    .desc("L" + identified.name() + ";").first();
        }
    }
}
