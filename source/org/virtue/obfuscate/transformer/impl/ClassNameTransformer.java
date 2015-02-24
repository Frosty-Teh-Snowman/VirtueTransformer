/**
 * Copyright (c) 2015 Virtue Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.virtue.obfuscate.transformer.impl;

import java.util.Random;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.MethodGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.Injector;
import org.virtue.deobfuscate.transformer.Transformer;

/**
 * @author Kyle Friz
 * @since Feb 22, 2015
 */
public class ClassNameTransformer extends Transformer {

	private int classID = 1;
	private int fieldID = 1;
	private int methodID = 1;
	
	private char[] chars;
	private StringBuilder builder;
	private Random random;
	
	/**
	 * @param injector
	 */
	public ClassNameTransformer(Injector injector) {
		super(injector);
		this.random = new Random();
		this.chars = new String("aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789$_").toCharArray();
		bool = true;
	}
	boolean bool;
	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(ClassNameTransformer.class);

	/* (non-Javadoc)
	 * @see org.virtue.deobfuscate.deobbers.Deobber#deob(org.apache.bcel.generic.ClassGen)
	 */
	@Override
	public void transform(ClassGen classGen) {
		if (!natives(classGen))	{
			builder = new StringBuilder();
			for (int i = 0; i < 12; i++) {
			    char c = chars[random.nextInt(chars.length)];
			    builder.append(c);
			}
			if (Character.isDigit(builder.toString().charAt(0)))
				builder.setCharAt(0, chars[random.nextInt(chars.length - 11)]);
			
			classID++;
			classGen.setClassName(builder.toString());
		}
		
		ConstantPoolGen c_pool = classGen.getConstantPool();
		
		for (Method method : classGen.getMethods()) {
			if (method.isNative())
				continue;
			
			builder = new StringBuilder();
			for (int i = 0; i < 6; i++) {
			    char c = chars[random.nextInt(chars.length)];
			    builder.append(c);
			}
			if (Character.isDigit(builder.toString().charAt(0)))
				builder.setCharAt(0, chars[random.nextInt(chars.length - 11)]);
			
			MethodGen methodGen = new MethodGen(method, classGen.getClassName(), c_pool);
			methodGen.setName(builder.toString());
			
			methodID++;
			
            classGen.removeMethod(method);
            classGen.addMethod(methodGen.getMethod());
		}
		
		for (Field field : classGen.getFields()) {
			if (field.isNative())
				continue;
			
			builder = new StringBuilder();
			for (int i = 0; i < 6; i++) {
			    char c = chars[random.nextInt(chars.length)];
			    builder.append(c);
			}
			if (Character.isDigit(builder.toString().charAt(0)))
				builder.setCharAt(0, chars[random.nextInt(chars.length - 11)]);
			
			FieldGen fieldGen = new FieldGen(field, c_pool);
			fieldGen.setName(builder.toString());
			
			fieldID++;
			
			classGen.removeField(field);
			classGen.addField(fieldGen.getField());
		}
	}
	
	private boolean natives(ClassGen gen) {
		for (Field field : gen.getFields()) {
			if (field.isNative())
				return true;
		}
		for (Method method : gen.getMethods()) {
			if (method.isNative())
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.virtue.deobfuscate.deobbers.Deobber#finish()
	 */
	@Override
	public void finish() {
		logger.info("Renamed " + (classID -1) + " Class(es), " + (methodID -1) + " Method(s), " + (fieldID -1) + " Field(s).");
	}
}
