package org.virtue.deobfuscate.deobbers;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.generic.UnconditionalBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.deobfuscate.Injector;

public class DeadCodeDeobber extends Deobber {
	
	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(DeadCodeDeobber.class);
	
	private int deadCodeRemoved;

	public DeadCodeDeobber(Injector injector) {
		super(injector);
	}

	@Override
	public void deob(ClassGen classGen) {
		ConstantPoolGen cpg = classGen.getConstantPool();
		for (Method m : classGen.getMethods()) {
			if (m.isAbstract())
				continue;
			MethodGen methodGen = new MethodGen(m, classGen.getClassName(), cpg);
			InstructionList iList = methodGen.getInstructionList();
			if (iList == null)
				continue;
			Instruction[] instructions = iList.getInstructions();
			InstructionHandle[] handles = iList.getInstructionHandles();
			List<InstructionHandle> toDelete = new ArrayList<InstructionHandle>();
			instructionLoop: for (int i = 0; i < instructions.length; i++) {
				Instruction ins = instructions[i];
				InstructionHandle handle = handles[i];
				if (ins instanceof BranchInstruction && ins instanceof UnconditionalBranch) {
					InstructionHandle target = iList.findHandle(handle.getPosition()
							+ ((BranchInstruction) ins).getIndex());
					int newIndex = indexOf(iList, target);
					if (newIndex < i)
						continue;
					if ((newIndex == i && !handle.hasTargeters()) || newIndex == i + 1) {
						if (!toDelete.contains(handle))
							toDelete.add(handle);
						if (handle.hasTargeters()) {
							for (InstructionTargeter targeter : handle.getTargeters())
								targeter.updateTarget(handle, handle.getNext());
						}
						continue;
					}
					for (int j = i + 1; j < newIndex; j++) {
						if (handles[i].hasTargeters())
							continue instructionLoop;
						if (!toDelete.contains(handles[i]))
							toDelete.add(handles[i]);
					}
				}
			}
			for (InstructionHandle handle : toDelete) {
				try {
					iList.delete(handle);
				} catch (TargetLostException exception) {
					exception.printStackTrace();
				}
				deadCodeRemoved++;
			}
		}
	}

	private int indexOf(InstructionList list, InstructionHandle handle) {
		InstructionHandle[] handles = list.getInstructionHandles();
		for (int i = 0; i < handles.length; i++)
			if (handles[i] == handle)
				return i;
		throw new ArrayIndexOutOfBoundsException();
	}

	@Override
	public void finish() {
		logger.info("Cleared " + deadCodeRemoved + " unused instructions.");
	}

}
