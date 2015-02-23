package org.virtue.deobfuscate.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConversionInstruction;
import org.apache.bcel.generic.D2I;
import org.apache.bcel.generic.D2L;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.DUP2;
import org.apache.bcel.generic.DUP2_X1;
import org.apache.bcel.generic.DUP2_X2;
import org.apache.bcel.generic.DUP_X1;
import org.apache.bcel.generic.DUP_X2;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.L2D;
import org.apache.bcel.generic.L2I;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.SWAP;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.StoreInstruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.generic.UnconditionalBranch;

public class ControlFlowAnalyzer {
	private final ConstantPoolGen constantPool;
	private final MethodGen methodGen;
	private final InstructionList list;
	private final List<ControlFlowVisitor> visitors;

	public ControlFlowAnalyzer(ClassGen classGen, Method method) {
		this(new MethodGen(method, classGen.getClassName(), classGen.getConstantPool()));
	}

	public ControlFlowAnalyzer(MethodGen methodGen) {
		this.methodGen = methodGen;
		constantPool = methodGen.getConstantPool();
		list = methodGen.getInstructionList();
		visitors = new ArrayList<ControlFlowVisitor>();
		if ("1".equals(""))
			addVisitor(new ControlFlowVisitor() {

				@Override
				public void visit(InstructionHandle handle, ControlFlowInstance instance) {
					String handleString = handle.getPosition() + ": ";
					if (handle.getInstruction() instanceof CPInstruction)
						handleString += handle.getInstruction().toString(constantPool.getConstantPool());
					else
						handleString += handle.getInstruction().toString(true);
					System.err.println("  " + handleString);
					System.err.println("    - Stack: " + instance.getStack());
					System.err.println("    - Local: " + Arrays.toString(Arrays.copyOf(instance.localVars, 10)));
				}

				@Override
				public void visitConsume(InstructionHandle handle, Type expected, Word consumed,
						ControlFlowInstance instance) {
				}

				@Override
				public void visitProduce(InstructionHandle handle, Word produced, ControlFlowInstance instance) {
				}

				@Override
				public void visitStored(InstructionHandle handle, int index, Word word, Word previous,
						ControlFlowInstance instance) {
				}

				@Override
				public void visitLoaded(InstructionHandle handle, int index, Word word, ControlFlowInstance instance) {
				}
			});
	}

	public void addVisitor(ControlFlowVisitor visitor) {
		visitors.add(visitor);
	}

	public void analyze() {
		System.err.println("Analyzing " + methodGen.getClassName() + "." + methodGen.getName()
				+ methodGen.getSignature());
		ControlFlowInstance instance = new ControlFlowInstance();
		Word[] localVariables = instance.getLocalVars();
		if (!methodGen.isStatic())
			localVariables[0] = new Word(new ObjectType(methodGen.getClassName()), (WordSource) null);
		Type[] argumentTypes = methodGen.getArgumentTypes();
		for (int i = 0; i < argumentTypes.length; i++)
			localVariables[i + (methodGen.isStatic() ? 0 : 1)] = new Word(argumentTypes[i], (WordSource) null);
		analyze(instance, 0);
	}

