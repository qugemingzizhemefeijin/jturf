package com.cgzz.mapbox.jturf.geojson.adapter.factory;

import com.cgzz.mapbox.jturf.shape.*;
import com.cgzz.mapbox.jturf.shape.impl.*;
import com.google.gson.TypeAdapterFactory;

public abstract class GeometryAdapterFactory implements TypeAdapterFactory {

    private static TypeAdapterFactory geometryTypeFactory;

    public static TypeAdapterFactory create() {
        if (geometryTypeFactory == null) {
            geometryTypeFactory = RuntimeTypeAdapterFactory.of(Geometry.class, "type", true)
                    .registerSubtype(GeometryCollection.class, GeometryType.GEOMETRY_COLLECTION.name)
                    .registerSubtype(Point.class, GeometryType.POINT.name)
                    .registerSubtype(MultiPoint.class, GeometryType.MULTI_POINT.name)
                    .registerSubtype(LineString.class, GeometryType.LINE_STRING.name)
                    .registerSubtype(MultiLineString.class, GeometryType.MULTI_LINE_STRING.name)
                    .registerSubtype(Polygon.class, GeometryType.POLYGON.name)
                    .registerSubtype(MultiPolygon.class, GeometryType.MULTI_POLYGON.name);
        }
        return geometryTypeFactory;
    }

}
