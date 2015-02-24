package org.virtue;

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

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.virtue.deobfuscate.indentify.ClassIdentifier;
import org.virtue.deobfuscate.transformer.Transformer;
import org.virtue.deobfuscate.utility.ClassVector;

public class Injector {
	
	private ClassVector classes;
	private List<String> entryNames;
	private final List<Transformer> transformers;
	private final List<ClassIdentifier> identifiers;
	
	public Injector() {
		classes = new ClassVector();
		entryNames = new ArrayList<String>();
		transformers = Collections.synchronizedList(new ArrayList<Transformer>());
		identifiers = Collections.synchronizedList(new ArrayList<ClassIdentifier>());
	}

	public void initialization(String path) {
		if (!classes.isEmpty())
			classes = new ClassVector();
		
		if (!entryNames.isEmpty())
			entryNames = new ArrayList<String>();
		
		/*try {
			classes.add(new ClassGen(new ClassParser("./build/classes/org/virtue/TestData.class").parse()));
		} catch (ClassFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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

	public void registerTransformer(Transformer trans) {
		transformers.add(trans);
	}

	public void registerTransformer(ClassIdentifier iden) {
		identifiers.add(iden);
	}
	
	public void transform() throws IOException {
		synchronized (transformers) {
			Iterator<Transformer> trans = transformers.iterator();
			while (trans.hasNext()) {
				Transformer transformer = trans.next();
				for (ClassGen classGen : classes) {
					transformer.transform(classGen);
				}
				transformer.finish();
			}
		}
		transformers.clear();
		
		saveToJar();
	}
	
	public void identify() throws IOException {
		synchronized (identifiers) {
			Iterator<ClassIdentifier> iden = identifiers.iterator();
			while (iden.hasNext()) {
				ClassIdentifier identifier = iden.next();
				for (ClassGen classGen : classes) {
					identifier.identify(classGen);
				}
			}
		}
		identifiers.clear();
	}

	private void saveToJar() throws IOException {
		FileOutputStream stream = new FileOutputStream(VirtueTransformer.getInstance().getDirectory() + (VirtueTransformer.getInstance().getTransformMode().equals(TransformMode.OBFUSCATE) ? "obfuscated.jar" : "deobfuscated.jar"));
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