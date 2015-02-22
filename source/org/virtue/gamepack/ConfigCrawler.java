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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kyle Friz
 * @since Feb 22, 2015
 */
public class ConfigCrawler {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(ConfigCrawler.class);
	
	private Map<String, String> parameters;
	
	public ConfigCrawler() {
		parameters = new HashMap<String, String>();
	}
	
	public void crawl() throws IOException {
		URL url = new URL("http://www.runescape.com/k=3/l=en/jav_config.ws");

		try (InputStream is = new BufferedInputStream(url.openStream()); BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("codebase"))
					parameters.put("codebase", line.substring(9));
				else if (line.startsWith("initial_jar"))
					parameters.put("initial_jar", line.substring(12));
				else if (line.startsWith("param")) {
					String[] args;
					if (line.contains("halign")) {
						args = line.substring(6).split("=ha");
						parameters.put(args[0], "ha" + args[1]);
					} else if (line.contains("services")) {
						args = line.substring(6).split("=http");
						parameters.put(args[0], "http" + args[1]);
					} else {
						args = line.substring(6).split("=");
						if (args.length == 1)
							parameters.put(args[0], "");
						else
							parameters.put(args[0], args[1]);
					}
				}
			}
			is.close();
			reader.close();
		}
		logger.info("Crawled " + parameters.size() + " Config(s).");
	}
	

	/**
	 * Downloads and saves the gamepack. Also saves the parameters
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void download() throws FileNotFoundException, IOException {
		URL url = new URL(parameters.get("codebase") + parameters.get("initial_jar"));
		try (InputStream is = new BufferedInputStream(url.openStream()); OutputStream os = new BufferedOutputStream(new FileOutputStream("./de_obf/gamepack.jar"))) {

			int read;
			while ((read = is.read()) != -1) {
				os.write(read);
			}
			is.close();
			os.close();
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("./de_obf/parameters.txt"))) {
			for (String name : parameters.keySet()) {
				writer.write("client_parameters.put(\"" + name + "\", \"" + parameters.get(name) + "\");");
				writer.newLine();
			}
			writer.close();
		}
	}
	
	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}
	
}
