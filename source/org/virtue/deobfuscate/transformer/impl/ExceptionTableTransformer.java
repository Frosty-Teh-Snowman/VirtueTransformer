package org.virtue.deobfuscate.transformer.impl;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.TargetLostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.Injector;
import org.virtue.deobfuscate.transformer.Transformer;
import org.virtue.deobfuscate.utility.InstructionSearcher;

public class ExceptionTableTransformer extends Transformer {
	
	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(ExceptionTableTransformer.class);
	
	private int deletedExceptions;

	public ExceptionTableTransformer(Injector injector) {
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
			InstructionSearcher searcher = new InstructionSearcher(methodGen);
			for (CodeExceptionGen exceptionGen : methodGen.getExceptionHandlers()) {
				if (exceptionGen.getCatchType() != null
						&& exceptionGen.getCatchType().getClassName().equals("java.lang.RuntimeException")) {
					InstructionHandle start = exceptionGen.getHandlerPC();
					Instruction startIns = start.getInstruction();
					if (!(startIns instanceof NEW && ((NEW) startIns).getLoadClassType(cpg) != null && ((NEW) startIns)
							.getLoadClassType(cpg).getClassName().equals("java.lang.StringBuilder")))
						continue;
					methodGen.removeExceptionHandler(exceptionGen);
					exceptionGen.setHandlerPC(null);
					searcher.setCurrent(start);
					Instruction instruction;
					while ((instruction = searcher.getNext()) != null) {
						if (instruction instanceof ATHROW) {
							if (searcher.getCurrentHandle().hasTargeters())
								searcher.getPrev();
							try {
								iList.delete(start, searcher.getCurrentHandle());
							} catch (TargetLostException exception) {
								System.out.println(exceptionGen.getCatchType());
								exception.printStackTrace();
							}
							deletedExceptions++;
							break;
						}
					}
					break;
				}
			}
			iList.setPositions();
			methodGen.setInstructionList(iList);
			methodGen.removeLineNumbers();
			methodGen.setMaxLocals();
			methodGen.setMaxStack();
			classGen.replaceMethod(m, methodGen.getMethod());
		}
	}

	@Override
	public void finish() {
		logger.info("Removed " + deletedExceptions + " exception handlers.");
	}
}
