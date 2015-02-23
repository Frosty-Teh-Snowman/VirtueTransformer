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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.VirtueTransformer;

/**
 * @author Kyle Friz
 * @since Feb 22, 2015
 */
public class BytecodeDecompiler {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(BytecodeDecompiler.class);

	public void decompile(String input, String output) throws Exception {
		switch (VirtueTransformer.getInstance().getDecompileMode()) {
		case CFR:
			org.benf.cfr.reader.Main.main(new String[] { input, "--outputdir", output });
			break;
		case FERNFLOWER:
			de.fernflower.main.decompiler.ConsoleDecompiler.main(new String[] { input, output });
			break;
		case JODE:
			jode.decompiler.Main.main(new String[] { "--style", "gnu", "--dest", output, input });
			break;
		case PROCYON:
			com.strobel.decompiler.DecompilerDriver.main(new String[] { "-jar", input, "-o", output });
			break;
		default:
			break;

		}
		logger.info("Decompiled using: " + VirtueTransformer.getInstance().getDecompileMode().toString());
	}
}
