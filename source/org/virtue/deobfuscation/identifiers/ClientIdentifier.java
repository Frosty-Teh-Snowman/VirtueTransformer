package org.virtue.deobfuscation.identifiers;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.arith.ArithmeticOperationNode;
import org.virtue.bytecode.node.impl.field.StaticFieldCallNode;
import org.virtue.bytecode.node.impl.method.StaticMethodCallNode;
import org.virtue.bytecode.node.impl.operand.LocalVariableStoreNode;
import org.virtue.bytecode.node.impl.operand.PushNode;
import org.virtue.bytecode.node.impl.type.CastNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.FieldQuery;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.method.MethodVisitor;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.deobfuscation.identifiers.cache.CacheIdentifier;
import org.virtue.deobfuscation.identifiers.cache.WidgetIdentifier;
import org.virtue.deobfuscation.identifiers.cache.WidgetNodeIdentifier;
import org.virtue.deobfuscation.identifiers.cache.definition.ItemDefinitionIdentifier;
import org.virtue.deobfuscation.identifiers.cache.definition.NpcDefinitionIdentifier;
import org.virtue.deobfuscation.identifiers.cache.definition.ObjectDefinitionIdentifier;
import org.virtue.deobfuscation.identifiers.node.BagIdentifier;
import org.virtue.deobfuscation.identifiers.node.DequeIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.LootIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.NpcIdentifier;
import org.virtue.deobfuscation.identifiers.renderable.PlayerIdentifier;
import org.virtue.deobfuscation.identifiers.scene.CollisionMapIdentifier;
import org.virtue.deobfuscation.identifiers.scene.RegionIdentifier;
import org.virtue.utility.Value;

/**
 * @author : const_
 */
public class ClientIdentifier extends AbstractClassIdentifier {

    public AbstractFieldIdentifier widgetNodeBag, loopCycle;

    public ClientIdentifier(Injector injector) {
    	super(injector);
        add(new LocalPlayer());
        add(new Players());
        add(new Npcs());
        add(new ItemDefinitionCache());
        add(new ObjectDefinitionCache());
        add(new NpcDefinitionCache());
        add(new Region());
        add(new LootDeque());
        add(new Canvas());
        add(widgetNodeBag = new WidgetNodeBag());
        add(new Widgets());
        add(new CollisionMap());
        add(loopCycle = new LoopCycle());
        add(new CameraX());
        add(new CameraY());
        add(new CameraZ());
        add(new CameraPitch());
        add(new CameraYaw());
        add(new SineTable());
        add(new CosineTable());
        add(new Plane());
    }

    @Override
    public ClassElement identify() {
        ClassQuery query = new ClassQuery();
        return query.named("client").first();
    }

