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
package org.virtue.gamepack.cryption;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.jar.JarInputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.Constants;

/**
 * @author Kyle Friz
 * @author Major
 * @since Feb 21, 2015
 */
public class GamepackEncryption {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(GamepackEncryption.class);

	/**
	 * The input stream to the {@code inner.pack.gz} file.
	 */
	private final InputStream input;

	/**
	 * Creates the inner pack decrypter.
	 * 
	 * @throws IOException
	 *             If the path to the gamepack is invalid.
	 */
	public GamepackEncryption() throws IOException {
		this.input = new BufferedInputStream(new FileInputStream("./build/transformer/obf/obfuscated.jar/"));
	}

	/**
	 * Packs a jar using Pack200, then GZIPs it, then uses and AES Cipher to
	 * encrypt the jar
	 * 
	 * @return The map of class names to the byte buffers containing their data.
	 * @throws NoSuchAlgorithmException
	 *             If the current system does not have an AES implementation.
	 * @throws NoSuchPaddingException
	 *             If the current system does not support the specified padding
	 *             scheme.
	 * @throws InvalidKeyException
	 *             If the secret key is invalid.
	 * @throws InvalidAlgorithmParameterException
	 *             If the initialization vector is invalid.
	 * @throws IOException
	 *             If there is an error reading from or writing to any of the
	 *             various streams used.
	 * @throws IllegalBlockSizeException
	 *             If AES is unable to process the input data provided.
	 * @throws BadPaddingException
	 *             If the data lacks the appropriate padding bytes.
	 */
	public void encrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {

		/* Generate a AES Secret key */
		KeyGenerator factory = KeyGenerator.getInstance("AES");
		SecretKey key = factory.generateKey();

		/* randomly generate IV */
		SecureRandom random = new SecureRandom();
		byte iv[] = new byte[16];
		random.nextBytes(iv);

		System.out.println(encodeBase64(key.getEncoded()));
		System.out.println(encodeBase64(iv));

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKey secret = new SecretKeySpec(key.getEncoded(), "AES");
		IvParameterSpec vector = new IvParameterSpec(iv);

		/* Initialize the cipher. */
		cipher.init(Cipher.ENCRYPT_MODE, secret, vector);

		byte[] buffer = new byte[Constants.BUFFER_SIZE];
		int read = 0, in = 0;

		while (read < buffer.length && (in = input.read(buffer, read, buffer.length - read)) != -1) {
			read += in;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream(Constants.BUFFER_SIZE);

		/* Packs and GZIPs the jar file, and writes the compressed data out. */
		try (JarInputStream jis = new JarInputStream(new ByteArrayInputStream(buffer));
				GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
			Pack200.newPacker().pack(jis, gzip);
		}

		/* Encrypts the compressed file. */
		byte[] encrypted = cipher.doFinal(bos.toByteArray());

		File file = new File("./build/transformer/obf/", Constants.ENCRYPTED_ARCHIVE_NAME);
		if (!file.exists())
			file.createNewFile();

		/* Writes encrypted file file out */
		try (BufferedOutputStream jos = new BufferedOutputStream(new FileOutputStream(file))) {
			jos.write(encrypted);
		}
		input.close();
		logger.info("Encrypted inner.pack.gz!");
	}

	/**
	 * Encodes a byte array into a base64 string
	 * 
	 * @param bytes
	 *            The key, as a byte array.
	 * @return The base64 key
	 */
	private String encodeBase64(byte[] bytes) {
		Base64.Encoder base64 = Base64.getEncoder();
		String string = new String(base64.encode(bytes));

		/*
		 * JaGex's implementation uses * and - instead of + and /, so replace
		 * them.
		 */
		return string.replaceAll("\\+", "\\*").replaceAll("/", "-");
	}
}
