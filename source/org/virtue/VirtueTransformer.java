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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.transformers.Transformer;
import org.virtue.transformers.impl.ClassNameTransformer;

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

	/**
	 * The list of transformers
	 */
	private List<Transformer> transformers = Collections.synchronizedList(new ArrayList<Transformer>());
	
	/**
	 * The module for holding classes
	 */
	private ClassModule module;

	public VirtueTransformer(Mode mode) {
		this.mode = mode;
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1)
			throw new IllegalArgumentException("Invalid Runtime Arguments! Usuage: int(Mode)");

		Mode mode = Mode.valueOf(Integer.parseInt(args[0]));

		instance = new VirtueTransformer(mode);
		instance.setStartTime(System.currentTimeMillis());
		
		instance.setModule(new ClassModule());
		
		instance.getTransformers().add(new ClassNameTransformer(true));
		instance.getTransformers().add(new ClassNameTransformer(false));
		
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
				
				getModule().initialization("./obf/original.jar");
				
				synchronized (transformers) {
					Iterator<Transformer> trans = transformers.iterator();
					while (trans.hasNext()) {
						Transformer transformer = trans.next();
						if (transformer.getMode().equals(Mode.OBFUSCATE)) {
							transformer.initialization();
							transformer.transform();
							transformer.finalization();
							// trans.remove();
						}
					}
				}
				setMode(Mode.FINALIZE);
				break;
			case GRAB:
				/* TODO: Grab the gamepack */
				setMode(Mode.DECRYPT);
				break;
			case DECRYPT:
				
				getModule().initialization("./deob/gamepack.jar");
				
				/* TODO: Decrypt the gamepack */
				setMode(Mode.DEOBFUSCATE);
				break;
			case DEOBFUSCATE:
				
				getModule().initialization("./deob/decrypted.jar");
				
				synchronized (transformers) {
					Iterator<Transformer> trans = transformers.iterator();
					while (trans.hasNext()) {
						Transformer transformer = trans.next();
						if (transformer.getMode().equals(Mode.DEOBFUSCATE)) {
							transformer.initialization();
							transformer.transform();
							transformer.finalization();
							// trans.remove();
						}
					}
				}
				/* TODO: Deobfuscate a jar */
				setMode(Mode.DECOMPILE);
				break;
			case DECOMPILE:
				
				getModule().initialization("./obf/deobfuscated.jar");
				
				synchronized (transformers) {
					Iterator<Transformer> trans = transformers.iterator();
					while (trans.hasNext()) {
						Transformer transformer = trans.next();
						if (transformer.getMode().equals(Mode.DECOMPILE)) {
							transformer.initialization();
							transformer.transform();
							transformer.finalization();
							// trans.remove();
						}
					}
				}
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
	 * 
	 * @return the instance
	 */
	public static VirtueTransformer getInstance() {
		return instance;
	}

	/**
	 * Grabs the current mode
	 * 
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Sets the current mode
	 * 
	 * @param mode
	 *            the mode to set
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
	 * @param startTime
	 *            the startTime to set
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
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @return the transformers
	 */
	public List<Transformer> getTransformers() {
		return transformers;
	}

	/**
	 * @return the module
	 */
	public ClassModule getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	public void setModule(ClassModule module) {
		this.module = module;
	}
}
