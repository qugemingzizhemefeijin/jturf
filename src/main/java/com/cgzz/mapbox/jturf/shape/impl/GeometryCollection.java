package com.cgzz.mapbox.jturf.shape.impl;

import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.cgzz.mapbox.jturf.shape.CollectionContainer;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeometryCollection implements CollectionContainer<Geometry> {

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

    public static GeometryCollection fromJson(String json) {
        return GeoJsonUtils.getGson().fromJson(json, GeometryCollection.class);
    }

    public static GeometryCollection geometryCollection(Geometry geometry) {
        return (GeometryCollection) geometry;
    }

    public List<Geometry> geometries() {
        return geometries;
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.GEOMETRY_COLLECTION;
    }

    @Override
    public int size() {
        return geometries.size();
    }

    @Override
    public Iterator<Geometry> iterator() {
        return geometries.iterator();
    }

    @Override
    public Geometry get(int index) {
        return geometries.get(index);
    }

    @Override
    public boolean isEmpty() {
        return geometries.isEmpty();
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(geometryType()).append("─────┤").append("\n");
        for (Geometry geometry : geometries) {
            buf.append(geometry.toViewCoordsString()).append("\n");
        }
        return buf.toString();
    }

    @Override
    public String toJson() {
        return GeoJsonUtils.getGson().toJson(this);
    }

    @Override
    public String toString() {
        return "GeometryCollection{" +
                "geometries=" + geometries +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof GeometryCollection) {
            GeometryCollection that = (GeometryCollection) obj;
            return this.geometries.equals(that.geometries());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode *= 1000003;
        hashCode ^= geometries.hashCode();
        return hashCode;
    }

}
