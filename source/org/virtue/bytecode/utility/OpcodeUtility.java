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
package org.virtue.bytecode.utility;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

/**
 * @author Kyle Friz
 * @since Feb 25, 2015
 */
public class OpcodeUtility {

	
	
    public static final Map<Integer, String> OPCODE_MAP = new HashMap<>();
    
    static {
        for (Field f : Opcodes.class.getFields()) {
            f.setAccessible(true);
            try {
                if (f.getName().contains("TOP") ||
                        f.getName().contains("INTEGER") ||
                        f.getName().contains("FLOAT") ||
                        f.getName().contains("DOUBLE") ||
                        f.getName().contains("LONG") ||
                        f.getName().contains("NULL") ||
                        f.getName().contains("THIS")) {
                    OPCODE_MAP.put((Integer) f.get(null), f.getName());
                    continue;
                }
                OPCODE_MAP.put(f.getInt(null), f.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
	
    public static String getDescFor(String desc) {
        char[] charArray = desc.toCharArray();
        int count = 0;
        for (char c : charArray) {
            if (c == ']') {
                count++;
            }
        }
        desc = desc.replaceAll("\\[\\]", "");
        String arrays = "";
        for (int i = 0; i < count; i++) {
            arrays += "[";
        }
        switch (desc) {
            case "float":
                return arrays + "F";
            case "int":
                return arrays + "I";
            case "boolean":
                return arrays + "Z";
            case "byte":
                return arrays + "B";
            case "short":
                return arrays + "S";
            case "long":
                return arrays + "J";
            case "double":
                return arrays + "D";
            case "void":
                return "V";
            case "char":
                return arrays + "C";
            case "String":
                return arrays + "Ljava/lang/String;";
            case "Object:":
                return arrays + "Ljava/lang/Object;";
        }
        //      if (Updater.getWrapper(desc) != null) {
        //        return arrays + "L" + desc + ";";
        //   }
        return null;
    }

    public static String stripDesc(String desc) {
        return desc.replaceAll("L", "").replaceAll(";", "").replaceAll("\\[", "");
    }

    public static int count(final String string, final String substring) {
        int count = 0;
        int idx = 0;
        while ((idx = string.indexOf(substring, idx)) != -1) {
            idx++;
            count++;
        }
        return count;
    }

    public static boolean isStandard(String desc) {
        return desc.contains("java") || desc.contains("[") &&
                desc.substring(desc.lastIndexOf('[') + 2).length() == 0 ||
                desc.length() == 1 && Character.isUpperCase(desc.charAt(0));
    }
    
}
