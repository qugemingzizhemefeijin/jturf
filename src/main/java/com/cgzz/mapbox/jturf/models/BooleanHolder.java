package com.cgzz.mapbox.jturf.models;

public final class BooleanHolder {

    public boolean value;

    public BooleanHolder() {
    }

    public BooleanHolder(boolean initial) {
        value = initial;
    }

    public static BooleanHolder newBooleanHolder() {
        return new BooleanHolder();
    }

    public static BooleanHolder newBooleanHolder(boolean val) {
        return new BooleanHolder(val);
    }

}
