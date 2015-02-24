/*	
	Copyright 2012 Jan Ove Saltvedt
	
	This file is part of KBot.

    KBot is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    KBot is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with KBot.  If not, see <http://www.gnu.org/licenses/>.
	
 */

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
import org.virtue.deobfuscate.Injector;
import org.virtue.deobfuscate.transformer.Transformer;

public class LongShiftTransformer extends Transformer {
	
	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(LongShiftTransformer.class);
	
	private int fixedShifts;

	public LongShiftTransformer(Injector injector) {
		super(injector);
	}

	@Override
	public void deob(ClassGen classGen) {
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
			for (Iterator<InstructionHandle[]> iterator = instructionFinder.search("PushInstruction ((lshl)|(lshr))"); iterator
					.hasNext();) {
				InstructionHandle[] ih = iterator.next();
				if (ih[0].getInstruction() instanceof ConstantPushInstruction) {
					ConstantPushInstruction pushInstruction = (ConstantPushInstruction) ih[0].getInstruction();
					int realPush = pushInstruction.getValue().intValue() & 0x3D;
					if (pushInstruction instanceof ICONST) {
						ih[0].setInstruction(new ICONST(realPush));
						fixedShifts++;
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
						int realPush = ((Integer) ldc.getValue(cpg)) & 0x3D;
						ih[0].setInstruction(new LDC(cpg.addInteger(realPush)));
						fixedShifts++;
					} else if (ldc.getValue(cpg) instanceof Long) {
						int realPush = (int) (((Long) ldc.getValue(cpg)) & 0x3D);
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
		logger.info("Fixed " + fixedShifts + " long shifts.");
	}
}