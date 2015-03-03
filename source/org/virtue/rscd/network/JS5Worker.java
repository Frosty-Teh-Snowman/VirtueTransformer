package org.virtue.rscd.network;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.virtue.VirtueTransformer;

/**
 * Manages the requesting of files from the game server.
 */
public abstract class JS5Worker {

	public enum State {
		DISCONNECTED, ERROR, OUTDATED, CONNECTING, CONNECTED
	}

	protected Queue<FileRequest> requests;
	protected Map<Long, FileRequest> waiting;
	protected Socket socket;
	protected InputStream input;
	protected OutputStream output;
	protected State state;
	protected String host;
	protected int major;
	protected int minor;
	protected String key;
	protected FileRequest current;
	protected long lastUpdate;
	protected ByteBuffer outputBuffer;
	protected ByteBuffer inputBuffer;
	
	/**
	 * Connects to the specified host on port 43594 and initiates the update protocol handshake.
	 * @param host The world to connect to
	 * @param major The client's major version
	 * @param minor The client's minor version
	 */
	public abstract void connect(String host, int major, int minor, String key);
	
	/**
	 * Handles the bulk of the processing for the requester. This method uses the current state of the requester
	 * to choose the correct action.
	 *
	 * When connected, this method will send up to 20 requests to the server at one time, reading and processing
	 * them as they are sent back from the server. 
	 */
	public abstract void process();

	/**
	 * Sends the initial connection status and login packets to the server. By default, this downloader
	 * indicates that it is logged out.
	 */
	protected abstract void sendConnectionInfo();


	/**
	 * Submits a request to be sent to the server.
	 * @param index The cache index the file belongs to
	 * @param file The file number
	 * @return A FileRequest object representing the requested file.
	 */
	public FileRequest request(int index, int file) {
		FileRequest request = new FileRequest(index, file);
		requests.offer(request);
		return request;
	}

	/**
	 * Gets the current state of the requester.
	 * @return The requester's current state.
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * Resets the state of the requester. Files that have been sent and are waiting to be processed will
	 * be requested again once the connection is reestablished.
	 */
	protected void reset() {
		for (FileRequest request : waiting.values()) {
			requests.offer(request);
		}
		waiting.clear();

		try {
			socket.close();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		socket = null;
		input = null;
		output = null;
		current = null;
		lastUpdate = 0;
	}

	/**
	 * Helper method to put a three-byte value into a buffer.
	 * @param buffer The buffer
	 * @param value The value to be placed into the buffer
	 */
	protected void putMedInt(ByteBuffer buffer, int value) {
		buffer.put((byte) (value >> 16));
		buffer.put((byte) (value >> 8));
		buffer.put((byte) value);
	}

}
