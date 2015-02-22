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
package org.virtue.gamepack;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtue.GameMode;
import org.virtue.VirtueTransformer;

/*
 * This is an adapted version of the Js5Worker class from Method's rscd, used with permission, 
 * and as such is not released under the same license as the rest of this project. If you wish
 * to adapt or use it, you must get Method's permission beforehand. All edits made to this class
 * by myself (Major) are released into the public domain. 
 */

/**
 * A simple version of Method's Js5Worker that identifies the current client version and exits. See the RS Cache
 * downloader thread <a href="http://www.rune-server.org/showpost.php?p=2170098">here</a> for the original version.
 * 
 * @author Method
 * @author Major
 * @author Kyle Friz
 */
public final class JS5Worker implements Closeable {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(ConfigCrawler.class);
	
	/**
	 * The type of response received from the server.
	 */
	private enum HandshakeResponse {

		/**
		 * The outdated handshake response, when the major or minor version is incorrect.
		 */
		OUTDATED,

		/**
		 * The unknown handshake response.
		 */
		UNKNOWN,

		/**
		 * The valid handshake response, when the major and minor versions are correct.
		 */
		VALID;

		/**
		 * Returns the handshake response associated with the specified value.
		 * 
		 * @param value The value.
		 * @return The handshake response, or {@link #UNKNOWN} if the value is not associated with a response.
		 */
		public static HandshakeResponse valueOf(int value) {
			switch (value) {
			case 0:
				return VALID;
			case 6:
				return OUTDATED;
			}
			return UNKNOWN;
		}

	}

	/**
	 * The state of a worker.
	 */
	private enum State {

		/**
		 * The connecting state, where the worker has sent the initial handshake but not yet received a response.
		 */
		CONNECTING,

		/**
		 * The disconnected state, when the socket is closed.
		 */
		DISCONNECTED,

		/**
		 * The outdated state, when the provided major version is incorrect.
		 */
		OUTDATED;

	}

	/**
	 * The host to connect to.
	 */
	private final String host;

	/**
	 * The input stream used by this worker.
	 */
	private InputStream input;

	/**
	 * The key used when connecting.
	 */
	private final String key;

	/**
	 * The major version.
	 */
	private int major;

	/**
	 * The minor version.
	 */
	private int minor;

	/**
	 * The output stream used by this worker.
	 */
	private OutputStream output;

	/**
	 * The socket used by this worker.
	 */
	private Socket socket;

	/**
	 * The state of this worker.
	 */
	private State state = State.DISCONNECTED;

	/**
	 * Creates the js5 worker.
	 * 
	 * @param host The host to connect to.
	 * @param key The connection key.
	 * @throws IOException If there is an error opening the socket.
	 */
	public JS5Worker(String host, String key) throws IOException {
		this.host = host.replace("http://", "").replace("/k=3/", "");
		this.key = key;
		
		socket = new Socket(this.host, 43594);
		input = socket.getInputStream();
		output = socket.getOutputStream();
	}

	@Override
	public void close() throws IOException {
		socket.close();
		state = State.DISCONNECTED;
	}

	/**
	 * Connects to the specified host on port 43594 and initiates the update protocol handshake.
	 * 
	 * @param major The client's major version.
	 * @param minor The client's minor version.
	 * @throws IOException If there is an error initiating the handshake.
	 */
	public void connect(int major, int minor) throws IOException {
		this.major = major;
		this.minor = minor;
		init();
	}

	/**
	 * Identifies the current runescape client version.
	 * 
	 * @return The version.
	 * @throws IOException If there is an error reading from the input stream.
	 */
	public int identifyVersion() throws IOException {
		return identifyVersion(100);
	}

	/**
	 * Identifies the current runescape client version.
	 * 
	 * @param attempts The amount of attempts to make before stopping.
	 * @return The version.
	 * @throws IOException If there is an error reading from the input stream.
	 * @throws IllegalStateException If the correct client version could not be found.
	 */
	public int identifyVersion(int attempts) throws IOException {
		for (int i = 0; i < attempts; i++) {
			switch (state) {
			case CONNECTING:
				if (input.available() <= 0) {
					break;
				}

				int response = input.read();
				switch (HandshakeResponse.valueOf(response)) {
				case VALID:
					logger.info("Successfully identified client version " + major + ".");
					return major;
				case OUTDATED:
					state = State.OUTDATED;
					logger.info("Invalid client version " + major + ", trying again.");
					break; // Will fall through to the OUTDATED case below.
				default:
					throw new IOException("Unexpected server response: " + response + ", please report.");
				}

			case OUTDATED:
				reset();
				connect(++major, minor);
				break;
			default:
				throw new IllegalStateException("Unexpected state " + state + ", please report.");
			}

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				logger.error("Error sleeping thread!", e);
			}
		}

		throw new IllegalStateException("Could not identify the correct client version after " + attempts + " attempts, please report.");
	}

	/**
	 * Initializes the connection and writes the first stage of the handshake to the output stream.
	 * 
	 * @throws IOException If there is an error writing to the output stream.
	 */
	private void init() throws IOException {
		ByteBuffer buffer;
		if (VirtueTransformer.getInstance().getGameMode().equals(GameMode.OLDSCHOOL)) {
			buffer = ByteBuffer.allocate(5);
			buffer.put((byte) 15); // handshake type
			buffer.putInt(major);
		} else {
			buffer = ByteBuffer.allocate(43);
			buffer.put((byte) 15); // handshake type
			buffer.put((byte) 41); // size
			buffer.putInt(major);
			buffer.putInt(minor);
			buffer.put(key.getBytes());
			buffer.put((byte) 0); // string terminator
		}
		output.write(buffer.array());
		output.flush();

		state = State.CONNECTING;
	}

	/**
	 * Resets this worker, closing and re-opening the socket.
	 * 
	 * @throws IOException If there is an error closing or re-opening the socket.
	 */
	private void reset() throws IOException {
		socket.close();
		// No changing the state here as we re-open the socket immediately.

		socket = new Socket(host, 43594);
		input = socket.getInputStream();
		output = socket.getOutputStream();
	}

}