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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.cryption.GamepackDecryption;
import org.virtue.cryption.GamepackEncryption;
import org.virtue.decompile.DecompileMode;
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
	 * The working directory
	 */
	private String directory;
	
	/**
	 * The current transform mode
	 */
	private TransformMode transform_mode;

	/**
	 * The current decompile mode
	 */
	private DecompileMode decompile_mode;

	/**
	 * The Secret AES Key (Parameter: "0");
	 */
	private String secret;

	/**
	 * The Vector AES Key (Parameter: "-1")
	 */
	private String vector;

	/**
	 * The list of transformers
	 */
	private List<Transformer> transformers;

	/**
	 * The module for holding classes
	 */
	private ClassModule module;
	
	/**
	 * The Gamepack decrytor
	 */
	private GamepackDecryption g_decrypt;
	
	/**
	 * The Gamepack encryptor
	 */
	private GamepackEncryption g_encrypt;

	public VirtueTransformer() {
		this.transform_mode = TransformMode.GRAB;
		this.decompile_mode = DecompileMode.JODE;
		this.module = new ClassModule();
		this.startTime = System.currentTimeMillis();
		this.running = true;
		this.transformers = Collections.synchronizedList(new ArrayList<Transformer>());
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Usage: java -jar VirtueTransformer.jar -t_mode=<value> [-<option>=<value>]*");
			System.err.println("Example: java -jar VirtueTransformer.jar -t_mode=2 -d_mode=2 -secret=aqmjTcXEDoe9a8BekbE3iw -vector=VPuc*5PB7oliknJVXdVDPw");
			System.err.println();
			throw new IllegalArgumentException("Invalid Runtime Arguments!");
		}
		
		instance = new VirtueTransformer();

		for (String option : args) {
			if (option.startsWith("-t_mode")) {
				instance.setTransformMode(TransformMode.valueOf(Integer.parseInt(option.substring(8))));
			} else if (option.startsWith("-d_mode")) {
				instance.setDecompileMode(DecompileMode.valueOf(Integer.parseInt(option.substring(8))));
			} else if (option.startsWith("-secret")) {
				instance.setSecret(option.substring(8));
			} else if (option.startsWith("-vector")) {
				instance.setVector(option.substring(8));
			}
		}
		
		instance.getTransformers().add(new ClassNameTransformer(true));
		instance.getTransformers().add(new ClassNameTransformer(false));

		instance.process();
	}

	/**
	 * Processes the main thread
	 */
	private void process() {
		while (isRunning()) {
			System.out.println(getTransformMode().toString());
			switch (getTransformMode()) {
			case OBFUSCATE:

				getModule().initialization("./obf/original.jar");

				synchronized (transformers) {
					Iterator<Transformer> trans = transformers.iterator();
					while (trans.hasNext()) {
						Transformer transformer = trans.next();
						if (transformer.getMode().equals(TransformMode.OBFUSCATE)) {
							transformer.initialization();
							transformer.transformation();
							transformer.finalization();
						}
					}
				}
				
				try {
					setGamepackEncryption(new GamepackEncryption());
					getGamepackEncryption().encrypt();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | IOException e) {
					logger.error("Error encrypting gamepack!", e);
				}
				
				/* TODO: Obfuscate a jar */
				setTransformMode(TransformMode.FINALIZE);
				break;
			case GRAB:
				/* TODO: Grab the gamepack */
				setTransformMode(TransformMode.DECRYPT);
				break;
			case DECRYPT:

				try {
					setGamepackDecryption(new GamepackDecryption(getSecret(), getVector()));
					getGamepackDecryption().decrypt();
				} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
					logger.error("Error decrypting gamepack!", e);
				}
				
				/* TODO: Decrypt the gamepack */
				setTransformMode(TransformMode.DEOBFUSCATE);
				break;
			case DEOBFUSCATE:

				getModule().initialization("./de_obf/decrypted.jar");

				synchronized (transformers) {
					Iterator<Transformer> trans = transformers.iterator();
					while (trans.hasNext()) {
						Transformer transformer = trans.next();
						if (transformer.getMode().equals(TransformMode.DEOBFUSCATE)) {
							transformer.initialization();
							transformer.transformation();
							transformer.finalization();
						}
					}
				}
				
				/* TODO: Deobfuscate a jar */
				setTransformMode(TransformMode.DECOMPILE);
				break;
			case DECOMPILE:

			//	getModule().initialization("./de_obf/deobfuscated.jar");
				
				/* TODO: Decompile a jar */
				setTransformMode(TransformMode.FINALIZE);
				break;
			case FINALIZE:
				/* TODO: Finalize the execution */
				setRunning(false);
				logger.info("Finished Running VirtueTransformer!");
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
	 * Grabs the current transform mode
	 * 
	 * @return the mode
	 */
	public TransformMode getTransformMode() {
		return transform_mode;
	}

	/**
	 * Sets the current mode
	 * 
	 * @param mode
	 *            the mode to set
	 */
	public void setTransformMode(TransformMode mode) {
		this.transform_mode = mode;
	}

	/**
	 * Grabs the current decompile mode
	 * 
	 * @return the mode
	 */
	public DecompileMode getDecompileMode() {
		return decompile_mode;
	}
	
	/**
	 * Sets the current mode
	 * 
	 * @param mode
	 *            the mode to set
	 */
	public void setDecompileMode(DecompileMode mode) {
		this.decompile_mode = mode;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
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
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param secret
	 *            the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	/**
	 * @return the vector
	 */
	public String getVector() {
		return vector;
	}

	/**
	 * @param vector
	 *            the vector to set
	 */
	public void setVector(String vector) {
		this.vector = vector;
	}

	/**
	 * @return the g_decrypt
	 */
	public GamepackDecryption getGamepackDecryption() {
		return g_decrypt;
	}

	/**
	 * @param g_decrypt the g_decrypt to set
	 */
	public void setGamepackDecryption(GamepackDecryption g_decrypt) {
		this.g_decrypt = g_decrypt;
	}

	/**
	 * @return the g_encrypt
	 */
	public GamepackEncryption getGamepackEncryption() {
		return g_encrypt;
	}

	/**
	 * @param g_encrypt the g_encrypt to set
	 */
	public void setGamepackEncryption(GamepackEncryption g_encrypt) {
		this.g_encrypt = g_encrypt;
	}
}
