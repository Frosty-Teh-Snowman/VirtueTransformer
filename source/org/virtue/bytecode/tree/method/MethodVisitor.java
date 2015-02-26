package org.virtue.bytecode.tree.method;

import org.virtue.bytecode.element.MethodElement;
import org.virtue.bytecode.node.AbstractNode;
import org.virtue.bytecode.node.impl.BasicNode;
import org.virtue.bytecode.node.impl.DuplicateNode;
import org.virtue.bytecode.node.impl.LabelNode;
import org.virtue.bytecode.node.impl.ReturnNode;
import org.virtue.bytecode.node.impl.arith.ArithmeticConversionNode;
import org.virtue.bytecode.node.impl.arith.ArithmeticOperationNode;
import org.virtue.bytecode.node.impl.arith.BitwiseOperationNode;
import org.virtue.bytecode.node.impl.field.StaticFieldCallNode;
import org.virtue.bytecode.node.impl.field.StaticFieldStoreNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldCallNode;
import org.virtue.bytecode.node.impl.field.VirtualFieldStoreNode;
import org.virtue.bytecode.node.impl.jump.GotoNode;
import org.virtue.bytecode.node.impl.jump.IfConditionNode;
import org.virtue.bytecode.node.impl.method.DynamicMethodCallNode;
import org.virtue.bytecode.node.impl.method.InterfaceMethodCallNode;
import org.virtue.bytecode.node.impl.method.SpecialMethodCallNode;
import org.virtue.bytecode.node.impl.method.StaticMethodCallNode;
import org.virtue.bytecode.node.impl.method.VirtualMethodCallNode;
import org.virtue.bytecode.node.impl.operand.LdcNode;
import org.virtue.bytecode.node.impl.operand.LocalVariableCallNode;
import org.virtue.bytecode.node.impl.operand.LocalVariableStoreNode;
import org.virtue.bytecode.node.impl.operand.NumberConstantNode;
import org.virtue.bytecode.node.impl.operand.PushNode;
import org.virtue.bytecode.node.impl.type.ANewArrayNode;
import org.virtue.bytecode.node.impl.type.CastNode;
import org.virtue.bytecode.node.impl.type.InstanceOfNode;
import org.virtue.bytecode.node.impl.type.NewNode;

/**
 * @author : const_
 */
public class MethodVisitor {

    private MethodElement element;

    public MethodVisitor(MethodElement element) {
        this.element = element;
        visitAll();
    }

