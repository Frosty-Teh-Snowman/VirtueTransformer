/**
 * Copyright (c) 2014 Virtue Studios
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
package org.virtue;

/**
 * 
 * @author Kyle Friz
 * @since Feb 20, 2015
 */
public enum Mode {

	
	/**
	 * Represents the mode of obfuscating bytecode
	 */
	OBFUSCATE,
	
	/**
	 * Represents the mode of grabbing the gamepack from the web
	 */
	GRAB,
	
	/**
	 * Represents the mode of decrypting AES Pack200 archives
	 */
	DECRYPT,
	
	
	/**
	 * Represents the mode of deobfuscating bytecode
	 */
	DEOBFUSCATE,
	
	/**
	 * Represents the mode of decompiling bytecode
	 */
	DECOMPILE, 
	
	/**
	 * Represents the mode of finalizing
	 */
	FINALIZE;
	
	/**
	 * Grabs the omde for the specified value
	 * @param val The value
	 * @return
	 */
	public static Mode valueOf(int val) {
		switch (val) {
		case 0:
			return OBFUSCATE;
		case 1:
			return GRAB;
		case 2:
			return DECRYPT;
		case 3:
			return DEOBFUSCATE;
		case 4:
			return DECOMPILE;
		case 5:
			return FINALIZE;
		}
		return null;
	}

}
