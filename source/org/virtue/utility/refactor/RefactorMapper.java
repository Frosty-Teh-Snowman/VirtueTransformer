package org.virtue.utility.refactor;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.commons.Remapper;

/**
 * @author sc4re
 */
public class RefactorMapper extends Remapper {

    protected final Map<String, ClassMappingData> sortedClasses;
    protected final Map<String, MethodMappingData> sortedMethods;
    protected final Map<String, FieldMappingData> sortedFields;

    public RefactorMapper(HookMap hookMap) {
        sortedClasses = new HashMap<>();
        sortedMethods = new HashMap<>();
        sortedFields = new HashMap<>();
        for(ClassMappingData hook : hookMap.getClasses()){
            if(hook.getObfuscatedName().contains("$"))
                continue;
            String obfuscatedName = hook.getObfuscatedName();
            String refactoredName = hook.getRefactoredName();
            sortedClasses.put(obfuscatedName, hook);
            sortedClasses.put(refactoredName, hook);
        }
        for (MethodMappingData hook : hookMap.getMethods()) {
            String obfuscatedName = hook.getMethodName().getObfuscatedName();
            String obfuscatedDesc = hook.getMethodDesc();
            String obfuscatedCname = hook.getMethodOwner();
            sortedMethods.put(obfuscatedCname + "$$$$" + obfuscatedName + "$$$$" + obfuscatedDesc, hook);
        }
        for (FieldMappingData hook : hookMap.getFields()) {
            String obfuscatedName = hook.getName().getObfuscatedName();
            String obfuscatedDesc = hook.getDesc();
            String obfuscatedCname = hook.getFieldOwner();
            sortedFields.put(obfuscatedCname + "$$$$" + obfuscatedName + "$$$$" + obfuscatedDesc, hook);
        }
    }

    @Override
    public String map(String type) {
        if (sortedClasses.containsKey(type))
            return sortedClasses.get(type).getRefactoredName();
        return type;
    }

    @Override
    public String mapFieldName(String owner, String name, String desc) {
        String obfKey = owner + "$$$$" + name + "$$$$" + desc;
        if (sortedFields.containsKey(obfKey))
            name = sortedFields.get(obfKey).getName().getRefactoredName();
        return name;
    }

    @Override
    public String mapMethodName(String owner, String name, String desc) {
        String obfKey = owner + "$$$$" + name + "$$$$" + desc;
        if (sortedMethods.containsKey(obfKey))
            name = sortedMethods.get(obfKey).getMethodName().getRefactoredName();
        return name;
    }
}
