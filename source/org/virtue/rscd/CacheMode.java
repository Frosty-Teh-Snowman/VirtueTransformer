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
package org.virtue.rscd;


/**
 * @author Kyle Friz
 * @since Mar 1, 2015
 */
public enum CacheMode {
	
	/**
	 * Represents downloading the cache will be skipped
	 */
	SKIP,
	
	/**
	 * Represents the last downloaded cache will be copied and updated
	 */
	UPDATE_PREVIOUS,
	
	/**
	 * Represents a the cache will be downloaded from scratch
	 */
	FULL_UPDATE;
	
	/**
	 * Grabs the mode for the specified value
	 * 
	 * @param val
	 *            The value
	 * @return The mode
	 */
	public static CacheMode valueOf(int val) {
		switch (val) {
		case 0:
			return SKIP;
		case 1:
			return UPDATE_PREVIOUS;
		case 2:
			return FULL_UPDATE;
		}
		return null;
	}
	
}
