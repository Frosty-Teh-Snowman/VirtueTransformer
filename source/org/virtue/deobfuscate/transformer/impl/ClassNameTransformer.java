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
package org.virtue.deobfuscate.transformer.impl;

import java.io.IOException;

import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.MethodGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.deobfuscate.Injector;
import org.virtue.deobfuscate.transformer.Transformer;

/**
 * @author Kyle Friz
 * @since Feb 22, 2015
 */
public class ClassNameTransformer extends Transformer {

	private int classID = 1;
	private int interfaceID = 1;
	private int fieldID = 1;
	private int methodID = 1;
	
	/**
	 * @param injector
	 */
	public ClassNameTransformer(Injector injector) {
		super(injector);
	}

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(ClassNameTransformer.class);

	/* (non-Javadoc)
	 * @see org.virtue.deobfuscate.deobbers.Deobber#deob(org.apache.bcel.generic.ClassGen)
	 */
	@Override
	public void deob(ClassGen classGen) {
		if (classGen.isInterface())
			classGen.setClassName("Interface" + (interfaceID++));
		else if (!natives(classGen))	
			classGen.setClassName("Class" + (classID++));
		
		ConstantPoolGen c_pool = classGen.getConstantPool();
		
		for (Method method : classGen.getMethods()) {
			if (method.isNative())
				continue;
			
			MethodGen methodGen = new MethodGen(method, classGen.getClassName(), c_pool);
			methodGen.setName("method" + (methodID++));
			
            classGen.removeMethod(method);
            classGen.addMethod(methodGen.getMethod());
		}
		
		for (Field field : classGen.getFields()) {
			if (field.isNative())
				continue;
			
			FieldGen fieldGen = new FieldGen(field, c_pool);
			fieldGen.setName("field" + (fieldID++));
			
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
		logger.info("Renamed " + (classID -1) + " Class(es), " + (interfaceID -1) + " Interface(s), " + (methodID -1) + " Method(s), " + (fieldID -1) + " Field(s).");
	}
}
