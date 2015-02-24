package org.virtue.deobfuscate.utility;

import java.util.Vector;

import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

/**
 * @Created Mar 10, 2010 at 7:51:37 PM
 * @author DarkStorm
 */
public class Util {
	private Util() {
	}

	public static boolean isInteger(String testString) {
		try {
			Integer.parseInt(testString);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String[] split(String str, String regex) {
		String replacer = new String(str.toCharArray());
		Vector<String> strings = new Vector<String>();
		while (replacer.contains(regex)) {
			int i = replacer.indexOf(regex);
			strings.add(replacer.substring(0, i));
			replacer = replacer.substring(i + regex.length());
		}
		strings.add(replacer);
		return strings.toArray(new String[] {});
	}

	public static Type getType(String className) {
		if (className.endsWith("]"))
			return getArrayType(className);
		else if (className.equals("boolean"))
			return Type.BOOLEAN;
		else if (className.equals("byte"))
			return Type.BYTE;
		else if (className.equals("short"))
			return Type.SHORT;
		else if (className.equals("int"))
			return Type.INT;
		else if (className.equals("long"))
			return Type.LONG;
		else if (className.equals("float"))
			return Type.FLOAT;
		else if (className.equals("double"))
			return Type.DOUBLE;
		else if (className.equals("char"))
			return Type.CHAR;
		else if (className.equals("void"))
			return Type.VOID;
		else if (className.equals("null"))
			return Type.NULL;
		return new ObjectType(className);
	}

	private static ArrayType getArrayType(String className) {
		String baseClassName = "";
		int dimensions = 0;
		for (char character : className.toCharArray())
			if (character == '[')
				dimensions++;
			else if (character != ']')
				baseClassName += character;
		return new ArrayType(baseClassName, dimensions);
	}
}
