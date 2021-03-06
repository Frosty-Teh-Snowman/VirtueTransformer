package org.virtue.deobfuscation;

import java.util.Map;

import org.virtue.bytecode.element.ClassElement;

/**
 * @author : const_
 */
public abstract class Transformer {

    private int counter;
    private int counter_1;
    private int total;
    private long exec;

    public void execute(Map<String, ClassElement> map) {
        long start = System.currentTimeMillis();
        transform(map);
        exec = System.currentTimeMillis() - start;
    }

    public abstract void transform(Map<String, ClassElement> map);

    public String name() {
        return getClass().getSimpleName();//.substring(0, getClass().getSimpleName().indexOf("Transform"));
    }

    public int counter() {
        return counter;
    }
    
    public int counter_1() {
    	return counter_1;
    }

    public int total() {
        return total;
    }

    public abstract String result();

    public void add() {
        counter++;
        total++;
    }

    public void add_1() {
        counter_1++;
        total++;
    }
    
    public void tAdd() {
        total++;
    }

    public long exec() {
        return exec;
    }
}
