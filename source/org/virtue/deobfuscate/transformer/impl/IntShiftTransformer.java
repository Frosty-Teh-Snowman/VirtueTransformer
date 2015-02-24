package org.virtue.deobfuscate.transformer.impl;

import java.util.Iterator;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.BIPUSH;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.SIPUSH;
import org.apache.bcel.util.InstructionFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.Injector;
import org.virtue.deobfuscate.transformer.Transformer;

public class IntShiftTransformer extends Transformer {
	
	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(IntShiftTransformer.class);
	
	private int fixedShifts;

	public IntShiftTransformer(Injector injector) {
		super(injector);
	}

	@Override
	public void transform(ClassGen classGen) {
		ConstantPoolGen cpg = classGen.getConstantPool();
		for (Method m : classGen.getMethods()) {
			if (m.isAbstract()) {
				continue;
			}
			MethodGen methodGen = new MethodGen(m, classGen.getClassName(), cpg);
			InstructionList iList = methodGen.getInstructionList();
			if (iList == null) {
				continue;
			}
			InstructionFinder instructionFinder = new InstructionFinder(iList);
			for (Iterator<InstructionHandle[]> iterator = instructionFinder.search("PushInstruction ((ishl)|(ishr))"); iterator
					.hasNext();) {
				InstructionHandle[] ih = iterator.next();
				if (ih[0].getInstruction() instanceof ConstantPushInstruction) {
					ConstantPushInstruction pushInstruction = (ConstantPushInstruction) ih[0].getInstruction();
					int realPush = pushInstruction.getValue().intValue() & 0x1f;
					if (pushInstruction instanceof ICONST) {
						ih[0].setInstruction(new ICONST(realPush));
						// fixedShifts++;
					} else if (pushInstruction instanceof SIPUSH) {
						ih[0].setInstruction(new SIPUSH((short) realPush));
						fixedShifts++;
					} else if (pushInstruction instanceof BIPUSH) {
						ih[0].setInstruction(new BIPUSH((byte) realPush));
						fixedShifts++;
					}
				} else if (ih[0].getInstruction() instanceof LDC) {
					LDC ldc = (LDC) ih[0].getInstruction();
					if (ldc.getValue(cpg) instanceof Integer) {
						int realPush = ((Integer) ldc.getValue(cpg)) & 0x1f;
						ih[0].setInstruction(new LDC(cpg.addInteger(realPush)));
						fixedShifts++;
					} else if (ldc.getValue(cpg) instanceof Long) {
						int realPush = (int) (((Long) ldc.getValue(cpg)) & 0x1f);
						ih[0].setInstruction(new LDC(cpg.addLong(realPush)));
						fixedShifts++;
					}
				}
			}
			methodGen.setInstructionList(iList);
			methodGen.setMaxLocals();
			methodGen.setMaxStack();
			classGen.replaceMethod(m, methodGen.getMethod());
		}
	}

	@Override
	public void finish() {
		logger.info("Fixed " + fixedShifts + " int shifts.");
	}
}
