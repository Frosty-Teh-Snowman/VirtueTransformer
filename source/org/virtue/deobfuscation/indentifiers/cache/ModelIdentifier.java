package org.virtue.deobfuscation.indentifiers.cache;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldStoreNode;
import org.virtue.bytecode.node.impl.method.VirtualMethodCallNode;
import org.virtue.bytecode.query.impl.ClassQuery;
import org.virtue.bytecode.query.impl.MethodQuery;
import org.virtue.bytecode.tree.method.MethodVisitor;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.deobfuscation.indentifiers.renderable.RenderableIdentifier;
import org.virtue.utility.ASMUtility;
import org.virtue.utility.Value;

/**
 * @author : const_
 */
public class ModelIdentifier extends AbstractClassIdentifier {

    public ModelIdentifier() {
        add(new TrianglesX());
        add(new TrianglesY());
        add(new TrianglesZ());
        add(new VerticesX());
        add(new VerticesY());
        add(new VerticesZ());
    }

    @Override
    public ClassElement identify() {
        ClassElement renderable = Injector.get(RenderableIdentifier.class).identified();
        ClassQuery query = new ClassQuery();
        final Value<ClassElement> value = new Value<>();
        for (final ClassElement possibleModel : query.branchSize(4).hasFieldLeast("[I", true, 6)) {
            for (MethodElement element : renderable.methods()) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualMethodCall(VirtualMethodCallNode node) {
                        if (!ASMUtility.isStandard("L" + node.owner() + ";")) {
                            if (node.owner().equals(possibleModel.name())) {
                                value.set(possibleModel);
                            }
                        }
                    }
                };
                if (value.set()) {
                    return value.value();
                }
            }
        }
        return null;
    }

    public class TrianglesX extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            final ClassElement model = ModelIdentifier.this.identified();
            for (MethodElement element : new MethodQuery().returns("L" + model.name() + ";")
                    .member().takes("I")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldStore(VirtualFieldStoreNode node) {
                        if (value.set()) {
                            return;
                        }
                        FieldElement trianglesX = node.field();
                        if (trianglesX.desc().equals("[I") && trianglesX.parent().name().equals(model.name())) {
                            VirtualFieldStoreNode trianglesY = node.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                            if (trianglesY != null && trianglesY.desc().equals("[I") &&
                                    trianglesY.owner().equals(model.name())) {
                                VirtualFieldStoreNode trianglesZ = trianglesY.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                if (trianglesZ != null && trianglesZ.desc().equals("[I") &&
                                        trianglesZ.owner().equals(model.name())) {
                                    VirtualFieldStoreNode array = trianglesZ.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                    if (array != null && array.owner().equals(model.name()) &&
                                            array.desc().equals("[B")) {
                                        value.set(trianglesX);
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

    public class TrianglesY extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            final ClassElement model = ModelIdentifier.this.identified();
            for (MethodElement element : new MethodQuery().returns("L" + model.name() + ";")
                    .member().takes("I")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldStore(VirtualFieldStoreNode node) {
                        if (value.set()) {
                            return;
                        }
                        FieldElement trianglesX = node.field();
                        if (trianglesX.desc().equals("[I") && trianglesX.parent().name().equals(model.name())) {
                            VirtualFieldStoreNode trianglesY = node.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                            if (trianglesY != null && trianglesY.desc().equals("[I") &&
                                    trianglesY.owner().equals(model.name())) {
                                VirtualFieldStoreNode trianglesZ = trianglesY.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                if (trianglesZ != null && trianglesZ.desc().equals("[I") &&
                                        trianglesZ.owner().equals(model.name())) {
                                    VirtualFieldStoreNode array = trianglesZ.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                    if (array != null && array.owner().equals(model.name()) &&
                                            array.desc().equals("[B")) {
                                        value.set(trianglesY.field());
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

    public class TrianglesZ extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            final ClassElement model = ModelIdentifier.this.identified();
            for (MethodElement element : new MethodQuery().returns("L" + model.name() + ";")
                    .member().takes("I")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldStore(VirtualFieldStoreNode node) {
                        if (value.set()) {
                            return;
                        }
                        FieldElement trianglesX = node.field();
                        if (trianglesX.desc().equals("[I") && trianglesX.parent().name().equals(model.name())) {
                            VirtualFieldStoreNode trianglesY = node.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                            if (trianglesY != null && trianglesY.desc().equals("[I") &&
                                    trianglesY.owner().equals(model.name())) {
                                VirtualFieldStoreNode trianglesZ = trianglesY.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                if (trianglesZ != null && trianglesZ.desc().equals("[I") &&
                                        trianglesZ.owner().equals(model.name())) {
                                    VirtualFieldStoreNode array = trianglesZ.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                    if (array != null && array.owner().equals(model.name()) &&
                                            array.desc().equals("[B")) {
                                        value.set(trianglesZ.field());
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

    public class VerticesX extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            final ClassElement model = ModelIdentifier.this.identified();
            for (MethodElement element : new MethodQuery().returns("L" + model.name() + ";")
                    .member().takes("I")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldStore(VirtualFieldStoreNode node) {
                        if (value.set()) {
                            return;
                        }
                        FieldElement verticesX = node.field();
                        if (verticesX.desc().equals("[I") && verticesX.parent().name().equals(model.name())) {
                            VirtualFieldStoreNode verticesY = node.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                            if (verticesY != null && verticesY.desc().equals("[I") &&
                                    verticesY.owner().equals(model.name())) {
                                VirtualFieldStoreNode verticesZ = verticesY.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                if (verticesZ != null && verticesZ.desc().equals("[I") &&
                                        verticesZ.owner().equals(model.name())) {
                                    VirtualFieldStoreNode length = verticesZ.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                    if (length != null && length.owner().equals(model.name()) &&
                                            length.desc().equals("I")) {
                                        value.set(verticesX);
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

    public class VerticesY extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            final ClassElement model = ModelIdentifier.this.identified();
            for (MethodElement element : new MethodQuery().returns("L" + model.name() + ";")
                    .member().takes("I")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldStore(VirtualFieldStoreNode node) {
                        if (value.set()) {
                            return;
                        }
                        FieldElement verticesX = node.field();
                        if (verticesX.desc().equals("[I") && verticesX.parent().name().equals(model.name())) {
                            VirtualFieldStoreNode verticesY = node.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                            if (verticesY != null && verticesY.desc().equals("[I") &&
                                    verticesY.owner().equals(model.name())) {
                                VirtualFieldStoreNode verticesZ = verticesY.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                if (verticesZ != null && verticesZ.desc().equals("[I") &&
                                        verticesZ.owner().equals(model.name())) {
                                    VirtualFieldStoreNode length = verticesZ.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                    if (length != null && length.owner().equals(model.name()) &&
                                            length.desc().equals("I")) {
                                        value.set(verticesY.field());
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

    public class VerticesZ extends AbstractFieldIdentifier {

        @Override
        public FieldElement identify() {
            final Value<FieldElement> value = new Value<>();
            final ClassElement model = ModelIdentifier.this.identified();
            for (MethodElement element : new MethodQuery().returns("L" + model.name() + ";")
                    .member().takes("I")) {
                MethodVisitor visitor = new MethodVisitor(element) {
                    @Override
                    public void visitVirtualFieldStore(VirtualFieldStoreNode node) {
                        if (value.set()) {
                            return;
                        }
                        FieldElement verticesX = node.field();
                        if (verticesX.desc().equals("[I") && verticesX.parent().name().equals(model.name())) {
                            VirtualFieldStoreNode verticesY = node.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                            if (verticesY != null && verticesY.desc().equals("[I") &&
                                    verticesY.owner().equals(model.name())) {
                                VirtualFieldStoreNode verticesZ = verticesY.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                if (verticesZ != null && verticesZ.desc().equals("[I") &&
                                        verticesZ.owner().equals(model.name())) {
                                    VirtualFieldStoreNode length = verticesZ.next(AbstractNode.VIRTUAL_FIELD_STORE_NODE, 6);
                                    if (length != null && length.owner().equals(model.name()) &&
                                            length.desc().equals("I")) {
                                        value.set(verticesZ.field());
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
}
