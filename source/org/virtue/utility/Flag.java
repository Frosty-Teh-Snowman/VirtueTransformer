package org.virtue.utility;

/**
 * @author : const_
 */
public class Flag {

    private boolean bool;

    public Flag() {
        bool = false;
    }

    public void flag() {
        bool = true;
    }

    public void unflag() {
        bool = false;
    }

    public boolean flagged() {
        return bool;
    }
}
