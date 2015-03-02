package org.virtue.deobfuscation.transformers.refactor;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.AbstractFieldIdentifier;
import org.virtue.deobfuscation.Transformer;
import org.virtue.utility.refactor.FieldMappingData;
import org.virtue.utility.refactor.MappingData;

/**
 * @author : const_
 */
public class FieldNameTransformer extends Transformer {

    @Override
    public void transform(Map<String, ClassElement> map) {
        elementLoop: for (ClassElement element : map.values()) {
        	if (element.name().equals("client")) {
        		tAdd();
        		continue;
        	}
        	
        	for (FieldElement field : element.fields()) {
        		if ((field.node().access & Opcodes.ACC_NATIVE) != 0) {
        			tAdd();
        			continue elementLoop;
        		}
        		
       			if (element.name().equals("di") || element.name().equalsIgnoreCase("Stream")) {
       				System.out.println(field.name());
       			}
        		
        		
               	for (AbstractClassIdentifier c_ident : Injector.classIdentifiers) {
               		for (AbstractFieldIdentifier f_ident : c_ident.fields()) {	
	               		if (!f_ident.broken() && f_ident.identified().name().equals(field.name())) {
	               			tAdd();
	               			continue elementLoop;
	               		}
               		}
            	}

            	add();
              	Injector.getContainer().getHookMap().addField(new FieldMappingData(element.name(), new MappingData(field.name(), "Field_" + counter()), field.desc(), ((field.access() & Opcodes.ACC_STATIC) != 0)));
        	}
        }
    }

    @Override
    public String result() {
        StringBuilder builder = new StringBuilder("\t\t\tExecuted ");
        builder.append(name()).append(" in ").append(exec()).append("ms\n\t\t\t\tRenamed ")
                .append(counter()).append(" Field(s)");
        return builder.toString();
    }
}
