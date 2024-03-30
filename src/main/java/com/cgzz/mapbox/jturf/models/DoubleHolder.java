package com.cgzz.mapbox.jturf.models;

import java.io.Serializable;

public final class DoubleHolder implements Serializable {

    private static final long serialVersionUID = -7759897451979207829L;

    public double value;

    public DoubleHolder() {
    }

    public DoubleHolder(double initial) {
        value = initial;
    }

}
