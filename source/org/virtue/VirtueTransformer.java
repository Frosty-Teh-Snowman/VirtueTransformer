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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kyle Friz
 * @since Feb 20, 2015
 */
public class VirtueTransformer {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(VirtueTransformer.class);
	
	/**
	 * The instance of the transformer
	 */
	private static VirtueTransformer instance;
	
	/**
	 * The start time of execution
	 */
	private long startTime;
	
	/**
	 * If the current execution should still be running
	 */
	private boolean running;
	
	/**
	 * The current mode
	 */
	private Mode mode;
	
	
	public VirtueTransformer(Mode mode) {
		this.mode = mode;
	}
	
	public static void main(String[] args) throws Exception{
		if (args.length < 1)
			throw new IllegalArgumentException("Invalid Runtime Arguments! Usuage: int(Mode)");
		
		Mode mode = Mode.valueOf(Integer.parseInt(args[0]));
		
		instance = new VirtueTransformer(mode);
		instance.setStartTime(System.currentTimeMillis());
		instance.setRunning(true);
		instance.process();
	}
	
	/**
	 * Processes the main thread
	 */
	private void process() {
		while (isRunning()) {
			logger.info(mode.toString());
			switch (mode) {
			case OBFUSCATE:
				/* TODO: Obfuscate a jar */
				setMode(Mode.FINALIZE);
				break;
			case GRAB:
				/* TODO: Grab the gamepack */
				setMode(Mode.DECRYPT);
				break;
			case DECRYPT:
				/* TODO: Decrypt the gamepack */
				setMode(Mode.DEOBFUSCATE);
				break;
			case DEOBFUSCATE:
				/* TODO: Deobfuscate a jar */
				setMode(Mode.DECOMPILE);
				break;
			case DECOMPILE:
				/* TODO: Decompile a jar */
				setMode(Mode.FINALIZE);
				break;
			case FINALIZE:
				/* TODO: Finalize the execution */
				setRunning(false);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Grabs the instance of the transformer
	 * @return the instance
	 */
	public static VirtueTransformer getInstance() {
		return instance;
	}

	/**
	 * Grabs the current mode
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Sets the current mode
	 * @param mode the mode to set
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
