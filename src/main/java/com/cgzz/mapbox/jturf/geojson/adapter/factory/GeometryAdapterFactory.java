package com.cgzz.mapbox.jturf.geojson.adapter.factory;

import com.cgzz.mapbox.jturf.shape.*;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.google.gson.TypeAdapterFactory;

public abstract class GeometryAdapterFactory implements TypeAdapterFactory {

    private static TypeAdapterFactory geometryTypeFactory;

    public static TypeAdapterFactory create() {
        if (geometryTypeFactory == null) {
            geometryTypeFactory = RuntimeTypeAdapterFactory.of(Geometry.class, "type", true)
                    .registerSubtype(GeometryCollection.class, GeometryType.GEOMETRY_COLLECTION.getName())
                    .registerSubtype(Point.class, GeometryType.POINT.getName())
                    .registerSubtype(MultiPoint.class, GeometryType.MULTI_POINT.getName())
                    .registerSubtype(LineString.class, GeometryType.LINE_STRING.getName())
                    .registerSubtype(MultiLineString.class, GeometryType.MULTI_LINE_STRING.getName())
                    .registerSubtype(Polygon.class, GeometryType.POLYGON.getName())
                    .registerSubtype(MultiPolygon.class, GeometryType.MULTI_POLYGON.getName());
        }
        return geometryTypeFactory;
    }

}
