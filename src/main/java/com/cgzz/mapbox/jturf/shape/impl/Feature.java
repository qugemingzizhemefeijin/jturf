package com.cgzz.mapbox.jturf.shape.impl;

import com.cgzz.mapbox.jturf.exception.JTurfException;
import com.cgzz.mapbox.jturf.geojson.GeoJsonUtils;
import com.cgzz.mapbox.jturf.shape.AbstractGeometryProperties;
import com.cgzz.mapbox.jturf.shape.Geometry;
import com.cgzz.mapbox.jturf.shape.GeometryType;
import com.google.gson.JsonObject;

public final class Feature extends AbstractGeometryProperties implements Geometry {

    private final String id;

    private final Geometry geometry;

    Feature(String id, Geometry geometry, JsonObject properties) {
        super(properties);

        if (geometry == null) {
            throw new NullPointerException("Null geometry");
        }

        this.id = id;
        this.geometry = geometry;
    }

    public static Feature fromGeometry(Geometry geometry) {
        return fromGeometry(geometry, new JsonObject(), null);
    }

    public static Feature fromGeometry(Geometry geometry, JsonObject properties) {
        return fromGeometry(geometry, properties == null ? new JsonObject() : properties, null);
    }

    public static Feature fromGeometry(Geometry geometry, JsonObject properties, String id) {
        if (geometry instanceof Feature) {
            throw new JTurfException("feature can not save feature object");
        }

        return new Feature(id, geometry, properties == null ? new JsonObject() : properties);
    }

    public static Feature fromJson(String json) {
        Feature feature = GeoJsonUtils.getGson().fromJson(json, Feature.class);

        if (feature.geometry() instanceof Feature) {
            throw new JTurfException("feature can not save feature object");
        }

        if (feature.properties != null) {
            return feature;
        }

        return new Feature(feature.id(), feature.geometry(), new JsonObject());
    }

    public static Feature feature(Geometry g) {
        return (Feature) g;
    }

    public String id() {
        return id;
    }

    public Geometry geometry() {
        return geometry;
    }

    @Override
    public GeometryType geometryType() {
        return GeometryType.FEATURE;
    }

    @Override
    public String toViewCoordsString() {
        StringBuilder buf = new StringBuilder();
        buf.append("├───── ").append(geometryType()).append("─────┤").append("\n");
        buf.append("id{").append(id).append("}").append("\n");
        buf.append("properties{").append(properties).append("}").append("\n");
        buf.append(geometry.toViewCoordsString());
        return buf.toString();
    }

    @Override
    public String toJson() {
        return null;
    }

    @Override
    public String toString() {
        return "Feature{"
                + "id=" + id + ", "
                + "geometry=" + geometry + ", "
                + "properties=" + properties
                + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Feature) {
            Feature that = (Feature) obj;
            return (this.id == null ? that.id() == null : this.id.equals(that.id()))
                    && this.geometry.equals(that.geometry())
                    && (this.properties == null ? that.properties() == null : this.properties.equals(that.properties()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode *= 1000003;
        hashCode ^= (id == null) ? 0 : id.hashCode();
        hashCode *= 1000003;
        hashCode ^= geometry.hashCode();
        hashCode *= 1000003;
        hashCode ^= (properties == null) ? 0 : properties.hashCode();
        return hashCode;
    }

}
