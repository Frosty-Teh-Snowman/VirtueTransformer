package org.virtue.rscd.network.workers;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.virtue.VirtueTransformer;
import org.virtue.rscd.network.FileRequest;
import org.virtue.rscd.network.JS5Worker;

/**
 * Manages the requesting of files from the game server.
 */
public class RS3Worker extends JS5Worker {

	/**
	 * Creates a new Js5Worker instance.
	 */
	public RS3Worker() {
		requests = new LinkedList<FileRequest>();
		waiting = new HashMap<Long, FileRequest>();
		state = State.DISCONNECTED;
		outputBuffer = ByteBuffer.allocate(6);
		inputBuffer = ByteBuffer.allocate(5);
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

			ByteBuffer buffer = ByteBuffer.allocate(11 + 32);
			buffer.put((byte) 15); // handshake type
			buffer.put((byte) (9 + 32)); // size
			buffer.putInt(major); // client's major version
			buffer.putInt(minor); // client's minor version?
			buffer.put(key.getBytes()); // handshake key?
			buffer.put((byte) 0); // nul-byte (c string)
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
						state = State.CONNECTED;
						System.out.println("Correct version: " + major);

						ByteBuffer sizes = ByteBuffer.allocate(25 * 4);
						int read = 0, off = 0, len = sizes.capacity();
						while (len > 0) {
							read = input.read(sizes.array(), off, len);
							if (read <= 0) {
								throw new EOFException();
							}
							off += read;
							len -= read;
						}

						int end = sizes.capacity() / 4;

						List<Integer> elements = new ArrayList<>();
						for (int i = 0; i < end; i++) {
							elements.add(sizes.getInt());
						}

						try (BufferedWriter writer = new BufferedWriter(new FileWriter(VirtueTransformer.getInstance()
								.getDirectory() + "elements.txt"))) {
							StringBuilder builder = new StringBuilder();
							builder.append("Required loading element sizes: ");
							writer.write("Required loading element sizes: ");
							Iterator<Integer> it = elements.iterator();
							while (it.hasNext()) {
								int key = it.next();
								writer.write(key + (it.hasNext() ? ", " : ""));
								builder.append(key + (it.hasNext() ? ", " : ""));
							}
							writer.close();
							System.out.println(builder.toString());
						}

						System.out.println();

						sendConnectionInfo();
						lastUpdate = System.currentTimeMillis();
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
					outputBuffer.put((byte) request.getIndex());
					outputBuffer.putInt(request.getFile());
					output.write(outputBuffer.array());
					output.flush();
					outputBuffer.clear();

					System.out.println("Requested " + request.getIndex() + "," + request.getFile());
					waiting.put(request.hash3(), request);
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
					if (current == null) {
						if (available >= 5) {
							inputBuffer.clear();
							input.read(inputBuffer.array());
							int index = inputBuffer.get() & 0xff;
							int file = inputBuffer.getInt() & 0x7fffffff;
							long hash = ((long) index << 32) | file;

							current = waiting.get(hash);
							if (current == null) {
								throw new IOException();
							}

							current.setPosition(5);
							inputBuffer.clear();
						}
					} else if (current.getBuffer() == null) {
						if (available >= 5) {
							input.read(inputBuffer.array());
							int compression = inputBuffer.get() & 0xff;
							int fileSize = inputBuffer.getInt();

							int size = fileSize + (compression == 0 ? 5 : 9) + (current.getIndex() != 255 ? 2 : 0);
							current.setSize(size);
							ByteBuffer buffer = current.getBuffer();
							buffer.put((byte) compression);
							buffer.putInt(fileSize);

							current.setPosition(10);
							inputBuffer.clear();
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
							waiting.remove(current.hash3());
							buffer.flip();
							current = null;
						} else if (current.getPosition() == 512) {
							current.setPosition(0);
							current = null;
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
			outputBuffer.put((byte) 6);
			putMedInt(outputBuffer, 3);
			outputBuffer.putShort((short) 0);
			output.write(outputBuffer.array());
			output.flush();
			outputBuffer.clear();

			outputBuffer.put((byte) 3);
			putMedInt(outputBuffer, 0);
			outputBuffer.putShort((short) 0);
			output.write(outputBuffer.array());
			output.flush();
			outputBuffer.clear();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}
}
