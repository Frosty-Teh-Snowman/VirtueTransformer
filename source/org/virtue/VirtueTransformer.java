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

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.bytecode.graph.hierarchy.HierarchyTree;
import org.virtue.decompile.BytecodeDecompiler;
import org.virtue.decompile.DecompileMode;
import org.virtue.gamepack.ConfigCrawler;
import org.virtue.gamepack.JS5Worker;
import org.virtue.gamepack.cryption.GamepackDecryption;
import org.virtue.gamepack.cryption.GamepackEncryption;

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
	 * The current game mode
	 */
	private GameMode game_mode;

	/**
	 * The current decompile mode
	 */
	private DecompileMode decompile_mode;

	/**
	 * The injector
	 */
	private Injector injector;
	
	/**
	 * The bytecode decompiler
	 */
	private BytecodeDecompiler decompiler;

	/**
	 * The config crawler, which grabs parameters
	 */
	private ConfigCrawler crawler;

	/**
	 * The js5 worker, which identifies client major build
	 */
	private JS5Worker js5_worker;

	/**
	 * The Secret AES Key (Parameter: "0"); NOTE: Only used for decypting a
	 * gamepack
	 */
	private String secret;

	/**
	 * The Vector AES Key (Parameter: "-1") NOTE: Only used for decypting a
	 * gamepack
	 */
	private String vector;

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
		this.game_mode = GameMode.OLDSCHOOL;
		this.decompile_mode = DecompileMode.JODE;
		this.directory = "./de_obf/local/";
		this.crawler = new ConfigCrawler();
		this.injector = new Injector();
		this.decompiler = new BytecodeDecompiler();
		this.startTime = System.currentTimeMillis();
		this.running = true;
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Usage: java -jar VirtueTransformer.jar [-<option>=<value>]*");
			System.err.println("Example: java -jar VirtueTransformer.jar -t_mode=2 -d_mode=2 -secret=aqmjTcXEDoe9a8BekbE3iw -vector=VPuc*5PB7oliknJVXdVDPw");
			System.err.println();
			throw new IllegalArgumentException("Invalid Runtime Arguments!");
		}
		
		System.out.println("/*********************************************************************\\");
	    System.out.println("\\*       VirtueTransformer  Copyright (C) 2015  Kyle Friz            */");
	    System.out.println("/*        Runescape 3/07 Bytecode Transformer & Obfuscator           *\\");
	    System.out.println("\\*                                                                   */");
	    System.out.println("/*        This program comes with ABSOLUTELY NO WARRANTY.            *\\");
	    System.out.println("\\*  This is free software, and you are welcome to redistribute it    */");
	    System.out.println("/*                      under certain conditions.                    *\\");
	    System.out.println("\\*********************************************************************/");

		instance = new VirtueTransformer();

		
		/**
		 * -t_mode= sets the transformation mode (Which stage should the program start at) Default: GRAB
		 * -g_mode= sets the game mode (Oldschool or Runescape3) Default: OLDSCHOOL
		 * -d_mode= sets the decompile mode (Which decompiler should the program use) Default: JODE
		 * -secret= sets the secret key for decrypting the gamepack (Only used when not grabbing the current rs gamepack)
		 * -vector= sets the initialization vector for dercypting the gamepack (Only used when not grabbing the current rs gamepack)
		 */
		for (String option : args) {
			if (option.startsWith("-t_mode")) {
				instance.setTransformMode(TransformMode.valueOf(Integer.parseInt(option.substring(8))));
			} else if (option.startsWith("-g_mode")) {
				instance.setGameMode(GameMode.valueOf(Integer.parseInt(option.substring(8))));
			} else if (option.startsWith("-d_mode")) {
				instance.setDecompileMode(DecompileMode.valueOf(Integer.parseInt(option.substring(8))));
			} else if (option.startsWith("-secret")) {
				instance.setSecret(option.substring(8));
			} else if (option.startsWith("-vector")) {
				instance.setVector(option.substring(8));
			}
		}
		
		instance.process();
	}

	/**
	 * Processes the main thread
	 */
	private void process() {
		while (isRunning()) {
			switch (getTransformMode()) {
			case OBFUSCATE:
				setDirectory("./obf/");

				getInjector().initialization(getDirectory() + "original.jar");
				
				try {
					getInjector().transform();
					setGamepackEncryption(new GamepackEncryption());
					getGamepackEncryption().encrypt();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| IOException e) {
					logger.error("Error encrypting gamepack!", e);
				}

				/* TODO: Obfuscate a jar */
				setTransformMode(TransformMode.FINALIZE);
				break;
			case GRAB:
				try {
					getCrawler().crawl(getGameMode().equals(GameMode.OLDSCHOOL));

					setJS5Worker(new JS5Worker(getCrawler().getCodebase(), getCrawler().getConnectionKey()));
					getJS5Worker().connect(getGameMode().equals(GameMode.OLDSCHOOL) ? 73 : 836, 1);
					setDirectory("./de_obf/" + (getGameMode().equals(GameMode.OLDSCHOOL) ? "oldschool/" : "rs3/")
							+ getJS5Worker().identifyVersion() + "/");

					File file = new File(getDirectory());
					if (!file.exists())
						file.mkdirs();

					getCrawler().download();
				} catch (IOException e) {
					logger.error("Error crawling configs!", e);
				}

				if (getGameMode().equals(GameMode.OLDSCHOOL))
					setTransformMode(TransformMode.DEOBFUSCATE);
				else {
					setSecret(getCrawler().getSecretKey());
					setVector(getCrawler().getIVector());
					setTransformMode(TransformMode.DECRYPT);
				}
				/* TODO: Grab the gamepack */
				break;
			case DECRYPT:

				try {
					setGamepackDecryption(new GamepackDecryption(getSecret(), getVector()));
					getGamepackDecryption().decrypt();
				} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
					logger.error("Error decrypting gamepack!", e);
				}

				/* TODO: Decrypt the gamepack */
				setTransformMode(TransformMode.DEOBFUSCATE);
				break;
			case DEOBFUSCATE:
				
				try {
					if (getGameMode().equals(GameMode.OLDSCHOOL))
						getInjector().initialization(getDirectory() + "gamepack.jar");
					else
						getInjector().initialization(getDirectory() + "decrypted.jar");
					
					getInjector().registerTransformer();
					getInjector().registerIdentifiers();
					
					getInjector().transform();
					
					HierarchyTree hierarchyTree = new HierarchyTree(getInjector().getClasses());
			        hierarchyTree.build();
			      // System.out.println(hierarchyTree.toString());
					
					getInjector().initialization(getDirectory() + "deobfuscated.jar");
					getInjector().identify();
				} catch (IOException e) {
					logger.error("Error deobfuscating!", e);
				}
				
				/* TODO: Deobfuscate a jar */
				setTransformMode(TransformMode.DECOMPILE);
				break;
			case DECOMPILE:

				try {
					 getDecompiler().decompile(getDirectory() + "deobfuscated.jar", getDirectory() + "source/");
				} catch (Exception e) {
					logger.error("Error Decompiling!", e);
				}

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
	 * @return the game_mode
	 */
	public GameMode getGameMode() {
		return game_mode;
	}

	/**
	 * @param game_mode
	 *            the game_mode to set
	 */
	public void setGameMode(GameMode game_mode) {
		this.game_mode = game_mode;
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
	 * @return the injector
	 */
	public Injector getInjector() {
		return injector;
	}

	/**
	 * @param injector the injector to set
	 */
	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	/**
	 * @return the crawler
	 */
	public ConfigCrawler getCrawler() {
		return crawler;
	}

	/**
	 * @return the js5_worker
	 */
	public JS5Worker getJS5Worker() {
		return js5_worker;
	}

	/**
	 * @param js5_worker
	 *            the js5_worker to set
	 */
	public void setJS5Worker(JS5Worker js5_worker) {
		this.js5_worker = js5_worker;
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
	 * @param directory
	 *            the directory to set
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
	 * @return the decompiler
	 */
	public BytecodeDecompiler getDecompiler() {
		return decompiler;
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
	 * @param g_decrypt
	 *            the g_decrypt to set
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
	 * @param g_encrypt
	 *            the g_encrypt to set
	 */
	public void setGamepackEncryption(GamepackEncryption g_encrypt) {
		this.g_encrypt = g_encrypt;
	}
}
