package org.virtue.deobfuscate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.virtue.VirtueTransformer;
import org.virtue.deobfuscate.deobbers.Deobber;
import org.virtue.deobfuscate.util.ClassVector;

public class Injector {
	
	private List<String> entryNames;
	private ClassVector classes;
	private final List<Deobber> deobbers;

	public Injector() {
		classes = new ClassVector();
		entryNames = new ArrayList<String>();
		deobbers = Collections.synchronizedList(new ArrayList<Deobber>());
	}

	public void initialization(String path) {
		if (!classes.isEmpty())
			classes = new ClassVector();
		
		if (!entryNames.isEmpty())
			entryNames = new ArrayList<String>();
		
		try (JarInputStream in = new JarInputStream(new FileInputStream(path))) {
			JarEntry entry;
			while ((entry = in.getNextJarEntry()) != null) {
				entryNames.add(entry.getName());
				if (entry.getName().endsWith(".class")) {
					ClassParser entryClassParser = new ClassParser(in, entry.getName());
					JavaClass parsedClass = entryClassParser.parse();
					ClassGen classGen = new ClassGen(parsedClass);
					classes.add(classGen);
				}
			}
			in.close();
		} catch (RuntimeException exception) {
			throw exception;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void registerDeobber(Deobber deobber) {
		deobbers.add(deobber);
	}

	public void deobfuscate() throws IOException {
		synchronized (deobbers) {
			Iterator<Deobber> trans = deobbers.iterator();
			while (trans.hasNext()) {
				Deobber deobber = trans.next();
				for (ClassGen classGen : classes) {
					deobber.deob(classGen);
				}
				deobber.finish();
			}
		}
		deobbers.clear();
		
		saveToJar();
	}

	private void saveToJar() throws IOException {
		FileOutputStream stream = new FileOutputStream(VirtueTransformer.getInstance().getDirectory() + "deobfuscated.jar");
		JarOutputStream out = new JarOutputStream(stream);
		for (ClassGen classGen : classes) {
			JarEntry jarEntry = new JarEntry(classGen.getClassName().replace('.', '/') + ".class");
			out.putNextEntry(jarEntry);
			out.write(classGen.getJavaClass().getBytes());
		}
		out.close();
		stream.close();
	}

	public ClassVector getClasses() {
		return classes;
	}
}