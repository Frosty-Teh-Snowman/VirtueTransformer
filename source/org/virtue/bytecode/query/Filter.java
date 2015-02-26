package org.virtue.bytecode.query;

/**
 * @author : const_
 */
public interface Filter<T> {

    public boolean accept(T obj);

}
