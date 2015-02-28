package org.virtue.deobfuscation;

import org.virtue.Injector;
import org.virtue.bytecode.element.FieldElement;
import org.virtue.utility.refactor.FieldMappingData;
import org.virtue.utility.refactor.MappingData;

/**
 * @author : const_
 */
public abstract class AbstractFieldIdentifier {

    private FieldElement identified;

    public abstract FieldElement identify();

    public void run() {
        identified = identify();
        if (!broken())
        	Injector.getContainer().getHookMap().addField(new FieldMappingData(identified.parent().name(), new MappingData(getClass().getSimpleName().substring(0, getClass().getSimpleName().indexOf("Identifier"))), identified.desc(), identified.member()));
    }

    public FieldElement identified() {
        return identified;
    }

    public String format() {
        StringBuilder builder = new StringBuilder("\t\t\t\t ");
        builder.append(String.valueOf(getClass().getSimpleName().charAt(0)).toLowerCase())
                .append(getClass().getSimpleName().substring(1));
        if (broken()) {
            return builder.append(" is broken").toString();
        }
        builder.append(" identified as ").append(identified.parent().name()).append(".").append(identified.name());
        return builder.toString();
    }

    public boolean broken() {
        return identified == null;
    }
}
