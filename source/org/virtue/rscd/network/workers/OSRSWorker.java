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
package org.virtue.rscd.network.workers;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;

import org.virtue.rscd.network.FileRequest;
import org.virtue.rscd.network.JS5Worker;

/**
 * @author Kyle Friz
 * @since Mar 2, 2015
 */
public class OSRSWorker extends JS5Worker {

	/**
	 * Creates a new CacheRequester instance.
	 */
	public OSRSWorker() {
		requests = new LinkedList<FileRequest>();
		waiting = new HashMap<Long, FileRequest>();
		state = State.DISCONNECTED;
		outputBuffer = ByteBuffer.allocate(4);
		inputBuffer = ByteBuffer.allocate(8);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virtue.rscd.network.JS5Worker#connect(java.lang.String, int,
	 * int, java.lang.String)
	 */
	@Override
	public void connect(String host, int major, int minor, String key) {
		this.host = host;
		this.major = major;
		this.minor = minor;
		this.key = key;

		try {
			socket = new Socket(host, 43594);
			input = socket.getInputStream();
			output = socket.getOutputStream();

			ByteBuffer buffer = ByteBuffer.allocate(5);
			buffer.put((byte) 15); // handshake type
			buffer.putInt(major); // client's major version
			output.write(buffer.array());
			output.flush();

			state = State.CONNECTING;
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virtue.rscd.network.JS5Worker#process()
	 */
	@Override
	public void process() {
		if (state == State.CONNECTING) {
			try {
				if (input.available() > 0) {
					int response = input.read();
					if (response == 0) {
						System.out.println("Correct version: " + major);

						System.out.println();

						sendConnectionInfo();
						lastUpdate = System.currentTimeMillis();
						state = State.CONNECTED;
					} else if (response == 6) {
						state = State.OUTDATED;
						System.out.println("Invalid version " + major + " " + minor + ", trying again");
					} else {
						state = State.ERROR;
					}
				}
			} catch (IOException ioex) {
				throw new RuntimeException(ioex);
			}
		} else if (state == State.OUTDATED) {
			reset();
			connect(host, ++major, minor, key);
		} else if (state == State.ERROR) {
			throw new RuntimeException("Unexpected server response");
		} else if (state == State.DISCONNECTED) {
			reset();
			connect(host, major, minor, key);
		} else {
			if (lastUpdate != 0 && System.currentTimeMillis() - lastUpdate > 30000) {
				System.out.println("Server timeout, dropping connection");
				state = State.DISCONNECTED;
				return;
			}
			try {
				while (!requests.isEmpty() && waiting.size() < 20) {
					FileRequest request = requests.poll();
					outputBuffer.put(request.getIndex() == 255 ? (byte) 1 : (byte) 0);
					putMedInt(outputBuffer, (int) request.hashOS());
					output.write(outputBuffer.array());
					output.flush();
					outputBuffer.clear();
					System.out.println("Requested " + request.getIndex() + "," + request.getFile());
					waiting.put(request.hashOS(), request);
				}
				for (int i = 0; i < 100; i++) {
					int available = input.available();
					if (available < 0) {
						throw new IOException();
					}
					if (available == 0) {
						break;
					}
					lastUpdate = System.currentTimeMillis();
					int needed = 0;
					if (current == null) {
						needed = 8;
					} else if (current.getPosition() == 0) {
						needed = 1;
					}
					if (needed > 0) {
						if (available >= needed) {
							if (current == null) {
								inputBuffer.clear();
								input.read(inputBuffer.array());
								int index = inputBuffer.get() & 0xff;
								int file = inputBuffer.getShort();
								int compression = (inputBuffer.get() & 0xff) & 0x7f;
								int fileSize = inputBuffer.getInt();
								long hash = ((long) index << 16) | file;
								current = waiting.get(hash);
								if (current == null) {
									throw new IOException();
								}

								int size = fileSize + (compression == 0 ? 5 : 9) + (index != 255 ? 2 : 0);
								current.setSize(size);
								ByteBuffer buffer = current.getBuffer();
								buffer.put((byte) compression);
								buffer.putInt(fileSize);
								current.setPosition(8);
								inputBuffer.clear();
							} else if (current.getPosition() == 0) {
								if (input.read() != 0xff) {
									current = null;
								} else {
									current.setPosition(1);
								}
							} else {
								throw new IOException();
							}
						}
					} else {
						ByteBuffer buffer = current.getBuffer();
						int totalSize = buffer.capacity() - (current.getIndex() != 255 ? 2 : 0);
						int blockSize = 512 - current.getPosition();
						int remaining = totalSize - buffer.position();
						if (remaining < blockSize) {
							blockSize = remaining;
						}
						if (available < blockSize) {
							blockSize = available;
						}
						int read = input.read(buffer.array(), buffer.position(), blockSize);
						buffer.position(buffer.position() + read);
						current.setPosition(current.getPosition() + read);
						if (buffer.position() == totalSize) {
							current.setComplete(true);
							waiting.remove(current.hashOS());
							buffer.flip();
							current = null;
						} else {
							if (current.getPosition() == 512) {
								current.setPosition(0);
							}
						}
					}
				}
			} catch (IOException ioex) {
				state = State.DISCONNECTED;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.virtue.rscd.network.JS5Worker#sendConnectionInfo()
	 */
	@Override
	protected void sendConnectionInfo() {
		try {
			outputBuffer.put((byte) 3);
			putMedInt(outputBuffer, 0);
			output.write(outputBuffer.array());
			output.flush();
			outputBuffer.clear();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}
}
