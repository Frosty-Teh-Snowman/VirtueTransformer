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
package org.virtue.decompile;

/**
 * @author Kyle Friz
 * @since Feb 21, 2015
 */
public enum DecompileMode {

	/**
	 * Represents decompilation should use the JODE Decompiler NOTE: JODE Fails
	 * to decompile one class, and also messes up another class (Choppy Items)
	 */
	JODE,

	/**
	 * Represents decompilation should use the CFR Decompiler NOTE: CFR is still
	 * in development, so it will fail to decompile many classes
	 */
	CFR,

	/**
	 * Represents decompilation should use the Fernflower Decompiler NOTE:
	 * Fernflower is an pretty good decompile, should use this to decompile the
	 * class that JODE messes up (Choppy Items)
	 */
	FERNFLOWER,

	/**
	 * Represents decompilation should use the Procyon Decompiler NOTE: Procyon
	 * is still in development, so it will fail to decompile some classes
	 */
	PROCYON;

	/**
	 * Grabs the mode for the specified value
	 * 
	 * @param val
	 *            The value
	 * @return The mode
	 */
	public static DecompileMode valueOf(int val) {
		switch (val) {
		case 0:
			return JODE;
		case 1:
			return CFR;
		case 2:
			return FERNFLOWER;
		case 3:
			return PROCYON;
		}
		return null;
	}

}
