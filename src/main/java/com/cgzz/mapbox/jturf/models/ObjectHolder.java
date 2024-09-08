package com.cgzz.mapbox.jturf.models;

import java.io.Serializable;

public final class ObjectHolder<T> implements Serializable {

    public T value;

    public ObjectHolder() {
    }

    public ObjectHolder(T initial) {
        value = initial;
    }

    public static <T> ObjectHolder<T> newObjectHolder() {
        return new ObjectHolder<>();
    }

    public static <T> ObjectHolder<T> newObjectHolder(T val) {
        return new ObjectHolder<>(val);
    }

}
