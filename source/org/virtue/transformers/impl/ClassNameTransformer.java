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
package org.virtue.transformers.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.TransformMode;
import org.virtue.transformers.Transformer;

/**
 * @author Kyle Friz
 * @since Feb 20, 2015
 */
public class ClassNameTransformer implements Transformer {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(ClassNameTransformer.class);

	private boolean obfuscation;
	
	public ClassNameTransformer(boolean obf) {
		this.obfuscation = obf;
	}
	
	/* (non-Javadoc)
	 * @see org.virtue.transformers.Transformer#initialize()
	 */
	public void initialization() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.virtue.transformers.Transformer#transform()
	 */
	public void transformation() {
		// TODO Auto-generated method stub

	}
	

	/* (non-Javadoc)
	 * @see org.virtue.transformers.Transformer#finalization()
	 */
	public void finalization() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.virtue.transformers.Transformer#getMode()
	 */
	public TransformMode getMode() {
		return obfuscation ? TransformMode.OBFUSCATE : TransformMode.DEOBFUSCATE;
	}
	
	private boolean containsNatives(ClassNode cNode) {
		for (Object node : cNode.methods) {
			MethodNode mNode = (MethodNode) node;
			if ((mNode.access & Opcodes.ACC_NATIVE) != 0)
				return true;
		}
		return false;
	}
}
