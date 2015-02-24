package org.virtue.deobfuscate.transformer.impl;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.IMUL;
import org.apache.bcel.generic.IRETURN;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LMUL;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.UnconditionalBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.Injector;
import org.virtue.deobfuscate.transformer.Transformer;

public class MultiplicationTransformer extends Transformer {
	
	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(MultiplicationTransformer.class);
	
	private int clearedMultiplies;

	public MultiplicationTransformer(Injector injector) {
		super(injector);
	}

	@Override
	public void transform(ClassGen classGen) {
		ConstantPoolGen cpg = classGen.getConstantPool();
		int amount = 0;
		for (Method m : classGen.getMethods()) {
			if (m.isAbstract())
				continue;
			MethodGen methodGen = new MethodGen(m, classGen.getClassName(), cpg);
			InstructionList iList = methodGen.getInstructionList();
			if (iList == null)
				continue;
			Stack<Object> stack = new Stack<Object>();
			List<InstructionHandle> toDelete = new ArrayList<InstructionHandle>();
			List<InstructionHandle> alreadyVisited = new ArrayList<InstructionHandle>();
			analyzeStack(classGen, m, iList, 0, stack, toDelete, alreadyVisited);
			amount += toDelete.size();
			for (InstructionHandle handle : toDelete)
				updateTargeters(handle, toDelete);
			for (InstructionHandle handle : toDelete) {
				try {
					iList.delete(handle);
				} catch (TargetLostException exception) {
					exception.printStackTrace();
				}
				amount++;
			}

			iList.setPositions();
			methodGen.setInstructionList(iList);
			methodGen.setMaxLocals();
			methodGen.setMaxStack();
			classGen.replaceMethod(m, methodGen.getMethod());
		}
		clearedMultiplies += amount;
	//	System.out.println("Cleared " + amount + " multiplies in class " + classGen.getClassName() + ".");
	}

	private void updateTargeters(InstructionHandle handle, List<InstructionHandle> allHandles) {
		if (handle.getInstruction() instanceof LDC) {
			if (handle.hasTargeters()) {
				if (handle.getNext().getInstruction() instanceof IMUL && !allHandles.contains(handle.getPrev())) {
					for (InstructionTargeter targeter : handle.getTargeters())
						targeter.updateTarget(handle, handle.getPrev());
				} else {
					for (InstructionTargeter targeter : handle.getTargeters())
						targeter.updateTarget(handle, handle.getNext());
					if (allHandles.contains(handle.getNext()))
						updateTargeters(handle.getNext(), allHandles);
				}
			}
		} else if (handle.getInstruction() instanceof LDC2_W) {
			if (handle.hasTargeters()) {
				if (handle.getNext().getInstruction() instanceof LMUL && !allHandles.contains(handle.getPrev())) {
					for (InstructionTargeter targeter : handle.getTargeters())
						targeter.updateTarget(handle, handle.getPrev());
				} else {
					for (InstructionTargeter targeter : handle.getTargeters())
						targeter.updateTarget(handle, handle.getNext());
					if (allHandles.contains(handle.getNext()))
						updateTargeters(handle.getNext(), allHandles);
				}
			}
		} else if (handle.getInstruction() instanceof IMUL || handle.getInstruction() instanceof LMUL) {
			if (handle.hasTargeters()) {
				for (InstructionTargeter targeter : handle.getTargeters())
					targeter.updateTarget(handle, handle.getNext());
				if (allHandles.contains(handle.getNext()))
					updateTargeters(handle.getNext(), allHandles);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void analyzeStack(ClassGen classGen, Method m, InstructionList list, int startingPosition,
			Stack<Object> stack, List<InstructionHandle> toDelete, List<InstructionHandle> alreadyVisited) {
		ConstantPoolGen cpg = classGen.getConstantPool();
		stack = (Stack<Object>) stack.clone();
		Instruction[] instructions = list.getInstructions();
		InstructionHandle[] handles = list.getInstructionHandles();
		for (int i = startingPosition; i < instructions.length; i++) {
			Instruction ins = instructions[i];
			InstructionHandle handle = handles[i];
			int consumed = ins.consumeStack(cpg);
			int produced = ins.produceStack(cpg);
			for (int j = 0; j < consumed; j++) {
				try {
					Object o = stack.pop();
					if (o instanceof InstructionHandle) {
						InstructionHandle otherHandle = (InstructionHandle) o;
						Instruction otherInstruction = otherHandle.getInstruction();
						if (!((ins instanceof IMUL && otherInstruction instanceof LDC) || (ins instanceof LMUL && otherInstruction instanceof LDC2_W) ||(ins instanceof IADD && otherInstruction instanceof LDC)))
							continue;
						if (!toDelete.contains(handle))
							toDelete.add(handle);
						if (!toDelete.contains(otherHandle))
							toDelete.add(otherHandle);
					}
				} catch (EmptyStackException exception) {
					String mStr = m.toString();
					String[] parts = mStr.split("\\(")[0].split(" ");
					String string = classGen.getClassName() + "." + parts[parts.length - 1] + "("
							+ mStr.split("\\(")[1];
					System.err.println("Empty stack: " + handle.toString(true) + " in " + string);
					throw exception;
				}
			}
			for (int j = 0; j < produced; j++) {
				if (ins instanceof LDC && ((LDC) ins).getType(cpg) == Type.INT) {
					stack.push(handle);
				} else if (ins instanceof LDC2_W && ((LDC2_W) ins).getType(cpg) == Type.LONG) {
					stack.push(handle);
				} else
					stack.push(new Object());
			}
			if (consumed == Constants.UNPREDICTABLE)
				throw new RuntimeException("Cannot consume: " + ins);
			if (produced == Constants.UNPREDICTABLE)
				throw new RuntimeException("Cannot produce: " + ins);
			if (ins instanceof BranchInstruction) {
				if (alreadyVisited.contains(handle))
					break;
				alreadyVisited.add(handle);
				InstructionHandle target = list.findHandle(handle.getPosition() + ((BranchInstruction) ins).getIndex());
				int newIndex = indexOf(list, target);
				if (!(ins instanceof UnconditionalBranch)) {
					analyzeStack(classGen, m, list, newIndex, stack, toDelete, alreadyVisited);
				} else {
					if (newIndex == i)
						continue;
					i = newIndex - 1;
					continue;
				}
			}
			if (ins instanceof ReturnInstruction)
				break;
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
		logger.info("Cleared " + clearedMultiplies + " multiplies.");
	}
}
