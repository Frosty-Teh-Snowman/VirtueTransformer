package org.virtue.deobfuscate.transformer.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.InstructionFinder;
import org.virtue.deobfuscate.Injector;
import org.virtue.deobfuscate.transformer.Transformer;

public class EuclideanNumberDeobber extends Transformer {
	private final Map<String, List<EuclideanNumberPair>> values = new HashMap<String, List<EuclideanNumberPair>>();
	private Map<String, EuclideanNumberPair> finalValues;

	public EuclideanNumberDeobber(Injector injector) {
		super(injector);
	}

	@Override
	public void deob(ClassGen classGen) {
		String[] patterns = { "((ldc|ldc_w) (getstatic|getfield) imul)|(ldc2_w (getstatic|getfield) lmul)",
				"(getstatic|getfield) (((ldc|ldc_w) imul)|(ldc2_w lmul))",
				"(((ldc|ldc_w) imul)|(ldc2_w lmul)) (putstatic|putfield)" };
		int[] ldcIndices = { 0, 1, 0 };
		int[] fieldIndices = { 1, 0, 2 };
		ConstantPoolGen cpg = classGen.getConstantPool();
		for (Method m : classGen.getMethods()) {
			if (m.isAbstract())
				continue;
			MethodGen methodGen = new MethodGen(m, classGen.getClassName(), cpg);
			InstructionList iList = methodGen.getInstructionList();
			if (iList == null)
				continue;
			Type[] argTypes = methodGen.getArgumentTypes();
			int maxLocalArg = (methodGen.isStatic() ? 1 : 0) + argTypes.length;
			boolean[] ints = new boolean[maxLocalArg];
			boolean[] longs = new boolean[maxLocalArg];
			for (int i = 0; i < argTypes.length; i++) {
				ints[i + (methodGen.isStatic() ? 1 : 0)] = argTypes[i] == Type.INT;
				longs[i + (methodGen.isStatic() ? 1 : 0)] = argTypes[i] == Type.LONG;
			}
			InstructionFinder instructionFinder = new InstructionFinder(iList);
			for (int i = 0; i < patterns.length; i++) {
				for (@SuppressWarnings("unchecked")
				Iterator<InstructionHandle[]> iterator = instructionFinder.search(patterns[i]); iterator.hasNext();) {
					InstructionHandle[] ih = iterator.next();
					FieldInstruction fi = (FieldInstruction) ih[fieldIndices[i]].getInstruction();
					String descriptor = fi.getReferenceType(cpg).toString() + "." + fi.getFieldName(cpg);
					Number value;
					int ldcIndex = ldcIndices[i];
					if (ih[ldcIndex].getInstruction() instanceof LDC) {
						if (((LDC) ih[ldcIndex].getInstruction()).getType(cpg) != Type.INT)
							continue;
						value = (Integer) ((LDC) ih[ldcIndex].getInstruction()).getValue(cpg);
					} else if (ih[ldcIndex].getInstruction() instanceof LDC2_W) {
						if (((LDC2_W) ih[ldcIndex].getInstruction()).getType(cpg) != Type.LONG)
							continue;
						value = ((LDC2_W) ih[ldcIndex].getInstruction()).getValue(cpg);
					} else
						continue;
					if ((value.longValue() & 1) == 0)
						continue;
					List<EuclideanNumberPair> values = this.values.get(descriptor);
					if (values == null) {
						values = new ArrayList<EuclideanNumberPair>();
						this.values.put(descriptor, values);
					}
					if (fi instanceof GETFIELD || fi instanceof GETSTATIC)
						values.add(decipherP(BigInteger.valueOf(value.longValue()), value instanceof Long ? 64 : 32,
								new AtomicBoolean(false)));
					else
						values.add(decipherQ(BigInteger.valueOf(value.longValue()), value instanceof Long ? 64 : 32,
								new AtomicBoolean(false)));
				}
			}

			iList.setPositions();
			methodGen.setInstructionList(iList);
			methodGen.setMaxLocals();
			methodGen.setMaxStack();
			classGen.replaceMethod(m, methodGen.getMethod());
		}
	}