	protected void analyze(ControlFlowInstance instance, int startIndex) {
		Instruction[] instructions = list.getInstructions();
		InstructionHandle[] handles = list.getInstructionHandles();
		for (int i = startIndex; i < instructions.length; i++) {
			Instruction instruction = instructions[i];
			InstructionHandle handle = handles[i];
			Type[] consumed = calculateStackConsumed(instruction, instance);
			Word[] actualConsumed = new Word[consumed.length];
			for (int j = 0; j < consumed.length; j++) {
				try {
					actualConsumed[j] = instance.getStack().pop();
				} catch (EmptyStackException exception) {
					String handleString = handle.getPosition() + ": ";
					if (handle.getInstruction() instanceof CPInstruction)
						handleString += handle.getInstruction().toString(constantPool.getConstantPool());
					else
						handleString += handle.getInstruction().toString(true);
					System.err.println(methodGen.getClassName() + "." + methodGen.getName() + methodGen.getSignature()
							+ " at " + handleString + " consuming " + Arrays.toString(consumed));
					System.err.println("    - Stack: " + instance.getStack());
					System.err.println("    - Local: " + Arrays.toString(Arrays.copyOf(instance.localVars, 10)));
					throw exception;
				}
				for (ControlFlowVisitor visitor : visitors)
					visitor.visitConsume(handle, consumed[j], actualConsumed[j], instance);
			}
			for (Word produced : calculateStackProduced(instruction, handle, actualConsumed, instance)) {
				instance.getStack().push(produced);
				for (ControlFlowVisitor visitor : visitors)
					visitor.visitProduce(handle, produced, instance);
			}
			int[] loaded = calculateLocalLoaded(instruction, instance);
			for (int index : loaded) {
				Word word = instance.getLocalVars()[index];
				for (ControlFlowVisitor visitor : visitors)
					visitor.visitLoaded(handle, index, word, instance);
			}
			int[] stored = calculateLocalStored(instruction, instance);
			for (int j = 0; j < stored.length; j++) {
				int index = stored[j];
				Word previous = instance.getLocalVars()[index];
				Word word = actualConsumed[j];
				instance.getLocalVars()[index] = word;
				for (ControlFlowVisitor visitor : visitors)
					visitor.visitStored(handle, index, word, previous, instance);
			}

			for (ControlFlowVisitor visitor : visitors)
				visitor.visit(handle, instance);
			if (instruction instanceof Select) {
				List<InstructionHandle> branches = instance.getBranches();
				if (branches.contains(handle))
					break;
				branches.add(handle);
				Select select = (Select) instruction;
				for (int offset : select.getIndices()) {
					InstructionHandle target = list.findHandle(handle.getPosition() + offset);
					int newIndex = indexOf(target);
					analyze(new ControlFlowInstance(instance), newIndex);
				}
				break;
			} else if (instruction instanceof BranchInstruction) {
				List<InstructionHandle> branches = instance.getBranches();
				if (branches.contains(handle))
					break;
				branches.add(handle);
				InstructionHandle target = list.findHandle(handle.getPosition()
						+ ((BranchInstruction) instruction).getIndex());
				int newIndex = indexOf(target);
				if (instruction instanceof UnconditionalBranch) {
					if (newIndex == i)
						continue;
					i = newIndex - 1;
				} else
					analyze(new ControlFlowInstance(instance), newIndex);
			}
			if (instruction instanceof ReturnInstruction || instruction instanceof ATHROW)
				break;
		}
	}

	private int indexOf(InstructionHandle handle) {
		InstructionHandle[] handles = list.getInstructionHandles();
		for (int i = 0; i < handles.length; i++)
			if (handles[i] == handle)
				return i;
		throw new ArrayIndexOutOfBoundsException();
	}

