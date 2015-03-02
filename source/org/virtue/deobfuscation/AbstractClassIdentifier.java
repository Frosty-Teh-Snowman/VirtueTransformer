package org.virtue.deobfuscation;

import java.util.LinkedList;
import java.util.List;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.utility.Timer;
import org.virtue.utility.refactor.ClassMappingData;

/**
 * @author : const_
 */
public abstract class AbstractClassIdentifier {

    private ClassElement identified;
    private List<AbstractFieldIdentifier> fieldIdentifiers = new LinkedList<>();
    private long fieldExec;

    public abstract ClassElement identify();

    public void run() {
        identified = identify();
        if (!broken())
        	Injector.getContainer().getHookMap().addClass(new ClassMappingData(identified.name(), getClass().getSimpleName().substring(0, getClass().getSimpleName().indexOf("Identifier"))));
    }

    public void add(AbstractFieldIdentifier fieldIdentifier) {
        fieldIdentifiers.add(fieldIdentifier);
    }

    public void runFields() {
        Timer timer = new Timer();
        timer.start();
        for (AbstractFieldIdentifier fieldIdentifier : fieldIdentifiers) {
            fieldIdentifier.run();
        }
        fieldExec = timer.clock();
    }

    public String format() {
        StringBuilder builder = new StringBuilder("\t\t\t ");
        builder.append(getClass().getSimpleName().substring(0, getClass().getSimpleName().indexOf("Identifier")));
        if (broken()) {
            return builder.append(" is broken").toString();
        }
        builder.append(" identified as ").append(identified.name()).append("\n");
        int found = 0;
        for (AbstractFieldIdentifier fieldIdentifier : fieldIdentifiers) {
            Injector.totalFields++;
            if (!fieldIdentifier.broken()) {
                Injector.foundFields++;
                found++;
            }
            builder.append(fieldIdentifier.format()).append("\n");
        }
        builder.append("\t\t\t\t\t Identified ").append(found).append(" out of ")
                .append(fieldIdentifiers.size()).append(" seeds in ").append(fieldExec).append("ms.\n");
        return builder.toString();
    }

    public ClassElement identified() {
        return identified;
    }
    
    public List<AbstractFieldIdentifier> fields() {
    	return fieldIdentifiers;
    }

    public boolean broken() {
        return identified == null;
    }
}
