package com.cgzz.mapbox.jturf.shape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 几何图形枚举
 */
public enum GeometryType {

    POINT("Point"),
    MULTI_POINT("MultiPoint"),
    POLYGON("Polygon"),
    MULTI_POLYGON("MultiPolygon"),
    LINE_STRING("LineString"),
    MULTI_LINE_STRING("MultiLineString"),
    GEOMETRY_COLLECTION("GeometryCollection"),
    FEATURE("Feature"),
    FEATURE_COLLECTION("FeatureCollection"),
    ;

    private static Map<String, GeometryType> typeMap;

    static {
        Map<String, GeometryType> typeMap = new HashMap<>();
        for (GeometryType t : GeometryType.values()) {
            typeMap.put(t.name, t);
        }

        GeometryType.typeMap = Collections.unmodifiableMap(typeMap);
    }

    public final String name;

    GeometryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static GeometryType getByName(String name) {
        return typeMap.get(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
