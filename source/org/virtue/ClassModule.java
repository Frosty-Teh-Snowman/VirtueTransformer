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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kyle Friz
 * @since Feb 20, 2015
 */
public class ClassModule {

	/**
	 * The {@link Logger} instance
	 */
	private static Logger logger = LoggerFactory.getLogger(ClassModule.class);
	
	private Map<String, ClassNode> classes;
	
	public ClassModule() {
		this.classes = new HashMap<String, ClassNode>();
	}
	
	public void initialization(String path) {
		if (!classes.isEmpty())
			classes = new HashMap<String, ClassNode>();
		
		try {
			JarFile jar = new JarFile(new File(path));
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".class")) {
					ClassNode node = new ClassNode();
					ClassReader reader = new ClassReader(jar.getInputStream(jar.getEntry(entry.getName())));
					reader.accept(node, ClassReader.SKIP_FRAMES);
					
					classes.put(node.name, node);
				}
			}
			jar.close();
			logger.info("Loaded " + classes.size() + " class(es) into the class module.");
		} catch (IOException e) {
			logger.error("Error loading class module!", e);
		}
	}

	/**
	 * @return the classes
	 */
	public Map<String, ClassNode> getClasses() {
		return classes;
	}
}