	protected Word[] calculateStackProduced(Instruction instruction, InstructionHandle handle, Word[] consumed,
			ControlFlowInstance instance) {
		if (!(instruction instanceof StackProducer || instruction instanceof MULTIANEWARRAY
				|| instruction instanceof DUP_X1 || instruction instanceof DUP_X2 || instruction instanceof DUP2_X1 || instruction instanceof DUP2_X2))
			return new Word[0];

		List<Word> words = new ArrayList<Word>();
		if (instruction instanceof MULTIANEWARRAY) {
			words.add(new Word(((MULTIANEWARRAY) instruction).getType(constantPool), handle));
		} else if (instruction instanceof LoadInstruction) {
			Word localWord = instance.getLocalVars()[((LoadInstruction) instruction).getIndex()];
			for (int j = 0; j < instruction.produceStack(constantPool); j++)
				if (localWord != null) {
					words.add(new Word(localWord.getValue(), handle, localWord.getSource()));
				} else
					words.add(new Word(((LoadInstruction) instruction).getType(constantPool), handle));
		} else if (instruction instanceof TypedInstruction) {
			for (int j = 0; j < instruction.produceStack(constantPool); j++)
				words.add(new Word(((TypedInstruction) instruction).getType(constantPool), handle));
		} else if (instruction instanceof DUP) {
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
		} else if (instruction instanceof DUP_X1) {
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
			words.add(new Word(consumed[1].getValue(), handle, consumed[1].getSource()));
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
		} else if (instruction instanceof DUP_X2) {
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
			words.add(new Word(consumed[2].getValue(), handle, consumed[2].getSource()));
			words.add(new Word(consumed[1].getValue(), handle, consumed[1].getSource()));
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
		} else if (instruction instanceof DUP2) {
			words.add(new Word(consumed[1].getValue(), handle, consumed[1].getSource()));
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
			words.add(new Word(consumed[1].getValue(), handle, consumed[1].getSource()));
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
		} else if (instruction instanceof DUP2_X1) {
			words.add(new Word(consumed[1].getValue(), handle, consumed[1].getSource()));
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
			words.add(new Word(consumed[2].getValue(), handle, consumed[2].getSource()));
			words.add(new Word(consumed[1].getValue(), handle, consumed[1].getSource()));
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
		} else if (instruction instanceof DUP2_X2) {
			words.add(new Word(consumed[1].getValue(), handle, consumed[1].getSource()));
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
			words.add(new Word(consumed[3].getValue(), handle, consumed[3].getSource()));
			words.add(new Word(consumed[2].getValue(), handle, consumed[2].getSource()));
			words.add(new Word(consumed[1].getValue(), handle, consumed[1].getSource()));
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
		} else if (instruction instanceof FieldInstruction) {
			for (int i = 0; i < instruction.produceStack(constantPool); i++)
				words.add(new Word(((FieldInstruction) instruction).getFieldType(constantPool), handle));
		} else if (instruction instanceof InvokeInstruction) {
			Type ret = ((InvokeInstruction) instruction).getReturnType(constantPool);
			if (ret != null && ret != Type.VOID)
				words.add(new Word(ret, handle));
		} else if (instruction instanceof NEW) {
			words.add(new Word(((NEW) instruction).getType(constantPool), handle));
		} else if (instruction instanceof NEWARRAY) {
			words.add(new Word(((NEWARRAY) instruction).getType(), handle));
		} else if (instruction instanceof ANEWARRAY) {
			words.add(new Word(((ANEWARRAY) instruction).getType(constantPool), handle));
		} else if (instruction instanceof ArrayInstruction) {
			Word word = consumed[1];
			if (word != null && word.getValue() != null && word.getValue() instanceof TypeWordValue
					&& ((TypeWordValue) word.getValue()).getValue() instanceof ArrayType) {
				words.add(new Word(((ArrayType) ((TypeWordValue) word.getValue()).getValue()).getBasicType(), handle,
						word.getSource()));
			} else {
				System.out.println("Wrong variable found on stack: " + word + " " + instance.getStack());
				words.add(new Word(((ArrayInstruction) instruction).getType(constantPool), handle));
			}
		} else if (instruction instanceof ConversionInstruction) {
			if (instruction instanceof L2I || instruction instanceof D2I || instruction instanceof L2D
					|| instruction instanceof D2L) {
				System.out.println("Converting to L2I, consumed: " + Arrays.toString(consumed));
			}
			Word word = consumed[0];
			if (word != null && word.getValue() != null && word.getValue() instanceof TypeWordValue
					&& ((TypeWordValue) word.getValue()).getValue() instanceof BasicType) {
				for (int j = 0; j < instruction.produceStack(constantPool); j++)
					words.add(new Word(word.getValue(), handle, word.getSource()));
			} else
				for (int j = 0; j < instruction.produceStack(constantPool); j++)
					words.add(new Word(((ConversionInstruction) instruction).getType(constantPool), handle));
		} else if (instruction instanceof ARRAYLENGTH) {
			Word word = consumed[0];
			if (word != null && word.getValue() != null && word.getValue() instanceof TypeWordValue
					&& ((TypeWordValue) word.getValue()).getValue() instanceof ArrayType) {
				words.add(new Word(Type.INT, handle, word.getSource()));
			} else {
				System.out.println("Wrong variable found on stack: " + word + " " + instance.getStack());
				words.add(new Word(Type.INT, handle));
			}
		} else if (instruction instanceof SWAP) {
			words.add(new Word(consumed[0].getValue(), handle, consumed[0].getSource()));
			words.add(new Word(consumed[1].getValue(), handle, consumed[1].getSource()));
		} else
			System.out.println("Stack producer: " + instruction.getClass().getSimpleName() + " ("
					+ instruction.produceStack(constantPool) + ")");
		int actual = words.size();
		int expected = instruction.produceStack(constantPool);
		if (actual != expected) {
			System.err.println("Wrong amount produced for " + instruction.getClass().getSimpleName() + " (expected: "
					+ expected + ", found: " + actual + ")");
			for (int i = 0; i < expected - actual; i++)
				words.add(new Word((WordValue) null, handle));
		}
		return words.toArray(new Word[words.size()]);
	}

	protected Type[] calculateStackConsumed(Instruction instruction, ControlFlowInstance instance) {
		return new Type[instruction.consumeStack(constantPool)];
	}

	protected int[] calculateLocalStored(Instruction instruction, ControlFlowInstance instance) {
		if (!(instruction instanceof StoreInstruction))
			return new int[0];
		return new int[] { ((LocalVariableInstruction) instruction).getIndex() };
	}

	protected int[] calculateLocalLoaded(Instruction instruction, ControlFlowInstance instance) {
		if (!(instruction instanceof LoadInstruction))
			return new int[0];
		return new int[] { ((LocalVariableInstruction) instruction).getIndex() };
	}

	public ConstantPoolGen getConstantPool() {
		return constantPool;
	}

	public MethodGen getMethodGen() {
		return methodGen;
	}

