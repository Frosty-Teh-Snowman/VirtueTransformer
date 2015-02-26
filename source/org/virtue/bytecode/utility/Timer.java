package org.virtue.bytecode.utility;

/**
 * @author : const_
 */
public class Timer {

    private long start;

    public void start() {
        start = System.currentTimeMillis();
    }

    public long clock() {
        return System.currentTimeMillis() - start;
    }
}
