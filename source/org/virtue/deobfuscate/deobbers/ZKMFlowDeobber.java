package org.virtue.deobfuscate.deobbers;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.deobfuscate.Injector;
import org.virtue.deobfuscate.util.InstructionSearcher;

public class ZKMFlowDeobber extends Deobber {
	
	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(ZKMFlowDeobber.class);
	
	private int shiftedFlows;

	public ZKMFlowDeobber(Injector injector) {
		super(injector);
	}

	@Override
	public void deob(ClassGen classGen) {
		if (!classGen.getClassName().equals("ej"))
			return;
		ConstantPoolGen cpg = classGen.getConstantPool();
		for (Method m : classGen.getMethods()) {
			if (!m.getName().equals("<clinit>"))
				continue;
			if (m.isAbstract()) {
				continue;
			}
			MethodGen methodGen = new MethodGen(m, classGen.getClassName(), cpg);
			InstructionList iList = methodGen.getInstructionList();
			if (iList == null) {
				continue;
			}
			InstructionSearcher searcher = new InstructionSearcher(iList, classGen.getConstantPool());
			Instruction ins;
			insLoop: while ((ins = searcher.getNext()) != null) {
				InstructionHandle handle = searcher.getCurrentHandle();
				if (ins instanceof GOTO
						&& (((GOTO) ins).getTarget().getPrev() == null || (((GOTO) ins).getTarget().getPrev()
								.getInstruction() instanceof GOTO && !((GOTO) ((GOTO) ins).getTarget().getPrev()
								.getInstruction()).getTarget().equals(((GOTO) ins).getTarget())))) {
					searcher.setCurrent(((GOTO) ins).getTarget());
					Instruction target = searcher.getCurrent();
					while (target != null) {
						InstructionHandle targetHandle = searcher.getCurrentHandle();
						boolean invalid = false;
						if (handle.hasTargeters())
							for (InstructionTargeter targeter : handle.getTargeters())
								if (targeter instanceof BranchInstruction && !targeter.equals(ins))
									invalid = true;
						if (target.equals(ins) || invalid) {
							searcher.setCurrent(handle);
							continue insLoop;
						}
						if (target instanceof GOTO) {
							InstructionHandle newTarget = handle.getPrev();
							InstructionHandle start = ((GOTO) ins).getTarget();
							// System.out.println("Moving " + start +
							// " through "
							// + targetHandle + " to " + newTarget);
							if (handle.hasTargeters())
								for (InstructionTargeter targeter : handle.getTargeters())
									targeter.updateTarget(handle, newTarget);
							try {
								iList.delete(handle);
							} catch (TargetLostException exception) {
								exception.printStackTrace();
							}
							iList.move(start, targetHandle, newTarget);
							searcher = new InstructionSearcher(iList, classGen.getConstantPool());
							searcher.setCurrent(newTarget);
							shiftedFlows++;
							continue insLoop;
						}
						target = searcher.getNext();
					}
					searcher.setCurrent(handle);
				}
			}
			for (InstructionHandle handle : iList.getInstructionHandles()) {
				Instruction instruction = handle.getInstruction();
				if (instruction instanceof GOTO && ((GOTO) instruction).getTarget().equals(handle.getNext()))
					try {
						iList.delete(handle);
					} catch (TargetLostException exception) {
						exception.printStackTrace();
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
		logger.info("Moved " + shiftedFlows + " separated clauses.");
	}
}
