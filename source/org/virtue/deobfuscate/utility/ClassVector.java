package org.virtue.deobfuscate.utility;

import java.util.Vector;

import org.apache.bcel.generic.ClassGen;

public class ClassVector extends Vector<ClassGen> {
	
	private static final long serialVersionUID = 8663919436007878267L;

	public ClassVector() {
	}

	public ClassGen getByName(String name) {
		if (name == null)
			return null;
		for (Object o : elementData) {
			if (o != null) {
				ClassGen cg = (ClassGen) o;
				if (cg.getClassName().equals(name))
					return cg;
			}
		}
		return null;
	}

	public boolean containsByName(String name) {
		if (name == null)
			return false;
		for (Object o : elementData) {
			if (o != null) {
				ClassGen cg = (ClassGen) o;
				if (cg.getClassName().equals(name))
					return true;
			}
		}
		return false;
	}

	public ClassGen getByInterface(String interfaceName) {
		if (interfaceName == null)
			return null;
		for (ClassGen classGen : this)
			for (String classInterfaceName : classGen.getInterfaceNames())
				if (interfaceName.equals(classInterfaceName))
					return classGen;
		return null;
	}
}