    private void visitAll() {
        if (element == null || element.instructions() == null) {
            return;
        }
        for (AbstractNode node : element.instructions()) {
            visit((BasicNode) node);
            switch (node.type()) {
                case AbstractNode.PUSH_NODE:
                    visitPush((PushNode) node);
                    break;
                case AbstractNode.NUMBER_CONSTANT_NODE:
                    visitNumberConstant((NumberConstantNode) node);
                    break;
                case AbstractNode.ARITHMETIC_CONVERSION_NODE:
                    visitArithmeticConversion((ArithmeticConversionNode) node);
                    break;
                case AbstractNode.ARITHMETIC_OPERATION_NODE:
                    visitArithmeticOperation((ArithmeticOperationNode) node);
                    break;
                case AbstractNode.BITWISE_OPERATION_NODE:
                    visitBitwiseOperation((BitwiseOperationNode) node);
                    break;
                case AbstractNode.LDC_NODE:
                    visitLdc((LdcNode) node);
                    break;
                case AbstractNode.LOCAL_VARIABLE_CALL_NODE:
                    visitLocalVariableCall((LocalVariableCallNode) node);
                    break;
                case AbstractNode.LOCAL_VARIABLE_STORE_NODE:
                    visitLocalVariableStore((LocalVariableStoreNode) node);
                    break;
                case AbstractNode.VIRTUAL_FIELD_CALL_NODE:
                    visitVirtualFieldCall((VirtualFieldCallNode) node);
                    break;
                case AbstractNode.VIRTUAL_FIELD_STORE_NODE:
                    visitVirtualFieldStore((VirtualFieldStoreNode) node);
                    break;
                case AbstractNode.RETURN_NODE:
                    visitReturn((ReturnNode) node);
                    break;
                case AbstractNode.STATIC_FIELD_CALL_NODE:
                    visitStaticFieldCall((StaticFieldCallNode) node);
                    break;
                case AbstractNode.STATIC_FIELD_STORE_NODE:
                    visitStaticFieldStore((StaticFieldStoreNode) node);
                    break;
                case AbstractNode.INTERFACE_METHOD_CALL_NODE:
                    visitInterfaceMethodCall((InterfaceMethodCallNode) node);
                    break;
                case AbstractNode.DYNAMIC_METHOD_CALL_NODE:
                    visitDynamicMethodCall((DynamicMethodCallNode) node);
                    break;
                case AbstractNode.SPECIAL_METHOD_CALL_NODE:
                    visitSpecialMethodCall((SpecialMethodCallNode) node);
                    break;
                case AbstractNode.STATIC_METHOD_CALL_NODE:
                    visitStaticMethodCall((StaticMethodCallNode) node);
                    break;
                case AbstractNode.VIRTUAL_METHOD_CALL_NODE:
                    visitVirtualMethodCall((VirtualMethodCallNode) node);
                    break;
                case AbstractNode.CAST_NODE:
                    visitCast((CastNode) node);
                    break;
                case AbstractNode.INSTANCE_OF_NODE:
                    visitInstanceOf((InstanceOfNode) node);
                    break;
                case AbstractNode.ANEW_ARRAY_NODE:
                    visitNewArray((ANewArrayNode) node);
                    break;
                case AbstractNode.NEW_NODE:
                    visitNew((NewNode) node);
                    break;
                case AbstractNode.GOTO_NODE:
                    visitGoto((GotoNode) node);
                    break;
                case AbstractNode.IF_CONDITION_NODE:
                    visitIfCondition((IfConditionNode) node);
                    break;
                case AbstractNode.LABEL_NODE:
                    visitLabel((LabelNode) node);
                    break;
                case AbstractNode.DUPLICATE_NODE:
                    visitDuplicate((DuplicateNode) node);
                    break;
            }
        }
    }

    public void visit(BasicNode node) {
    }

    public void visitCast(CastNode node) {
    }

    public void visitDuplicate(DuplicateNode node) {
    }

    public void visitInstanceOf(InstanceOfNode node) {
    }

    public void visitNew(NewNode node) {
    }

    public void visitGoto(GotoNode node) {
    }

    public void visitLabel(LabelNode node) {
    }

    public void visitIfCondition(IfConditionNode node) {
    }

    public void visitNewArray(ANewArrayNode node) {
    }

    public void visitVirtualFieldCall(VirtualFieldCallNode node) {
    }

    public void visitVirtualFieldStore(VirtualFieldStoreNode node) {
    }

    public void visitStaticFieldCall(StaticFieldCallNode node) {
    }

    public void visitStaticFieldStore(StaticFieldStoreNode node) {
    }

    public void visitDynamicMethodCall(DynamicMethodCallNode node) {
    }

    public void visitInterfaceMethodCall(InterfaceMethodCallNode node) {
    }

    public void visitSpecialMethodCall(SpecialMethodCallNode node) {
    }

    public void visitStaticMethodCall(StaticMethodCallNode node) {
    }

    public void visitVirtualMethodCall(VirtualMethodCallNode node) {
    }

    public void visitReturn(ReturnNode node) {
    }

    public void visitArithmeticConversion(ArithmeticConversionNode node) {
    }

    public void visitArithmeticOperation(ArithmeticOperationNode node) {
    }

    public void visitBitwiseOperation(BitwiseOperationNode node) {
    }

    public void visitLdc(LdcNode node) {
    }

    public void visitLocalVariableCall(LocalVariableCallNode node) {
    }

    public void visitLocalVariableStore(LocalVariableStoreNode node) {
    }

    public void visitNumberConstant(NumberConstantNode node) {
    }

    public void visitPush(PushNode node) {
    }

}
