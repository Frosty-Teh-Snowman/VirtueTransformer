package org.virtue.deobfuscation.transformers.refactor;

import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.bytecode.element.MethodElement;
import org.virtue.deobfuscation.AbstractClassIdentifier;
import org.virtue.deobfuscation.Transformer;
import org.virtue.utility.refactor.ClassMappingData;

/**
 * @author : const_
 */
public class ClassNameTransformer extends Transformer {

    @Override
    public void transform(Map<String, ClassElement> map) {
        elementLoop: for (ClassElement element : map.values()) {
        	if (element.name().equals("client") || element.name().equals("Client")) {
        		tAdd();
        		continue;
        	}
        	
        	for (AbstractClassIdentifier ident : Injector.classIdentifiers) {
        		if (ident.getClass().getSimpleName().substring(0, ident.getClass().getSimpleName().indexOf("Identifier")).equals(element.name())) {
        			tAdd();
        			continue elementLoop;
        		}
        	}
        	
        	for (MethodElement method : element.methods()) {
				/** As we dont want to rename any  main-classes */
				if (method.name().equals("main") && method.desc().equals("([Ljava/lang/String;)V") || method.name().equals("init") && element.superName().equals("java/applet/Applet"))
					continue elementLoop;
        	}
        	String name;
        	if ((element.node().access & Opcodes.ACC_INTERFACE) != 0) {
        		add_1();
        		name = "Interface_" + counter_1();
        	} else {
        		add();
        		name = "Class_" + counter();
        	}
        	Injector.getContainer().getHookMap().addClass(new ClassMappingData(element.name(), name));
        }
    }

    @Override
    public String result() {
        StringBuilder builder = new StringBuilder("\t\t\tExecuted ");
        builder.append(name()).append(" in ").append(exec()).append("ms\n\t\t\t\tRenamed ")
                .append(counter()).append(" Class(es) and ")
                .append(counter_1()).append(" Interface(s)");
        return builder.toString();
    }
}
