package com.cgzz.mapbox.jturf.geojson.adapter.factory;

import com.google.gson.TypeAdapterFactory;

public abstract class GeoJsonAdapterFactory implements TypeAdapterFactory {

    public static TypeAdapterFactory create() {
        return new GeoJsonAdapterFactoryImpl();
    }

}
