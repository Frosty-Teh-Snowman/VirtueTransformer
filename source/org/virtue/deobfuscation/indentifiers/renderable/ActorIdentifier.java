package org.virtue.deobfuscation.indentifiers.renderable;

import org.objectweb.asm.Opcodes;
import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.DuplicateNode;
import org.virtue.bytecode.node.impl.ReturnNode;
import org.virtue.bytecode.node.impl.arith.ArithmeticOperationNode;
import org.virtue.bytecode.node.impl.field.StaticFieldCallNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldCallNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldStoreNode;
import org.virtue.bytecode.node.impl.jump.IfConditionNode;
import org.virtue.bytecode.node.impl.operand.PushNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.method.MethodVisitor;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.deobfuscation.indentifiers.ClientIdentifier;
import org.virtue.utility.Value;

/**
 * @author : const_
 */
public class ActorIdentifier extends AbstractClassIdentifier {

    private AbstractFieldIdentifier currentHealth;

    public ActorIdentifier() {
        add(new Speech());
        add(new Animation());
        add(new Orientation());
        add(currentHealth = new CurrentHealth());
        add(new MaxHealth());
        add(new InteractingIndex());
        add(new LocalX());
        add(new LocalY());
        add(new QueueX());
        add(new QueueY());
        add(new QueueSize());
        add(new LoopCycleStatus());
    }

