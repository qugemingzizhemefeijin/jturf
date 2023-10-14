package com.cgzz.mapbox.jturf.shape;

import com.cgzz.mapbox.jturf.exception.JTurfException;

import java.util.ArrayList;
import java.util.List;

public final class GeometryCollection extends GeometryProperties implements CollectionContainer<GeometryCollection> {

    private final List<Geometry> geometries;

    GeometryCollection(List<Geometry> geometries) {
        if (geometries == null) {
            throw new NullPointerException("Null geometries");
        }
        this.geometries = geometries;
    }

    public static GeometryCollection fromGeometries(List<Geometry> geometries) {
        return new GeometryCollection(geometries);
    }

    public static GeometryCollection fromGeometry(Geometry geometry) {
        List<Geometry> geometries = new ArrayList<>(1);
        geometries.add(geometry);

        return new GeometryCollection(geometries);
    }

    public List<Geometry> geometries() {
        return geometries;
    }

    @Override
    public GeometryCollection deepClone() {
        List<Geometry> newList = new ArrayList<>(geometries.size());
        for (Geometry geometry : geometries) {
            if (geometry instanceof CoordinateContainer) {
                CoordinateContainer<?, ? extends Geometry> coordinateContainer = (CoordinateContainer<?, ? extends Geometry>) geometry;
                newList.add(coordinateContainer.deepClone());
            } else if (geometry instanceof GeometryCollection) {
                newList.add(((GeometryCollection) geometry).deepClone());
            } else {
                throw new JTurfException("geometry not support deepClone");
            }
        }
        GeometryCollection gc = fromGeometries(newList);
        gc.properties = cloneProperties();

        return gc;
    }

    @Override
    public GeometryType type() {
        return GeometryType.GEOMETRY_COLLECTION;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(type()).append("─────┤").append("\n");
        for (Geometry geometry : geometries) {
            buf.append(geometry.toViewCoordsString()).append("\n");
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        return "GeometryCollection{" +
                "geometries=" + geometries +
                '}';
    }
}