	public InstructionList getList() {
		return list;
	}

	public class ControlFlowInstance {
		private final Stack<Word> stack;
		private final List<InstructionHandle> branches;
		private final Word[] localVars;

		private ControlFlowInstance() {
			stack = new Stack<Word>();
			localVars = new Word[256];
			branches = new ArrayList<InstructionHandle>();
		}

		@SuppressWarnings("unchecked")
		private ControlFlowInstance(ControlFlowInstance instance) {
			stack = (Stack<Word>) instance.getStack().clone();
			localVars = instance.getLocalVars().clone();
			branches = instance.getBranches();
		}

		public Stack<Word> getStack() {
			return stack;
		}

		public Word[] getLocalVars() {
			return localVars;
		}

		public List<InstructionHandle> getBranches() {
			return branches;
		}

		public ControlFlowAnalyzer getAnalyzer() {
			return ControlFlowAnalyzer.this;
		}
	}

	public class Word {
		private final WordValue value;
		private final WordSource source;

		private Word(Type type, InstructionHandle source) {
			this(new TypeWordValue(type), new WordSource(source));
		}

		private Word(Type type, InstructionHandle source, InstructionHandle parent) {
			this(new TypeWordValue(type), new WordSource(source, new WordSource(parent)));
		}

		private Word(Type type, WordSource source) {
			this(new TypeWordValue(type), source);
		}

		private Word(Type type, InstructionHandle source, WordSource parent) {
			this(new TypeWordValue(type), new WordSource(source, parent));
		}

		private Word(Type[] array, InstructionHandle source) {
			this(new ArrayWordValue(array), new WordSource(source));
		}

		private Word(Type[] array, InstructionHandle source, InstructionHandle parent) {
			this(new ArrayWordValue(array), new WordSource(source, new WordSource(parent)));
		}

		private Word(Type[] array, WordSource source) {
			this(new ArrayWordValue(array), source);
		}

		private Word(Type[] array, InstructionHandle source, WordSource parent) {
			this(new ArrayWordValue(array), new WordSource(source, parent));
		}

		private Word(WordValue value, InstructionHandle source) {
			this(value, new WordSource(source));
		}

		private Word(WordValue value, InstructionHandle source, InstructionHandle parent) {
			this(value, new WordSource(source, new WordSource(parent)));
		}

		private Word(WordValue value, InstructionHandle source, WordSource parent) {
			this(value, new WordSource(source, parent));
		}

		private Word(WordValue value, WordSource source) {
			this.value = value;
			this.source = source;
		}

		public WordValue getValue() {
			return value;
		}

		public WordSource getSource() {
			return source;
		}

		public ControlFlowAnalyzer getAnalyzer() {
			return ControlFlowAnalyzer.this;
		}

		@Override
		public String toString() {
			String value = this.value != null ? this.value.toString() : "null";
			if (source != null)
				return value + "<-" + source.toString();
			return value;
		}
	}

	public interface WordValue {
		public Object getValue();
	}

	public class TypeWordValue implements WordValue {
		private final Type type;

		private TypeWordValue(Type type) {
			this.type = type;
		}

		@Override
		public Type getValue() {
			return type;
		}

		@Override
		public String toString() {
			return type.toString();
		}
	}

	public class ArrayWordValue implements WordValue {
		private final Type[] array;

		private ArrayWordValue(Type[] array) {
			this.array = array.clone();
		}

		@Override
		public Type[] getValue() {
			return array.clone();
		}

		@Override
		public String toString() {
			return Arrays.toString(array);
		}
	}

	public class WordSource {
		private final InstructionHandle handle;
		private final WordSource parent;

		private WordSource(InstructionHandle handle) {
			this(handle, null);
		}

		private WordSource(InstructionHandle handle, WordSource parent) {
			this.handle = handle;
			this.parent = parent;
		}

		public InstructionHandle getHandle() {
			return handle;
		}

		public WordSource getParent() {
			return parent;
		}

		@Override
		public String toString() {
			if (parent != null)
				return handle.getInstruction().getName() + "<-" + parent.toString();
			return handle.getInstruction().getName();
		}
	}

	public static interface ControlFlowVisitor {
		public void visit(InstructionHandle handle, ControlFlowInstance instance);

		public void visitConsume(InstructionHandle handle, Type expected, Word consumed, ControlFlowInstance instance);

		public void visitProduce(InstructionHandle handle, Word produced, ControlFlowInstance instance);

		public void visitStored(InstructionHandle handle, int index, Word word, Word previous,
				ControlFlowInstance instance);

		public void visitLoaded(InstructionHandle handle, int index, Word word, ControlFlowInstance instance);
	}
}