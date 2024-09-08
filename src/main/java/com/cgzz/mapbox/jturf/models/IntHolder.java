package com.cgzz.mapbox.jturf.models;

import java.io.Serializable;

public final class IntHolder implements Serializable {

    public int value;

    public IntHolder() {
    }

    public IntHolder(int initial) {
        value = initial;
    }

    public static IntHolder newObjectHolder() {
        return new IntHolder();
    }

    public static IntHolder newObjectHolder(int val) {
        return new IntHolder(val);
    }

}
