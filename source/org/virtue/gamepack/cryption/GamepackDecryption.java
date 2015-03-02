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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.Constants;
import org.virtue.VirtueTransformer;

/**
 * @author Major
 * @author Kyle Friz
 * @since Feb 21, 2015
 */
public class GamepackDecryption {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(GamepackDecryption.class);

	/**
	 * The key returned if an empty (i.e. {@code length == 0} string is
	 * decrypted.
	 */
	private static final byte[] EMPTY_KEY = new byte[0];

	/**
	 * The encoded secret key for the AES block cipher.
	 */
	private final String encodedSecret;

	/**
	 * The encoded initialization vector for the AES block cipher.
	 */
	private final String encodedVector;

	/**
	 * The input stream to the {@code inner.pack.gz} file.
	 */
	private final InputStream input;

	/**
	 * The gamepack jar file.
	 */
	private final JarFile jar;

	/**
	 * Creates the inner pack decrypter.
	 * 
	 * @param secret
	 *            The encoded secret key.
	 * @param vector
	 *            The encoded initialisation vector.
	 * @throws IOException
	 *             If the path to the gamepack is invalid.
	 */
	public GamepackDecryption(String secret, String vector) throws IOException {
		this.encodedSecret = secret;
		this.encodedVector = vector;
		this.jar = new JarFile(VirtueTransformer.getInstance().getDirectory() + "gamepack.jar");

		ZipEntry archive = jar.getEntry(Constants.ENCRYPTED_ARCHIVE_NAME);
		this.input = new BufferedInputStream(jar.getInputStream(archive));
	}

	/**
	 * Decrypts the {@code inner.pack.gz} archive using the AES cipher. The
	 * decrypted data is then un-gzipped and unpacked from the pack200 format,
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
	public void decrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {
		byte[] secretKey = encodedSecret.length() == 0 ? EMPTY_KEY : decodeBase64(encodedSecret);
		byte[] vectorKey = encodedVector.length() == 0 ? EMPTY_KEY : decodeBase64(encodedVector);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secret = new SecretKeySpec(secretKey, "AES");
		IvParameterSpec vector = new IvParameterSpec(vectorKey);

		/* Initialize the cipher. */
		cipher.init(Cipher.DECRYPT_MODE, secret, vector);

		byte[] buffer = new byte[Constants.BUFFER_SIZE];
		int read = 0, in = 0;

		while (read < buffer.length && (in = input.read(buffer, read, buffer.length - read)) != -1) {
			read += in;
		}

		/* Decrypts the inner.pack.gz file. */
		byte[] decrypted = cipher.doFinal(buffer, 0, read);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(Constants.BUFFER_SIZE);

		/*
		 * Un-gzips and unpacks the jar file contained in the archive, and
		 * writes the decompressed data out.
		 */
		try (JarOutputStream jos = new JarOutputStream(bos);
				GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(decrypted))) {
			Pack200.newUnpacker().unpack(gzip, jos);
		}

		File file = new File(VirtueTransformer.getInstance().getDirectory() + "decrypted.jar");
		if (!file.exists())
			file.createNewFile();

		/* Writes decrypted jar file out */
		try (BufferedOutputStream jos = new BufferedOutputStream(new FileOutputStream(file))) {
			jos.write(bos.toByteArray());
		}
		jar.close();
		input.close();
		logger.info("Decrypted inner.pack.gz!");
	}

	/**
	 * Decodes the base64 string into a valid secret key or initialization
	 * vector.
	 * 
	 * @param string
	 *            The key.
	 * @return The key, as a byte array.
	 */
	private byte[] decodeBase64(String string) {
		/*
		 * JaGex's implementation uses * and - instead of + and /, so replace
		 * them.
		 */
		String valid = string.replaceAll("\\*", "\\+").replaceAll("-", "/");

		Base64.Decoder base64 = Base64.getDecoder();
		return base64.decode(valid);
	}
}
