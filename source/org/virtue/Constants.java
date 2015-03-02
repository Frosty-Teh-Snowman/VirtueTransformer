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
package org.virtue;


/**
 * @author Kyle Friz
 * @since Mar 1, 2015
 */
public class Constants {
	
	public static final int OSRS_MAJOR_VERSION = 74;
	public static final int RS3_MAJOR_VERSION = 837;
	public static final int MINOR_VERSION = 1;
	public static final int WORLD = 2;
    public static final int LOBBY = 2;
    
	/**
	 * The size of the buffer used when decrypting the {@code inner.pack.gz}
	 * archive, in bytes.
	 */
	public static final int BUFFER_SIZE = 0x500000;

	/**
	 * The name of the archive containing the client.
	 */
	public static final String ENCRYPTED_ARCHIVE_NAME = "inner.pack.gz";
	
}