	@Override
	public void finish() {
		try {
			Map<String, EuclideanNumberPair> finalValues = new HashMap<String, EuclideanNumberPair>();
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./de_obf/local/out.txt")));
			for (String key : values.keySet()) {
				List<EuclideanNumberPair> values = this.values.get(key);
				List<EuclideanNumberPair> nonDuplicates = new ArrayList<EuclideanNumberPair>();
				Map<EuclideanNumberPair, AtomicInteger> counts = new HashMap<EuclideanNumberPair, AtomicInteger>();
				for (EuclideanNumberPair number : values) {
					AtomicInteger amount = counts.get(number);
					if (amount == null) {
						amount = new AtomicInteger();
						counts.put(number, amount);
					}
					amount.incrementAndGet();
					if (!nonDuplicates.contains(number))
						nonDuplicates.add(number);
				}
				Object value;
				List<EuclideanNumberPair> highestValues;
				if (nonDuplicates.size() > 1) {
					highestValues = new ArrayList<EuclideanNumberPair>();
					writer.write("# More than one value for " + key + ": " + nonDuplicates);
					writer.newLine();
					int highestCount = 0;
					for (EuclideanNumberPair nonDuplicate : nonDuplicates) {
						if (nonDuplicate.isUnsafe())
							continue;
						int count = counts.get(nonDuplicate).get();
						if (highestValues.isEmpty() || count > highestCount) {
							highestValues.clear();
							highestValues.add(nonDuplicate);
							highestCount = count;
						} else if (count == highestCount)
							highestValues.add(nonDuplicate);
					}
					if (highestValues.size() > 1) {
						writer.write("# More than one value with the same occurence for " + key + ": " + highestValues);
						writer.newLine();
						value = highestValues;
					} else {
						writer.write("# Resolved conflict for " + key + ": " + highestValues.get(0));
						writer.newLine();
						value = highestValues.get(0);
					}
				} else
					value = (highestValues = nonDuplicates).get(0);
				finalValues.put(key, highestValues.get(0));
				writer.write(key + ": " + value);
				writer.newLine();
			}
			writer.close();
			this.finalValues = Collections.unmodifiableMap(finalValues);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public Map<String, EuclideanNumberPair> getFinalPairs() {
		return finalValues;
	}

	private EuclideanNumberPair decipherP(BigInteger product, int bits, AtomicBoolean unsafe) {
		BigInteger quotient = inverse(product, bits);
		return decipher(product, quotient, bits, unsafe);

	}

	private EuclideanNumberPair decipherQ(BigInteger quotient, int bits, AtomicBoolean unsafe) {
		BigInteger product = inverse(quotient, bits);
		return decipher(product, quotient, bits, unsafe);
	}

	private EuclideanNumberPair decipher(BigInteger product, BigInteger quotient, int bits, AtomicBoolean unsafe) {
		BigInteger g = product.gcd(quotient);

		if (g.longValue() != 1) {// Double check common divisor
			// Make sure that "g" truly is common.
			long v1 = product.divide(g).multiply(quotient).longValue();
			long v2 = quotient.divide(g).multiply(product).longValue();

			if (v1 != v2)
				unsafe.set(true);
		}

		return new EuclideanNumberPair(product, quotient, g, bits, unsafe.get());
	}

	private BigInteger inverse(BigInteger val, int bits) {
		BigInteger shift = BigInteger.ONE.shiftLeft(bits);
		return val.modInverse(shift);
	}

	public static final class EuclideanNumberPair {

		/**
		 * The greatest common divisor, product, and quotient. Product is used
		 * to encode values, where quotient is used to decode them. GCD usually
		 * = product * quotient. True value is the decoded value {@see <init>}
		 */
		private BigInteger product, quotient, gcd, trueValue;

		/**
		 * If the unsafe flag is flagged {@code true}, the gcd was not <1>. This
		 * is a very bad thing.
		 */
		private boolean unsafe;

		/**
		 * The amount of bits in this pair of numbers. 32 = int, 64 = long
		 */
		private int bits;

		public EuclideanNumberPair(BigInteger product, BigInteger quotient, BigInteger gcd, int bits, boolean unsafe) {
			this.product = product;
			this.quotient = quotient;
			this.gcd = gcd;
			this.bits = bits;

			BigInteger k = gcd.multiply(product);
			trueValue = quotient.multiply(k);
		}

		public BigInteger product() {
			return product;
		}

		public BigInteger quotient() {
			return quotient;
		}

		public BigInteger gcd() {
			return gcd;
		}

		public BigInteger trueValue() {
			return trueValue;
		}

		public int bits() {
			return bits;
		}

		public boolean isUnsafe() {
			return unsafe;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EuclideanNumberPair))
				return false;
			EuclideanNumberPair pair = (EuclideanNumberPair) obj;
			return product.equals(pair.product) && quotient.equals(pair.quotient);
		}

		@Override
		public String toString() {
			return trueValue + "{p=" + product + ",q=" + quotient + ",g=" + gcd + "}";
		}

		@Override
		public int hashCode() {
			return trueValue.intValue();
		}
	}
}
