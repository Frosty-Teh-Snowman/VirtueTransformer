package org.virtue.deobfuscation.transformers.refactor;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.deobfuscation.Transformer;
import org.virtue.utility.refactor.ClassMappingData;
import org.virtue.utility.refactor.MappingData;
import org.virtue.utility.refactor.MethodMappingData;

/**
 * @author : const_
 */
public class MethodNameTransformer extends Transformer {

    @Override
    public void transform(Map<String, ClassElement> map) {
        elementLoop: for (ClassElement element : map.values()) {
        	if (element.name().equals("client")) {
        		tAdd();
        		continue;
        	}
        	if ((element.node().access & Opcodes.ACC_NATIVE) != 0) {
        		tAdd();
        		continue;
        	}
        	
        	for (MethodElement method : element.methods()) {
        		if ((method.node().access & Opcodes.ACC_NATIVE) != 0) {
        			tAdd();
        			continue elementLoop;
        		}
            	
            	add();
            	Injector.getContainer().getHookMap().addMethod(new MethodMappingData(element.name(), new MappingData(method.name(), "Method_" + counter()), method.desc(), method.member()));
        	}
        }
    }

    @Override
    public String result() {
        StringBuilder builder = new StringBuilder("\t\t\tExecuted ");
        builder.append(name()).append(" in ").append(exec()).append("ms\n\t\t\t\tRenamed ")
                .append(counter()).append(" Method(s)");
        return builder.toString();
    }
}
