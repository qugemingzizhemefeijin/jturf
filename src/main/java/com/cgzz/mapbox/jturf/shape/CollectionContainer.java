package com.cgzz.mapbox.jturf.shape;

import java.util.List;

public interface CollectionContainer<V extends Geometry> extends Geometry {

    List<Geometry> geometries();

    V deepClone();

}
