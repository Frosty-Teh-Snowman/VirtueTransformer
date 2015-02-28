package org.virtue.utility;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.ClassNode;
import org.virtue.bytecode.element.ClassElement;
import org.virtue.utility.refactor.HookMap;
import org.virtue.utility.refactor.Refactorer;

/**
 * @author sc4re
 */
public class ClassContainer {

    /** The classnodes with package+name -> node naming **/
    protected final Map<String, ClassNode> nodeMap;
    protected Map<String, ClassElement> elementMap;
    protected HookMap hookMap;
    public final Map<URL, Map<String, byte[]>> resources;

    public ClassContainer(Collection<ClassElement> elements) {
        nodeMap = new HashMap<>();
        elementMap = new HashMap<>();
        hookMap = new HookMap();
        resources = new HashMap<>();
        for (ClassElement element : elements) {
        	elementMap.put(element.name(), element);
            nodeMap.put(element.name(), element.node());
        }
    }

    public final Map<String, ClassNode> getNodes() {
        return Collections.unmodifiableMap(nodeMap);
    }
    
    public Map<String, ClassElement> getElements() {
        return elementMap;
    }
    
    public HookMap getHookMap() {
    	return hookMap;
    }

    public void refactor() {
        Refactorer rf = new Refactorer(hookMap, this);
        rf.run();
    }

    public void relocate(String oldName, ClassElement element) {
        if (nodeMap.containsKey(oldName))
            nodeMap.remove(oldName);
        nodeMap.put(element.name(), element.node());
        if (elementMap.containsKey(oldName))
            elementMap.remove(oldName);
        elementMap.put(element.name(), element);
    }

    public void add(ClassContainer other) {
        if (other == null)
            return;
        nodeMap.putAll(other.getNodes());
        resources.putAll(other.resources);
    }

    public void addClass(ClassNode cn) {
        nodeMap.put(cn.name, cn);
    }
}