    public class CameraX extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().returns("V")
                    .takes("I").constant(16, 128)) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("I")) {
                            LocalVariableStoreNode store = node.next(AbstractNode.LOCAL_VARIABLE_STORE_NODE, 6);
                            StaticMethodCallNode method = node.next(AbstractNode.STATIC_METHOD_CALL_NODE, 6);
                            if (store != null && method == null &&
                                    store.index() == 0 && store.desc().equals("I")) {
                                ArithmeticOperationNode sub = store.prev(AbstractNode.ARITHMETIC_OPERATION_NODE, 3);
                                if (sub != null && sub.operation() == ArithmeticOperationNode.Operation.SUBTRACT) {
                                    value.set(node.field());
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

    public class CameraY extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().returns("V")
                    .takes("I").constant(16, 128)) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("I")) {
                            LocalVariableStoreNode store = node.next(AbstractNode.LOCAL_VARIABLE_STORE_NODE, 6);
                            StaticMethodCallNode method = node.next(AbstractNode.STATIC_METHOD_CALL_NODE, 6);
                            if (store != null && method == null &&
                                    store.index() == 1 && store.desc().equals("I")) {
                                ArithmeticOperationNode sub = store.prev(AbstractNode.ARITHMETIC_OPERATION_NODE, 3);
                                if (sub != null && sub.operation() == ArithmeticOperationNode.Operation.SUBTRACT) {
                                    value.set(node.field());
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

    public class CameraZ extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().returns("V")
                    .takes("I").constant(16, 128)) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("I")) {
                            LocalVariableStoreNode store = node.next(AbstractNode.LOCAL_VARIABLE_STORE_NODE, 6);
                            StaticMethodCallNode method = node.next(AbstractNode.STATIC_METHOD_CALL_NODE, 6);
                            if (store != null && method == null &&
                                    store.index() == 3 && store.desc().equals("I")) {
                                ArithmeticOperationNode sub = store.prev(AbstractNode.ARITHMETIC_OPERATION_NODE, 3);
                                if (sub != null && sub.operation() == ArithmeticOperationNode.Operation.SUBTRACT) {
                                    value.set(node.field());
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

    public class CameraPitch extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().returns("V")
                    .takes("I").constant(16, 128)) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("[I")) {
                            FieldElement sineTable = node.field();
                            StaticFieldCallNode pitch = node.next(AbstractNode.STATIC_FIELD_CALL_NODE, 3);
                            if (pitch != null && pitch.desc().equals("I")) {
                                LocalVariableStoreNode store = pitch.next(AbstractNode.LOCAL_VARIABLE_STORE_NODE, 6);
                                if (store != null && store.desc().equals("I") &&
                                        store.index() == 4) {
                                    value.set(pitch.field());
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

    public class SineTable extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().returns("V")
                    .takes("I").constant(16, 128)) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("[I")) {
                            FieldElement sineTable = node.field();
                            StaticFieldCallNode pitch = node.next(AbstractNode.STATIC_FIELD_CALL_NODE, 3);
                            if (pitch != null && pitch.desc().equals("I")) {
                                LocalVariableStoreNode store = pitch.next(AbstractNode.LOCAL_VARIABLE_STORE_NODE, 6);
                                if (store != null && store.desc().equals("I") &&
                                        store.index() == 4) {
                                    value.set(sineTable);
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

    public class CameraYaw extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().returns("V")
                    .takes("I").constant(16, 128)) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("[I")) {
                            FieldElement cosineTable = node.field();
                            StaticFieldCallNode yaw = node.next(AbstractNode.STATIC_FIELD_CALL_NODE, 3);
                            if (yaw != null && yaw.desc().equals("I")) {
                                LocalVariableStoreNode store = yaw.next(AbstractNode.LOCAL_VARIABLE_STORE_NODE, 6);
                                if (store != null && store.desc().equals("I") &&
                                        store.index() == 6) {
                                    value.set(yaw.field());
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

    public class CosineTable extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().returns("V")
                    .takes("I").constant(16, 128)) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("[I")) {
                            FieldElement cosineTable = node.field();
                            StaticFieldCallNode yaw = node.next(AbstractNode.STATIC_FIELD_CALL_NODE, 3);
                            if (yaw != null && yaw.desc().equals("I")) {
                                LocalVariableStoreNode store = yaw.next(AbstractNode.LOCAL_VARIABLE_STORE_NODE, 6);
                                if (store != null && store.desc().equals("I") &&
                                        store.index() == 7) {
                                    value.set(cosineTable);
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

    public class Plane extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().notMember().returns("V")
                    .takes("I").constant(16, 128)) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitStaticFieldCall(StaticFieldCallNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals("I")) {
                            FieldElement plane = node.field();
                            StaticMethodCallNode method = node.next(AbstractNode.STATIC_METHOD_CALL_NODE, 6);
                            if (method != null && method.desc().endsWith(")I")) {
                                ArithmeticOperationNode operation = method.next(AbstractNode.ARITHMETIC_OPERATION_NODE, 5);
                                if (operation != null && operation.operation() == ArithmeticOperationNode.Operation.SUBTRACT) {
                                    value.set(plane);
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


    public class LoopCycle extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().returns("V").member()) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitPush(PushNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.push() == 1000) {
                            StaticFieldCallNode call = node.prev(AbstractNode.STATIC_FIELD_CALL_NODE, 3);
                            ArithmeticOperationNode mul = node.next(AbstractNode.ARITHMETIC_OPERATION_NODE, 6);
                            if (mul != null && (mul.operation() == ArithmeticOperationNode.Operation.DIVIDE
                                    || mul.next(AbstractNode.ARITHMETIC_OPERATION_NODE, 6) != null &&
                                    ((ArithmeticOperationNode) mul.
                                            next(AbstractNode.ARITHMETIC_OPERATION_NODE, 6)).
                                            operation() == ArithmeticOperationNode.Operation.DIVIDE) &&
                                    call != null && call.desc().equals("I")) {
                                value.set(call.field());
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

    public class LootDeque extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            ClassElement loot = getInjector().get(LootIdentifier.class).identified();
            ClassElement deque = getInjector().get(DequeIdentifier.class).identified();
            FieldQuery query = new FieldQuery(new MethodQuery().hasCast(loot.name()).
                    hasFieldDesc("[[[L" + deque.name() + ";", false).returns("V").all());
            return query.desc("[[[L" + deque.name() + ";").first();
        }
    }

    public class Widgets extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement widget = getInjector().get(WidgetIdentifier.class).identified();
            return new FieldQuery().notMember().desc("[[L" + widget.name() + ";").first();
        }
    }

    public class CollisionMap extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            ClassElement collisionMap = getInjector().get(CollisionMapIdentifier.class).identified();
            return new FieldQuery().notMember().desc("[L" + collisionMap.name() + ";").first();
        }
    }

    public class WidgetNodeBag extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            final ClassElement widgetNode = getInjector().get(WidgetNodeIdentifier.class).identified();
            final ClassElement bag = getInjector().get(BagIdentifier.class).identified();
            final Value<FieldElement> value = new Value<>();
            for (MethodElement element : new MethodQuery().hasCast(widgetNode.name()).
                    hasFieldDesc("L" + bag.name() + ";", false).returns("V")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitCast(CastNode node) {
                        if (value.set()) {
                            return;
                        }
                        if (node.desc().equals(widgetNode.name())) {
                            AbstractNode prev = node;
                            while ((prev = prev.prev(AbstractNode.STATIC_FIELD_CALL_NODE)) != null) {
                                StaticFieldCallNode call = (StaticFieldCallNode) prev;
                                if (call.desc().equals("L" + bag.name() + ";")) {
                                    value.set(call.field());
                                    break;
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


    public class Region extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            FieldQuery query = new FieldQuery();
            ClassElement region = getInjector().get(RegionIdentifier.class).identified();
            return query.desc("L" + region.name() + ";").notMember().first();
        }
    }

    public class LocalPlayer extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            FieldQuery query = new FieldQuery();
            ClassElement player = getInjector().get(PlayerIdentifier.class).identified();
            return query.desc("L" + player.name() + ";").notMember().first();
        }
    }

    public class Players extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            FieldQuery query = new FieldQuery();
            ClassElement player = getInjector().get(PlayerIdentifier.class).identified();
            return query.desc("[L" + player.name() + ";").notMember().first();
        }
    }

    public class Npcs extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            FieldQuery query = new FieldQuery();
            ClassElement npc = getInjector().get(NpcIdentifier.class).identified();
            return query.desc("[L" + npc.name() + ";").notMember().first();
        }
    }

    public class Canvas extends AbstractFieldIdentifier {
        @Override
        public FieldElement identify() {
            FieldQuery query = new FieldQuery();
            return query.desc("Ljava/awt/Canvas;").notMember().first();
        }
    }

    public class ItemDefinitionCache extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            MethodQuery methodQuery = new MethodQuery();
            ClassElement itemDef = getInjector().get(ItemDefinitionIdentifier.class).identified();
            ClassElement cache = getInjector().get(CacheIdentifier.class).identified();
            FieldQuery query = new FieldQuery(methodQuery.returns("L" + itemDef.name() + ";").all()).desc("L" + cache.name() + ";");
            return query.first();
        }
    }

    public class ObjectDefinitionCache extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            MethodQuery methodQuery = new MethodQuery();
            ClassElement objectDef = getInjector().get(ObjectDefinitionIdentifier.class).identified();
            ClassElement cache = getInjector().get(CacheIdentifier.class).identified();
            FieldQuery query = new FieldQuery(methodQuery.returns("L" + objectDef.name() + ";").all()).desc("L" + cache.name() + ";");
            return query.first();
        }
    }

    public class NpcDefinitionCache extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            MethodQuery methodQuery = new MethodQuery();
            ClassElement npcDef = getInjector().get(NpcDefinitionIdentifier.class).identified();
            ClassElement cache = getInjector().get(CacheIdentifier.class).identified();
            FieldQuery query = new FieldQuery(methodQuery.returns("L" + npcDef.name() + ";").all()).desc("L" + cache.name() + ";");
            return query.first();
        }
    }
}