    @Override
    public ClassElement identify() {
        ClassElement player = Injector.get(PlayerIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        return query.branchSize(5).onBranchAt(player, 0).abstractClass().firstOnBranch(1);
    }

    public class Speech extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            return new FieldQuery(ActorIdentifier.this.identified()).desc("Ljava/lang/String;").member().first();
        }
    }

    public class Animation extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(ActorIdentifier.this.identified()).takes("I")
                    .takes("Z").member().returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitIfCondition(IfConditionNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.conditon().hasConstant() && node.conditon().hasField() &&
                                node.conditon().constant().equals(-1)) {
                            FieldElement field = node.conditon().field();
                            if (field.parent().name().equals(ActorIdentifier.this.identified().name()) &&
                                    field.desc().equals("I") && field.member()) {
                                value.set(field);
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class Orientation extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().takes(ActorIdentifier.this.identified().name())
                    .returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitPush(PushNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.push() == 2047) {
                            VirtualFieldCallNode call = node.prev(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                            if (call != null && call.owner().equals(ActorIdentifier.this.identified().name()) &&
                                    call.desc().equals("I")) {
                                call = call.prev(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                                if (call != null && call.owner().equals(ActorIdentifier.this.identified().name()) &&
                                        call.desc().equals("I")) {
                                    value.set(call.field());
                                }
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class CurrentHealth extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery()) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitArithmeticOperation(ArithmeticOperationNode node) {
                        if (node.operation() == ArithmeticOperationNode.Operation.DIVIDE) {
                            VirtualFieldCallNode call = node.prev(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                            if (call != null && call.owner().equals(ActorIdentifier.this.identified().name()) &&
                                    call.desc().equals("I")) {
                                call = call.prev(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                                if (call != null && call.owner().equals(ActorIdentifier.this.identified().name()) &&
                                        call.desc().equals("I")) {
                                    value.set(call.field());
                                }
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class MaxHealth extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().takes("I")
                    .takes("L" + ActorIdentifier.this.identified().name() + ";")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitArithmeticOperation(ArithmeticOperationNode node) {
                        if (node.operation() == ArithmeticOperationNode.Operation.DIVIDE) {
                            VirtualFieldCallNode call = node.prev(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                            if (call != null && call.owner().equals(ActorIdentifier.this.identified().name()) &&
                                    call.desc().equals("I")) {
                                VirtualFieldCallNode call2 = call.prev(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                                if (call2 != null && call2.owner().equals(ActorIdentifier.this.identified().name()) &&
                                        call2.desc().equals("I") && call2.name().equals(currentHealth.identified().name())) {
                                    value.set(call.field());
                                }
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class InteractingIndex extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().takes("L" + ActorIdentifier.this.identified().name() + ";")
                    .notMember().returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {

                    @Override
                    public void visitDuplicate(DuplicateNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.opcode() == Opcodes.DUP) {
                            VirtualFieldCallNode call = node.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                            if (call != null && call.owner().equals(ActorIdentifier.this.identified().name()) &&
                                    call.desc().equals("I")) {
                                ArithmeticOperationNode add = call.next(AbstractNode.ARITHMETIC_OPERATION_NODE);
                                if (add != null && add.operation() == ArithmeticOperationNode.Operation.SUBTRACT) {
                                    VirtualFieldStoreNode store = call.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE);
                                    if (store != null && store.field().equals(call.field())) {
                                        value.set(store.field());
                                    }
                                }
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class LocalX extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(ActorIdentifier.this.identified()).
                    member().takes("I").takes("Z").returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("I") &&
                                node.owner().equals(ActorIdentifier.this.identified().name())) {
                            VirtualFieldCallNode queue = node.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                            if (queue != null && queue.desc().equals("[I") &&
                                    queue.owner().equals(ActorIdentifier.this.identified().name())) {
                                VirtualFieldStoreNode store = queue.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE);
                                if (store != null && store.owner().equals(ActorIdentifier.this.identified().name()) &&
                                        store.desc().equals("I") && store.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE) != null) {
                                    ArithmeticOperationNode add = store.prev(AbstractNode.ARITHMETIC_OPERATION_NODE);
                                    VirtualFieldCallNode node2 = store.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                                    if (add != null && add.operation() == ArithmeticOperationNode.Operation.ADD &&
                                            node2 != null && node2.field().equals(node.field())) {
                                        value.set(store.field());
                                    }
                                }
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class LocalY extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(ActorIdentifier.this.identified()).
                    member().takes("I").takes("Z").returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("I") &&
                                node.owner().equals(ActorIdentifier.this.identified().name())) {
                            VirtualFieldCallNode queue = node.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                            if (queue != null && queue.desc().equals("[I") &&
                                    queue.owner().equals(ActorIdentifier.this.identified().name())) {
                                VirtualFieldStoreNode store = queue.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE);
                                if (store != null && store.owner().equals(ActorIdentifier.this.identified().name()) &&
                                        store.desc().equals("I") && store.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE) == null) {
                                    ArithmeticOperationNode add = store.prev(AbstractNode.ARITHMETIC_OPERATION_NODE);
                                    ReturnNode returnNode = store.next(AbstractNode.RETURN_NODE, 3);
                                    if (add != null && add.operation() == ArithmeticOperationNode.Operation.ADD &&
                                            returnNode != null) {
                                        value.set(store.field());
                                    }
                                }
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }


    public class QueueX extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(ActorIdentifier.this.identified()).
                    member().takes("I").takes("Z").returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("I") &&
                                node.owner().equals(ActorIdentifier.this.identified().name())) {
                            VirtualFieldCallNode queue = node.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                            if (queue != null && queue.desc().equals("[I") &&
                                    queue.owner().equals(ActorIdentifier.this.identified().name())) {
                                VirtualFieldStoreNode store = queue.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE);
                                if (store != null && store.owner().equals(ActorIdentifier.this.identified().name()) &&
                                        store.desc().equals("I") && store.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE) != null) {
                                    ArithmeticOperationNode add = store.prev(AbstractNode.ARITHMETIC_OPERATION_NODE);
                                    VirtualFieldCallNode node2 = store.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                                    if (add != null && add.operation() == ArithmeticOperationNode.Operation.ADD &&
                                            node2 != null && node2.field().equals(node.field())) {
                                        value.set(queue.field());
                                    }
                                }
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class QueueY extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(ActorIdentifier.this.identified()).
                    member().takes("I").takes("Z").returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("I") &&
                                node.owner().equals(ActorIdentifier.this.identified().name())) {
                            VirtualFieldCallNode queue = node.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE);
                            if (queue != null && queue.desc().equals("[I") &&
                                    queue.owner().equals(ActorIdentifier.this.identified().name())) {
                                VirtualFieldStoreNode store = queue.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE);
                                if (store != null && store.owner().equals(ActorIdentifier.this.identified().name()) &&
                                        store.desc().equals("I") && store.next(AbstractNode.VIRTUAL_FIELD_CALL_NODE) == null) {
                                    ArithmeticOperationNode add = store.prev(AbstractNode.ARITHMETIC_OPERATION_NODE);
                                    ReturnNode returnNode = store.next(AbstractNode.RETURN_NODE, 3);
                                    if (add != null && add.operation() == ArithmeticOperationNode.Operation.ADD &&
                                            returnNode != null) {
                                        value.set(queue.field());
                                    }
                                }
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class QueueSize extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery(ActorIdentifier.this.identified()).takes("I")
                    .takes("Z").member().returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitIfCondition(IfConditionNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.conditon().hasConstant() && node.conditon().hasField() &&
                                node.conditon().constant().equals(9)) {
                            FieldElement field = node.conditon().field();
                            if (field.parent().name().equals(ActorIdentifier.this.identified().name()) &&
                                    field.desc().equals("I") && field.member()) {
                                value.set(field);
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }

    public class LoopCycleStatus extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().takes("I")
                    .takes("L" + ActorIdentifier.this.identified().name() + ";").notMember().returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
                        if(value.set()) {
                            return;
                        }
                        FieldElement field = node.field();
                        if(field.desc().equals("I") &&
                                field.parent().name().equals(ActorIdentifier.this.identified().name())) {
                            StaticFieldCallNode loopCycle = node.next(AbstractNode.STATIC_FIELD_CALL_NODE, 5);
                            if(loopCycle != null && loopCycle.field().equals(Injector.get(ClientIdentifier.class).loopCycle.identified())) {
                                value.set(field);
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
            return null;
        }
    }
}
