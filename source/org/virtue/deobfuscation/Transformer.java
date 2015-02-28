package org.virtue.deobfuscation;

import java.util.List;

import org.virtue.Injector;
import org.virtue.bytecode.element.ClassElement;

/**
 * @author : const_
 */
public abstract class Transformer {

    private int counter;
    private int total;
    private long exec;

    protected Injector injector;
    
    public Transformer(Injector injector) {
    	this.injector = injector;
    }
    
    public void execute(List<ClassElement> elements) {
        long start = System.currentTimeMillis();
        transform(elements);
        exec = System.currentTimeMillis() - start;
    }

    public abstract void transform(List<ClassElement> elements);

    public String name() {
        return getClass().getSimpleName().substring(0, getClass().getSimpleName().indexOf("Transform"));
    }

    public int counter() {
        return counter;
    }

    public int total() {
        return total;
    }

    public abstract String result();

    public void add() {
        counter++;
        total++;
    }

    public void tAdd() {
        total++;
    }

    public long exec() {
        return exec;
    }
    
    protected Injector getInjector() {
    	return injector;
    }
}