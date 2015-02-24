package org.virtue.deobfuscate.utility;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.util.InstructionFinder;

public class InstructionSearcher {

	private ConstantPoolGen cpg;
	private InstructionList instructionList;
	private InstructionHandle start = null;
	private InstructionHandle end = null;
	private InstructionHandle current = null;
	private int index = 0;

	public InstructionSearcher(ClassGen classGen, Method method) {
		this(new MethodGen(method, classGen.getClassName(), classGen.getConstantPool()).getInstructionList(), classGen
				.getConstantPool());
	}

	public InstructionSearcher(MethodGen method) {
		this(method.getInstructionList(), method.getConstantPool());
	}

	public InstructionSearcher(InstructionList instructionList, ConstantPoolGen cpg) {
		this.cpg = cpg;
		this.instructionList = instructionList;
		try {
			start = current = instructionList.getStart();
			end = instructionList.getEnd();
		} catch (NullPointerException e) {
		}
	}

	public Instruction getNext() {
		if (current == end || current == null) {
			current = null;
			return null;
		}
		current = current.getNext();
		if (current == null)
			return null;
		index++;
		return current.getInstruction();
	}

	public Instruction getPrev() {
		if (current == start || current == null) {
			current = null;
			return null;
		}
		current = current.getPrev();
		if (current == null)
			return null;
		index--;
		return current.getInstruction();
	}

	@SuppressWarnings("unchecked")
	public <T extends Instruction> T getNext(Class<T> type) {
		while (getNext() != null)
			if (type.isInstance(getCurrent()))
				return (T) getCurrent();
		return null;
	}

	public Instruction getNext(Object value) {
		while (getNext() != null)
			try {
				if (ConstantPushInstruction.class.isInstance(getCurrent())) {
					if (((ConstantPushInstruction) getCurrent()).getValue().equals(value))
						return getCurrent();
				} else if (LDC.class.isInstance(getCurrent()))
					if (((LDC) getCurrent()).getValue(cpg).equals(value))
						return getCurrent();
			} catch (Exception exception) {
			}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Instruction> T getPrev(Class<T> type) {
		while (getPrev() != null)
			if (type.isInstance(getCurrent()))
				return (T) getCurrent();
		return null;
	}

	public Instruction getCurrent() {
		if (current == null)
			return null;
		return current.getInstruction();
	}

	public InstructionHandle getCurrentHandle() {
		return current;
	}

	@SuppressWarnings("unchecked")
	public <T extends FieldInstruction> T getNextFieldInstruction(Class<T> type, String fieldType) {
		while (getNext(type) != null) {
			FieldInstruction fi = (FieldInstruction) getCurrent();
			if (fi.getFieldType(cpg).toString().equals(fieldType))
				return (T) getCurrent();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends InvokeInstruction> T getNextInvokeInstruction(Class<T> type, String methodType) {
		while (getNext(type) != null) {
			InvokeInstruction fi = (InvokeInstruction) getCurrent();
			if (fi.getSignature(cpg).equals(methodType))
				return (T) getCurrent();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Instruction> T getNext(Class<T> type, Object value) {
		while (getNext(value) != null)
			if (type.isInstance(getCurrent()))
				return (T) getCurrent();
		return null;
	}

	public LDC getNextLDC(Object value) {
		return getNext(LDC.class, value);
	}

	@SuppressWarnings("unchecked")
	public InstructionHandle[][] getPattern(Method method, String pattern) {
		ArrayList<InstructionHandle[]> results = new ArrayList<InstructionHandle[]>();
		InstructionFinder instructionFinder = new InstructionFinder(new InstructionList(method.getCode().getCode()));
		for (Iterator<InstructionHandle[]> it = instructionFinder.search(pattern); it.hasNext();) {
			results.add(it.next());
		}
		return results.toArray(new InstructionHandle[results.size()][]);
	}

	public void resetToStart() {
		current = start;
		index = 0;
	}

	public void resetToEnd() {
		current = end;
		index = instructionList.getLength() - 1;
	}

	public InstructionHandle getHandleForInstruction(Instruction instruction) {
		for (InstructionHandle handle : instructionList.getInstructionHandles()) {
			Instruction instructionForHandle = handle.getInstruction();
			if (instructionForHandle.equals(instruction)) {
				return handle;
			}
		}
		return null;
	}

	public ConstantPoolGen getConstantPoolGen() {
		return cpg;
	}

	public InstructionList getInstructionList() {
		return instructionList;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		int length = instructionList.getLength();
		if (index >= length || index < 0)
			throw new IndexOutOfBoundsException("param 0 (type int) is out of bounds");
		current = instructionList.getInstructionHandles()[index];
		this.index = index;
	}

	public void setCurrent(InstructionHandle current) {
		this.current = current;
	}

	public int getCount(Class<? extends Instruction> type) {
		int amount = 0;
		for (Instruction i : instructionList.getInstructions())
			if (type.isInstance(i))
				amount++;
		return amount;
	}

	public int getLength() {
		return instructionList.getLength();
	}

	public boolean isAtEnd() {
		return index >= instructionList.size();
	}
}
