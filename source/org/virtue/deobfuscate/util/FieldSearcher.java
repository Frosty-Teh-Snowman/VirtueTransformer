package org.virtue.deobfuscate.util;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.MethodGen;

public class FieldSearcher {

	public class FieldInfo {
		public String fieldClass;
		public String fieldName;

		public FieldInfo(String fieldClass, String fieldName) {
			this.fieldClass = fieldClass;
			this.fieldName = fieldName;
		}
	}

	@SuppressWarnings("deprecation")
	public static Method findMethodWithField(ClassVector classes, FieldInfo fieldInfo) {
		Method foundMethod = null;
		for (ClassGen cg : classes) {
			for (Method m : cg.getMethods()) {
				MethodGen mg = new MethodGen(m, m.getName(), cg.getConstantPool());
				if (mg.getInstructionList() != null) {
					Instruction[] is = mg.getInstructionList().getInstructions();
					for (int i = 0; i < is.length; i++) {
						if (is[i] instanceof FieldInstruction) {
							FieldInstruction fi = (FieldInstruction) is[i];
							if (fi.getFieldName(cg.getConstantPool()).equals(fieldInfo.fieldName)
									&& fi.getClassName(cg.getConstantPool()).equals(fieldInfo.fieldClass)) {
								foundMethod = m;
							}
						}
					}
				}
			}
		}
		return foundMethod;
	}

}
