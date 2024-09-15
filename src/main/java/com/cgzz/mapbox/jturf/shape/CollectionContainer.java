package com.cgzz.mapbox.jturf.shape;

import java.util.Iterator;
import java.util.List;

public interface CollectionContainer<V extends Geometry> extends Geometry {

    List<V> geometries();

    int size();

    Iterator<V> iterator();

    V get(int index);

    boolean isEmpty();

}
